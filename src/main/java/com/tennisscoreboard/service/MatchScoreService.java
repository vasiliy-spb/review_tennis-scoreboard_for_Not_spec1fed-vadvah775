package com.tennisscoreboard.service;

import com.tennisscoreboard.model.Match;
import com.tennisscoreboard.model.OngoingMatch;
import com.tennisscoreboard.model.Player;
import com.tennisscoreboard.repository.MatchRepository;

public class MatchScoreService {

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // TODO: MatchRepository (только в текущей реализации) стоит внедрять через конструктор, а не создавать в этом классе.

    // TODO: Класс отвечает за обработку счёта текущего матча (доменной модели), а также за работу с DAO и сохранение завершённого матча.
        // Это нарушает Принцип единой ответственности (SRP).
        // Также класс способствует смешению слоёв — сам использует зависимость от DAO и работает с JPA Entity.
        // (см. файл "Принцип разделения ответственности (Separation of Concerns).md" в этом же пакете)
        // Этому классу не должна быть нужна зависимость MatchRepository.
        // Логика сохранения завершённых матчей не должна быть в этом сервисе.

    // Все "магические числа" лучше вынести в именованные константы

    // Составные условия из if лучше выносить во вспомогательный метод с понятным названием.
        // Это улучшит читаемость кода и позволит переиспользовать повторяющиеся условия без дублирования кода.

    // TODO: Класс содержит в себе всю бизнес-логику по подсчёту очков, геймов и сетов.
        // Объект, которым он оперирует (`OngoingMatch`), является "анемичной" моделью —
        // простым контейнером данных практически без собственного поведения. Сервис напрямую читает и записывает его поля.
        // Это главная архитектурная проблема этой части логики. По этим причинам:
        //
        //  - Нарушение инкапсуляции: Данные (в `OngoingMatch`) и поведение (в `MatchScoreService`) полностью разделены.
            //  Любой другой сервис может так же напрямую изменить счёт матча, и объект `OngoingMatch` не сможет себя защитить.
        //  - Процедурный стиль: Вместо объектно-ориентированного подхода, где объекты сами управляют своим состоянием
            //  (и начисление очков происходит в духе `matchScore.pointWonBy(player)`), получается процедурный код,
            //  который манипулирует внешними структурами данных.
        //  - Жёсткая связанность (Tight Coupling) и низкая связность (Low Cohesion):
            //  Сервис тесно связан с внутренним устройством `OngoingMatch`. При этом логика,
            //  относящаяся к одному понятию (счёт), размазана по разным классам (модели и сервису).
        //  - Сложность тестирования: Чтобы протестировать один конкретный сценарий (например, переход от "ровно" к "преимуществу"),
            //  нужно разбираться во множестве `if` и переходов по методам. Это сложно и хрупко.
        //
        // Как исправить: Провести рефакторинг классов моделей с переходом к "богатой" доменной модели.

    // TODO: Часть логики в некоторых методах полностью дублируется для каждого игрока.
        // Это нарушение принципа DRY и следствие анемичности доменных моделей и признак процедурного стиля программирования.
        //
            // DRY (Don't Repeat Yourself) — принцип «Не повторяйся», это базовый принцип разработки,
            // суть которого сводится к минимизации дублирования информации и фрагментов кода.
        //
        // Эта проблема исчезнет сама после проведения декомпозиции предметной области и реализации богатых доменных моделей.

    // Стоит удалять комментарии (вроде того, что указан в следующей строке) из кода перед тем, как выполнять коммит

    private final MatchRepository matchRepository = new MatchRepository();

    // У полей отсутствуют явные модификаторы доступа.
    int winnerPoints;
    int loserPoints;
    int winnerGames;
    int loserGames;
    int winnerSets;
    int loserSets;

    private void setScore(OngoingMatch match, Long winnerId) {
        Player player1 = match.getPlayer1();
        Player player2 = match.getPlayer2();

        if (player1.getId().equals(winnerId)) {
            winnerPoints = match.getPointsPlayer1();
            loserPoints = match.getPointsPlayer2();
            winnerGames = match.getGamesPlayer1();
            loserGames = match.getGamesPlayer2();
            winnerSets = match.getSetsPlayer1();
            loserSets = match.getSetsPlayer2();
        } else if(player2.getId().equals(winnerId)){
            winnerPoints = match.getPointsPlayer2();
            loserPoints = match.getPointsPlayer1();
            winnerGames = match.getGamesPlayer2();
            loserGames = match.getGamesPlayer1();
            winnerSets = match.getSetsPlayer2();
            loserSets = match.getSetsPlayer1();
        } else {
            throw new IllegalArgumentException("winnerId doesn't belong to the match");
        }
    }

