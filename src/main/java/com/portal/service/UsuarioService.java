package com.portal.service;

import com.portal.dao.UsuarioDAO;
import com.portal.model.PerfilUsuario;
import com.portal.model.Usuario;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Serviço para operações de negócio relacionadas a usuários.
 * Contém validações e regras de negócio específicas.
 */
@ApplicationScoped
public class UsuarioService {
    
    @Inject
    private UsuarioDAO usuarioDAO;
    
    /**
     * Salva um novo usuário com validações
     */
    public void salvar(Usuario usuario) throws ServiceException {
        validarUsuario(usuario);
        
        // Verifica se email já existe
        if (usuarioDAO.emailExists(usuario.getEmail())) {
            throw new ServiceException("Email já está em uso por outro usuário");
        }
        
        // Criptografa a senha
        usuario.setSenha(criptografarSenha(usuario.getSenha()));
        
        usuarioDAO.save(usuario);
    }
    
    /**
     * Atualiza um usuário existente
     */
    public Usuario atualizar(Usuario usuario) throws ServiceException {
        validarUsuario(usuario);
        
        // Verifica se email já existe para outro usuário
        Usuario usuarioExistente = usuarioDAO.findByEmail(usuario.getEmail());
        if (usuarioExistente != null && !usuarioExistente.getId().equals(usuario.getId())) {
            throw new ServiceException("Email já está em uso por outro usuário");
        }
        
        return usuarioDAO.update(usuario);
    }
    
    /**
     * Autentica um usuário
     */
    public Usuario autenticar(String email, String senha) throws ServiceException {
        if (email == null || email.trim().isEmpty()) {
            throw new ServiceException("Email é obrigatório");
        }
        
        if (senha == null || senha.trim().isEmpty()) {
            throw new ServiceException("Senha é obrigatória");
        }
        
        String senhaCriptografada = criptografarSenha(senha);
        Usuario usuario = usuarioDAO.authenticate(email, senhaCriptografada);
        
        if (usuario == null) {
            throw new ServiceException("Email ou senha inválidos");
        }
        
        return usuario;
    }
    
    /**
     * Busca usuário por ID
     */
    public Usuario buscarPorId(Long id) {
        return usuarioDAO.findById(id);
    }
    
    /**
     * Lista todos os usuários
     */
    public List<Usuario> listarTodos() {
        return usuarioDAO.findAll();
    }
    
    /**
     * Exclui um usuário
     */
    public void excluir(Long id) throws ServiceException {
        Usuario usuario = usuarioDAO.findById(id);
        if (usuario == null) {
            throw new ServiceException("Usuário não encontrado");
        }
        
        usuarioDAO.delete(id);
    }
    
    /**
     * Valida os dados do usuário
     */
    private void validarUsuario(Usuario usuario) throws ServiceException {
        if (usuario == null) {
            throw new ServiceException("Usuário não pode ser nulo");
        }
        
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new ServiceException("Nome é obrigatório");
        }
        
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new ServiceException("Email é obrigatório");
        }
        
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new ServiceException("Senha é obrigatória");
        }
        
        if (usuario.getSenha().length() < 6) {
            throw new ServiceException("Senha deve ter pelo menos 6 caracteres");
        }
        
        if (usuario.getPerfil() == null) {
            usuario.setPerfil(PerfilUsuario.LEITOR);
        }
    }
    
    /**
     * Criptografa a senha usando SHA-256
     */
    private String criptografarSenha(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar senha", e);
        }
    }
}