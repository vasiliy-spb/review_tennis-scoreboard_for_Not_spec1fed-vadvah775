package com.tennisscoreboard.util;

import com.tennisscoreboard.model.OngoingMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchStorage {

    // Класс спроектирован как утилитный, но при этом не объявлен как final и имеет публичный конструктор.

    // TODO: Нет интерфейса для этого класса. Из-за этого будет невозможно перейти
        // на хранение завершённых матчей в БД без изменения классов, зависимых от этого хранилища.

    // Класс хранилища завершённых матчей лучше проектировать как обычный сервис/репозиторий — создавать интерфейс и реализации,
        // а не делать утилитным классом. Это позволит подменять реализацию для тестирования или при изменении способа хранения объектов.

    // TODO: Веб-приложения по своей природе являются многопоточными.
            // Поэтому стоит использовать потокобезопасную реализацию `Map`, специально предназначенную для многопоточной среды.
            // Лучшим выбором здесь является `java.util.concurrent.ConcurrentHashMap`.
    // Ключом должен быть UUID, а не его строковое представление.
    private static final Map<String, OngoingMatch> matches = new HashMap<>();

    // Этот метод должен присваивать матчу UUID (не обязательно передавать в сам объект матча) и возвращать его.
    public static void put(OngoingMatch match) {
        matches.put(match.getUUID(), match);
    }

    // По аналогии с репозиториями можно в этом методе возвращать Optional, чтобы из него никогда не возвращался null.
    // Лучше принимать объект UUID. Парсинг и валидация должна происходить на входе этих данных в приложение.
    public static OngoingMatch get(String uuid) {
        return matches.get(uuid);
    }

    public static List<OngoingMatch> getAll(){

        // Ещё более удачным решением было бы возвращать неизменяемый список: List.of(matches.values())
        return new ArrayList<>(matches.values());
    }

    // Лучше принимать объект UUID. Парсинг и валидация должна происходить на входе этих данных в приложение.
    public static void remove(String uuid) {
        matches.remove(uuid);
    }
}