    private void savingScore(OngoingMatch match, Long winnerId) {
        Player player1 = match.getPlayer1();

        if (player1.getId().equals(winnerId)) {
            match.setPointsPlayer1(winnerPoints);
            match.setGamesPlayer1(winnerGames);
            match.setSetsPlayer1(winnerSets);
            match.setPointsPlayer2(loserPoints);
            match.setGamesPlayer2(loserGames);
            match.setSetsPlayer2(loserSets);
        } else {
            match.setPointsPlayer2(winnerPoints);
            match.setGamesPlayer2(winnerGames);
            match.setSetsPlayer2(winnerSets);
            match.setPointsPlayer1(loserPoints);
            match.setGamesPlayer1(loserGames);
            match.setSetsPlayer1(loserSets);
        }
    }

    public void addPoint(OngoingMatch match, Long winnerId) {
        setScore(match, winnerId);

        if (match.isTiebreak()) {
            winnerPoints++;

            // Составные условия из if лучше выносить во вспомогательный метод с понятным названием
            // TODO: Согласно ТЗ, для победы в тай-брейке нужно набрать минимум 7 очков и иметь преимущество в 2 очка.
            if (winnerPoints >= 6 && winnerPoints - loserPoints >= 2) {
                winGame(match, winnerId);
            }
        }else{

            // TODO: Обработка счёта в этом switch не отображает предметную область — реальный счёт в гейме.
            // В Java 17 можно использовать switch-выражения и стрелочный синтаксис.
            switch (winnerPoints) {
                case 0: winnerPoints = 1; break;
                case 1: winnerPoints = 2; break;
                case 2: winnerPoints = 3; break;
                case 3:

                    // TODO: Блок if-else находится внутри switch, который тоже находится в блоке if-else — такую логику сложно понимать, тестировать и поддерживать.
                    if (loserPoints <= 2){
                        winGame(match, winnerId);

                    // Стоит удалять комментарии (вроде того, что указан в следующей строке) из кода перед тем, как выполнять коммит
                    } else if (loserPoints == 3) { // deuce -> advantage
                        winnerPoints = 4;

                    // Стоит удалять комментарии (вроде того, что указан в следующей строке) из кода перед тем, как выполнять коммит
                    } else if (loserPoints == 4){ //advantage
                        loserPoints = 3;
                    }
                    break;

                // Стоит удалять комментарии (вроде того, что указан в следующей строке) из кода перед тем, как выполнять коммит
                case 4: // advantage -> winSet
                    winGame(match, winnerId);
                    break;
            }
        }

        savingScore(match, winnerId);
    }

    private void winGame(OngoingMatch match, Long winnerId) {
        winnerPoints = 0;
        loserPoints = 0;

        winnerGames++;

        if (match.isTiebreak()) {
            winSet(match, winnerId);
        }

        // Составные условия из if лучше выносить во вспомогательный метод с понятным названием
        if(winnerGames >= 6 && winnerGames - loserGames >= 2){
            winSet(match, winnerId);
        } else if (winnerGames == 6 && loserGames == 6) {
            match.setTiebreak(true);
        }
    }

    // TODO: Этот метод вызывается из блока if-else, который находится в методе, который вызывается из switch, который находится в блоке if-else.
        // Это чрезмерно сложная цепочка для обработки этой логики.
    private void winSet(OngoingMatch match, Long winnerId){
        winnerGames = 0;
        loserGames = 0;

        match.setTiebreak(false);

        winnerSets++;

        if(winnerSets == 2){
            match.setFinished(true);
            savingMatchInDB(match, winnerId);
        }
    }

    // Логика этого метода не должна находиться в классе, отвечающим за обработку счёта в текущем матче.
    private void savingMatchInDB(OngoingMatch ongoingMatch, Long winnerId){
        Match match = new Match();
        Player player1 = ongoingMatch.getPlayer1();
        Player player2 = ongoingMatch.getPlayer2();

        match.setPlayer1(player1);
        match.setPlayer2(player2);

        if (player1.getId().equals(winnerId)) {
            match.setWinner(player1);
        } else {
            match.setWinner(player2);
        }

        matchRepository.save(match);
    }
}
