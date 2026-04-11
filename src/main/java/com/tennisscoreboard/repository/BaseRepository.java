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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DatabaseException("Error when performing an operation in a transaction");
        }
    }

    protected <R> R executeWithReturn(Function<Session, R> operation) {
        Transaction transaction = null;
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            R result = operation.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DatabaseException("Error when performing an operation in a transaction");
        }
    }

    protected <R> R executeReadOnly(Function<Session, R> operation) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            return operation.apply(session);
        } catch (Exception e) {
            throw new DatabaseException("Error when performing a read operation");
        }
    }

    public void save(T entity) {
        executeInTransaction(session -> session.persist(entity));
    }

    public T update(T entity) {
        return executeWithReturn(session -> session.merge(entity));
    }

    public void delete(T entity) {
        executeInTransaction(session -> session.remove(entity));
    }

    public Optional<T> findById(ID id) {
        return executeReadOnly(session ->
                Optional.ofNullable(session.find(entityClass, id)));
    }

    public List<T> findAll() {
        return executeReadOnly(session ->
                session.createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                        .list()
        );
    }

}





