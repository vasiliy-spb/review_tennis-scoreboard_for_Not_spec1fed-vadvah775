package com.tennisscoreboard.repository;

import com.tennisscoreboard.model.Match;

import java.util.List;

public class MatchRepository extends BaseRepository<Match, Long> {

    // TODO: Нет интерфейса для этого класса. (см. файл "repository.md" в этом же пакете)

    // Для визуального разделения HQL запросов на строки лучше использовать текстовые блоки

    // Лучше вынести тексты HQL запросов в `private static final` константы и дать им понятные имена.

    // Название параметра "playerName" тоже лучше вынести в именованную константу

    // Для выполнения регистронезависимого поиска по имени можно использовать возможности базы данных — в H2 есть ключевое слово `ILIKE`.

    // Лучше иметь разные методы для выборки с фильтром по имени и без него, а также для подсчёта количества,
        // чем собирать эту логику в одном методе. Если правила фильтрации поменяются,
        // то нужно будет изменить/дописать только некоторые методы, оставив логику выборки без фильтра без изменений.

    public MatchRepository() {
        super(Match.class);
    }

    public List<Match> findMatchesWithPaginationAndFilter(int offset, int limit, String playerName) {
        return executeReadOnly(session -> {
            String hql = "SELECT m FROM Match m " +
                    "LEFT JOIN FETCH m.player1 " +
                    "LEFT JOIN FETCH m.player2 " +
                    "LEFT JOIN FETCH m.winner " +
                    "WHERE (:playerName is NULL OR " +
                    "LOWER(m.player1.name) LIKE LOWER(CONCAT('%', :playerName, '%')) OR " +
                    "LOWER(m.player2.name) LIKE LOWER(CONCAT('%', :playerName, '%'))) " +
                    "ORDER BY m.id DESC";
            return session.createQuery(hql, Match.class)
                    .setParameter("playerName", playerName == null || playerName.isBlank() ? null : playerName)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
        });
    }

    public long countMatchesWithFilter(String playerName) {
        return executeReadOnly(session -> {
            String hql = "SELECT COUNT(m) FROM Match m " +
                    "WHERE (:playerName IS NULL OR " +
                    "LOWER(m.player1.name) LIKE LOWER(CONCAT('%', :playerName, '%')) OR " +
                    "LOWER(m.player2.name) LIKE LOWER(CONCAT('%', :playerName, '%')))";
            return session.createQuery(hql, Long.class)
                    .setParameter("playerName", playerName == null || playerName.isBlank() ? null : playerName)
                    .uniqueResult();
        });
    }
}
