package com.portal.dao;

import com.portal.model.Noticia;
import com.portal.model.Usuario;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * DAO específico para operações com a entidade Noticia.
 * Estende o GenericDAO e adiciona métodos específicos para notícias.
 */
public class NoticiaDAO extends GenericDAO<Noticia, Long> {
    
    public NoticiaDAO() {
        super(Noticia.class);
    }
    
    /**
     * Lista todas as notícias ordenadas por data de publicação (mais recentes primeiro)
     */
    public List<Noticia> findAllOrderByDate() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Noticia> query = em.createQuery(
                "SELECT n FROM Noticia n ORDER BY n.dataPublicacao DESC", Noticia.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Lista notícias de um autor específico
     */
    public List<Noticia> findByAutor(Usuario autor) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Noticia> query = em.createQuery(
                "SELECT n FROM Noticia n WHERE n.autor = :autor ORDER BY n.dataPublicacao DESC", Noticia.class);
            query.setParameter("autor", autor);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Busca notícias por título (busca parcial)
     */
    public List<Noticia> findByTitulo(String titulo) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Noticia> query = em.createQuery(
                "SELECT n FROM Noticia n WHERE LOWER(n.titulo) LIKE LOWER(:titulo) ORDER BY n.dataPublicacao DESC", Noticia.class);
            query.setParameter("titulo", "%" + titulo + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Lista as últimas N notícias
     */
    public List<Noticia> findLatest(int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Noticia> query = em.createQuery(
                "SELECT n FROM Noticia n ORDER BY n.dataPublicacao DESC", Noticia.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
