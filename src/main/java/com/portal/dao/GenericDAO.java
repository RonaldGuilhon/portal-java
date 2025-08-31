package com.portal.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Consumer;

/**
 * DAO genérico que implementa operações básicas de CRUD.
 * Utiliza JPA/Hibernate para persistência de dados.
 * @param <T> Tipo da entidade
 * @param <ID> Tipo do identificador da entidade
 */
public abstract class GenericDAO<T, ID extends Serializable> {
    
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("portal-noticias-pu");
    private Class<T> entityClass;
    
    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    /**
     * Obtém uma instância do EntityManager
     */
    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    /**
     * Salva uma nova entidade no banco de dados
     */
    public void save(T entity) {
        executeInTransactionVoid(em -> em.persist(entity));
    }
    
    /**
     * Atualiza uma entidade existente
     */
    public T update(T entity) {
        return executeInTransaction(em -> em.merge(entity));
    }
    
    /**
     * Remove uma entidade pelo ID
     */
    public void delete(ID id) {
        executeInTransactionVoid(em -> {
            Optional.ofNullable(em.find(entityClass, id))
                    .ifPresent(em::remove);
        });
    }
    
    /**
     * Busca uma entidade pelo ID
     */
    public Optional<T> findById(ID id) {
        return executeQuery(em -> Optional.ofNullable(em.find(entityClass, id)));
    }
    
    /**
     * Lista todas as entidades
     */
    public List<T> findAll() {
        return executeQuery(em -> {
            TypedQuery<T> query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
            return query.getResultList();
        });
    }
    
    /**
     * Conta o total de registros
     */
    public Long count() {
        return executeQuery(em -> {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class);
            return query.getSingleResult();
        });
    }
    
    /**
     * Executa uma operação em uma transação
     */
    protected void executeInTransactionVoid(Consumer<EntityManager> operation) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            operation.accept(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro na operação de transação: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Executa uma operação em uma transação com retorno
     */
    protected <R> R executeInTransaction(Function<EntityManager, R> operation) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            R result = operation.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro na operação de transação: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Executa uma consulta sem transação
     */
    protected <R> R executeQuery(Function<EntityManager, R> query) {
        EntityManager em = getEntityManager();
        try {
            return query.apply(em);
        } finally {
            em.close();
        }
    }
    
    /**
     * Fecha o EntityManagerFactory
     */
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
