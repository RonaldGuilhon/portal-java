package com.portal.service;

import com.portal.dao.UsuarioDAO;
import com.portal.model.PerfilUsuario;
import com.portal.model.Usuario;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
        usuarioDAO.findByEmail(usuario.getEmail())
            .filter(existente -> !existente.getId().equals(usuario.getId()))
            .ifPresent(existente -> {
                throw new RuntimeException("Email já está em uso por outro usuário");
            });
        
        return usuarioDAO.update(usuario);
    }
    
    /**
     * Autentica um usuário
     */
    public Usuario autenticar(String email, String senha) throws ServiceException {
        validarCampoObrigatorio(email, "Email é obrigatório");
        validarCampoObrigatorio(senha, "Senha é obrigatória");
        
        String senhaCriptografada = criptografarSenha(senha);
        return usuarioDAO.authenticate(email, senhaCriptografada)
            .orElseThrow(() -> new ServiceException("Email ou senha inválidos"));
    }
    
    /**
     * Busca usuário por ID
     */
    public Optional<Usuario> buscarPorId(Long id) {
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
        usuarioDAO.findById(id)
            .orElseThrow(() -> new ServiceException("Usuário não encontrado"));
        
        usuarioDAO.delete(id);
    }
    
    /**
     * Busca usuário por email
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioDAO.findByEmail(email);
    }
    
    /**
     * Valida os dados do usuário
     */
    private void validarUsuario(Usuario usuario) throws ServiceException {
        Optional.ofNullable(usuario)
            .orElseThrow(() -> new ServiceException("Usuário não pode ser nulo"));
        
        validarCampoObrigatorio(usuario.getNome(), "Nome é obrigatório");
        validarCampoObrigatorio(usuario.getEmail(), "Email é obrigatório");
        validarCampoObrigatorio(usuario.getSenha(), "Senha é obrigatória");
        
        validarTamanhoSenha(usuario.getSenha());
        
        // Define perfil padrão se não informado
        Optional.ofNullable(usuario.getPerfil())
            .orElseGet(() -> {
                usuario.setPerfil(PerfilUsuario.LEITOR);
                return PerfilUsuario.LEITOR;
            });
    }
    
    /**
     * Valida se um campo obrigatório não está vazio
     */
    private void validarCampoObrigatorio(String campo, String mensagem) throws ServiceException {
        Optional.ofNullable(campo)
            .filter(s -> !s.isEmpty())
            .filter(s -> !s.trim().isEmpty())
            .orElseThrow(() -> new ServiceException(mensagem));
    }
    
    /**
     * Valida o tamanho mínimo da senha
     */
    private void validarTamanhoSenha(String senha) throws ServiceException {
        Optional.ofNullable(senha)
            .filter(s -> s.length() >= 6)
            .orElseThrow(() -> new ServiceException("Senha deve ter pelo menos 6 caracteres"));
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