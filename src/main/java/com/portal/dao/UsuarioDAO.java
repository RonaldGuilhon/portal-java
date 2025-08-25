package com.portal.dao;

import com.portal.model.Usuario;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * DAO específico para operações com a entidade Usuario.
 * Estende o GenericDAO e adiciona métodos específicos para usuários.
 */
public class UsuarioDAO extends GenericDAO<Usuario, Long> {
    
    public UsuarioDAO() {
        super(Usuario.class);
    }
    
    /**
     * Busca um usuário pelo email
     */
    public Usuario findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
    
    /**
     * Autentica um usuário pelo email e senha
     */
    public Usuario authenticate(String email, String senha) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email AND u.senha = :senha", Usuario.class);
            query.setParameter("email", email);
            query.setParameter("senha", senha);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
    
    /**
     * Verifica se um email já está em uso
     */
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }
}
