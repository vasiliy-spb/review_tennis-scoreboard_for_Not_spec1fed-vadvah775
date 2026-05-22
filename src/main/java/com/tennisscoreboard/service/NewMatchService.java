package com.tennisscoreboard.service;

import com.tennisscoreboard.model.OngoingMatch;
import com.tennisscoreboard.model.Player;
import com.tennisscoreboard.repository.PlayerRepository;
import com.tennisscoreboard.util.MatchStorage;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class NewMatchService {

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // Валидация имён игроков происходит внутри сервисного слоя, а не на "входе" в приложение.
        // Это не соответствует принципу быстрого отказа ("Fail Fast"):
        // Проверку корректности данных, пришедших от пользователя, следует проводить как можно раньше.
        // Валидация на уровне сервлета позволяет немедленно прервать обработку некорректного запроса и вернуть клиенту ошибку `400 Bad Request`.
        // Текущий подход заставляет приложение выполнять лишнюю работу, передавая заведомо невалидные данные дальше в сервисный слой.
        // Стоит перенести логику валидации в специальный класс-валидатор и запускать её из сервлета.

    // TODO: PlayerRepository стоит внедрять через конструктор, а не создавать в этом классе.

    // TODO: Класс отвечает за создание объекта текущего матча (доменной модели).
        // При этом он способствует смешению слоёв — сам использует зависимость от DAO и передаёт JPA Entity в доменную модель.
        // (см. файл "Принцип разделения ответственности (Separation of Concerns).md" в этом же пакете)
        // Этому классу не должна быть нужна зависимость PlayerRepository.

    private final PlayerRepository playerRepository = new PlayerRepository();

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;

    // Можно запретить использование цифр и разрешить использование апострофов
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\d\\s.-]+$");

    // Непоследовательное именование переменных (см. файл "service.md" в этом же пакете)
    // Метод может возвращать UUID, а клиентский код сам преобразует его в строковое представление, если там это будет нужно.
    public String createNewMatch(String playerOneName, String playerTwoName){
        validatePlayerName(playerOneName);
        validatePlayerName(playerTwoName);

        if (playerOneName.trim().equalsIgnoreCase(playerTwoName.trim())) {
            throw new IllegalArgumentException("Player names must be different");
        }

        // Запрещает игрокам с одинаковыми именами одновременно играть в теннис. Хотя это нормальная ситуация.
        if (checkPlayersBusy(playerOneName, playerTwoName)) {
            throw new IllegalArgumentException("This player is already busy");
        }

        Player player1 = playerRepository.getOrCreatePlayer(playerOneName);
        Player player2 = playerRepository.getOrCreatePlayer(playerTwoName);

        // Передаёт JPA Entity в доменную модель
        OngoingMatch match = new OngoingMatch(player1, player2);

        // TODO: MatchStorage стоит внедрять через конструктор, а не обращаться напрямую к статическому методу здесь.
        MatchStorage.put(match);

        // Из этого метода не понятно, как создаётся ID матча — можно подумать,
            // что MatchStorage присваивает его неявно, поскольку это обязанность класса,
            // который представляет хранилище матчей. Но сейчас матч сам присваивает себе ID.
            // Стоит перенести генерацию ID в сервис-хранилище и возвращать его из метода создания/добавления матча.
            // Так эта часть логики будет более явной.
        return match.getUUID();
    }

    private void validatePlayerName(String name){
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        String trimmed = name.trim();
        if (trimmed.length() < MIN_NAME_LENGTH) {
            throw new IllegalArgumentException("Name must be at least " + MIN_NAME_LENGTH + " characters");
        }
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Name cannot exceed " + MAX_NAME_LENGTH + " characters");
        }
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Name contains invalid characters. Only letters, digits, spaces, dots and hyphens are allowed.");
        }
    }

    private boolean checkPlayersBusy(String playerOneName, String playerTwoName){
        List<OngoingMatch> list = MatchStorage.getAll();
        for (OngoingMatch match : list) {

            // Тело блоков if всегда стоит оборачивать в {}
            // Имена игроков можно вынести в переменные
            if(match.getPlayer1().getName().equals(playerOneName)) return true;
            if(match.getPlayer1().getName().equals(playerTwoName)) return true;
            if(match.getPlayer2().getName().equals(playerOneName)) return true;
            if(match.getPlayer2().getName().equals(playerTwoName)) return true;
        }

        return false;

        // Возможно, будет лучше читаться, если записать тело метода так:
        /*
        return list.stream()
                .flatMap(match -> Stream.of(match.getPlayer1(), match.getPlayer2())) // Логику этого шага можно вынести во вспомогательный метод
                .map(Player::getName)
                .anyMatch(name -> name.equals(playerOneName) || name.equals(playerTwoName));
         */
    }

}
