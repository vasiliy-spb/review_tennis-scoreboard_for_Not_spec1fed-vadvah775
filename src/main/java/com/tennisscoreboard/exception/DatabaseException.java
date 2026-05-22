package com.tennisscoreboard.exception;

public class DatabaseException extends RuntimeException {

    // Стоит создать конструктор, который принимает в себя исходное исключение. Это упростит отладку в случае необходимости.

    public DatabaseException(String message) {
        super(message);
    }
}
