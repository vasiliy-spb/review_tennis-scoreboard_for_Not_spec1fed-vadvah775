package com.tennisscoreboard.repository;

import com.tennisscoreboard.exception.DatabaseException;
import com.tennisscoreboard.util.DatabaseManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BaseRepository<T, ID> {

    // TODO: Класс использует `sessionFactory.openSession()` для получения сессии. Это ведёт к антипаттерну "Session-per-Operation" ("сессия на операцию")
        // (см. файл "repository.md" в этом же пакете)

    // Класс напрямую вызывает статический метод `DatabaseManager.getSessionFactory()`, создавая жёсткую связь с утилитным классом.
        // Это делает невозможным юнит-тестирование классов репозиториев в изоляции.
        // Для теста придётся поднимать реальную сессию Hibernate, так как невозможно подменить `DatabaseManager` на тестовый объект (mock).
        // Стоит использовать внедрение зависимостей (Dependency Injection) и передавать `DatabaseManager`
        // (или `SessionFactory`) в конструктор репозитория.

    protected final Class<T> entityClass;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected void executeInTransaction(Consumer<Session> operation) {
        Transaction transaction = null;
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            operation.accept(session);
            transaction.commit();

        // TODO: Ловится слишком общее исключение. (см. файл "repository.md" в этом же пакете)
        } catch (Exception e) {

            // TODO: Перед откатом транзакции надо проверить, что она активна (isActive())
            if (transaction != null) {

                // TODO: Вызов `transaction.rollback()` не обёрнут в `try-catch`
                transaction.rollback();
            }
            throw new DatabaseException("Error when performing an operation in a transaction");
        }
    }

    // Можно тоже назвать executeInTransaction
    protected <R> R executeWithReturn(Function<Session, R> operation) {
        Transaction transaction = null;
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            R result = operation.apply(session);
            transaction.commit();
            return result;

        // TODO: Ловится слишком общее исключение. (см. файл "repository.md" в этом же пакете)
        } catch (Exception e) {

            // TODO: Перед откатом транзакции надо проверить, что она активна (isActive())
            if (transaction != null) {

                // TODO: Вызов `transaction.rollback()` не обёрнут в `try-catch`
                transaction.rollback();
            }
            throw new DatabaseException("Error when performing an operation in a transaction");
        }
    }

    // Можно тоже назвать просто execute
    protected <R> R executeReadOnly(Function<Session, R> operation) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            return operation.apply(session);

        // TODO: Ловится слишком общее исключение. (см. файл "repository.md" в этом же пакете)
        } catch (Exception e) {
            throw new DatabaseException("Error when performing a read operation");
        }
    }

    // Лучше возвращать из этого метода сохранённую сущность, тогда её использование в клиентском коде будет более явным.
    public void save(T entity) {
        executeInTransaction(session -> session.persist(entity));
    }

    // Функционал этого метода не используется в проекте, поэтому его можно удалить.
    public T update(T entity) {
        return executeWithReturn(session -> session.merge(entity));
    }

    // Функционал этого метода не используется в проекте, поэтому его можно удалить.
    public void delete(T entity) {
        executeInTransaction(session -> session.remove(entity));
    }

    // Функционал этого метода не используется в проекте, поэтому его можно удалить.
    public Optional<T> findById(ID id) {
        return executeReadOnly(session ->
                Optional.ofNullable(session.find(entityClass, id)));
    }

    // Функционал этого метода не используется в проекте, поэтому его можно удалить.
    public List<T> findAll() {
        return executeReadOnly(session ->
                session.createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                        .list()
        );
    }

}





