package com.portal.dao;

import com.portal.model.Noticia;
import com.portal.model.Usuario;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

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
        return executeQuery(em -> {
            TypedQuery<Noticia> query = em.createQuery(
                "SELECT n FROM Noticia n ORDER BY n.dataPublicacao DESC", Noticia.class);
            return query.getResultList();
        });
    }
    
    /**
     * Lista notícias de um autor específico
     */
    public List<Noticia> findByAutor(Usuario autor) {
        return executeQuery(em -> {
            TypedQuery<Noticia> query = em.createQuery(
                "SELECT n FROM Noticia n WHERE n.autor = :autor ORDER BY n.dataPublicacao DESC", Noticia.class);
            query.setParameter("autor", autor);
            return query.getResultList();
        });
    }
    
    /**
     * Busca notícias por título (busca parcial)
     */
    public List<Noticia> findByTitulo(String titulo) {
        return executeQuery(em -> {
            TypedQuery<Noticia> query = em.createQuery(
                "SELECT n FROM Noticia n WHERE LOWER(n.titulo) LIKE LOWER(:titulo) ORDER BY n.dataPublicacao DESC", Noticia.class);
            query.setParameter("titulo", "%" + titulo + "%");
            return query.getResultList();
        });
    }
    
    /**
     * Lista as últimas N notícias
     */
    public List<Noticia> findLatest(int limit) {
        return executeQuery(em -> {
            TypedQuery<Noticia> query = em.createQuery(
                "SELECT n FROM Noticia n ORDER BY n.dataPublicacao DESC", Noticia.class);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }
    
    /**
     * Busca notícias por palavras-chave no título ou conteúdo
     */
    public List<Noticia> findByKeywords(String keywords) {
        return executeQuery(em -> {
            TypedQuery<Noticia> query = em.createQuery(
                "SELECT n FROM Noticia n WHERE LOWER(n.titulo) LIKE LOWER(:keywords) " +
                "OR LOWER(n.conteudo) LIKE LOWER(:keywords) ORDER BY n.dataPublicacao DESC", Noticia.class);
            query.setParameter("keywords", "%" + keywords + "%");
            return query.getResultList();
        });
    }
}
