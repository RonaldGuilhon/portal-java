package com.portal.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

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
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar entidade: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Atualiza uma entidade existente
     */
    public T update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T updatedEntity = em.merge(entity);
            em.getTransaction().commit();
            return updatedEntity;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao atualizar entidade: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Remove uma entidade pelo ID
     */
    public void delete(ID id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao excluir entidade: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Busca uma entidade pelo ID
     */
    public T findById(ID id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }
    
    /**
     * Lista todas as entidades
     */
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<T> query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Conta o total de registros
     */
    public Long count() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class);
            return query.getSingleResult();
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
