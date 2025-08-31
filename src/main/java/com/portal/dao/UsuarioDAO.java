package com.portal.dao;

import com.portal.model.Usuario;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

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
    public Optional<Usuario> findByEmail(String email) {
        return executeQuery(em -> {
            try {
                TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
                query.setParameter("email", email);
                return Optional.of(query.getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }
    
    /**
     * Autentica um usuário pelo email e senha
     */
    public Optional<Usuario> authenticate(String email, String senha) {
        return executeQuery(em -> {
            try {
                TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.email = :email AND u.senha = :senha", Usuario.class);
                query.setParameter("email", email);
                query.setParameter("senha", senha);
                return Optional.of(query.getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }
    
    /**
     * Verifica se um email já está em uso
     */
    public boolean emailExists(String email) {
        return findByEmail(email).isPresent();
    }
}
