package com.portal.controller;

import com.portal.model.PerfilUsuario;
import com.portal.model.Usuario;
import com.portal.service.UsuarioService;
import com.portal.service.ServiceException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Controller para gerenciamento de usuários (apenas admin)
 */
@Named
@RequestScoped
public class UsuarioController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private UsuarioService usuarioService;
    
    @Inject
    private LoginController loginController;
    
    private Usuario usuario;
    private List<Usuario> usuarios;
    private Long usuarioId;
    
    @PostConstruct
    public void init() {
        usuario = new Usuario();
        carregarUsuarios();
    }
    
    /**
     * Carrega todos os usuários
     */
    public void carregarUsuarios() {
        usuarios = usuarioService.listarTodos();
    }
    
    /**
     * Prepara um novo usuário
     */
    public String novoUsuario() {
        usuario = new Usuario();
        return "/pages/admin/usuario-form.xhtml?faces-redirect=true";
    }
    
    /**
     * Carrega usuário para edição
     */
    public String editarUsuario() {
        return Optional.ofNullable(usuarioId)
            .flatMap(usuarioService::buscarPorId)
            .map(u -> {
                this.usuario = u;
                return "/pages/admin/usuario-form.xhtml?faces-redirect=true";
            })
            .orElse(null);
    }
    
    /**
     * Salva o usuário (novo ou editado)
     */
    public String salvar() {
        try {
            if (usuario.getId() != null) {
                // Edição
                usuarioService.atualizar(usuario);
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário atualizado com sucesso!");
            } else {
                // Novo usuário
                usuarioService.salvar(usuario);
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário criado com sucesso!");
            }
            
            return "/pages/admin/usuarios.xhtml?faces-redirect=true";
            
        } catch (ServiceException e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao salvar", e.getMessage());
            return null;
        }
    }
    
    /**
     * Exclui um usuário
     */
    public void excluir() {
        Optional.ofNullable(usuarioId)
            .ifPresent(id -> {
                try {
                    // Verifica se não está tentando excluir o próprio usuário
                    Optional<Long> loggedUserId = Optional.ofNullable(loginController.getUsuarioLogado())
                        .map(Usuario::getId);
                    
                    if (loggedUserId.isPresent() && loggedUserId.get().equals(id)) {
                        adicionarMensagem(FacesMessage.SEVERITY_ERROR, 
                            "Erro", "Você não pode excluir seu próprio usuário");
                    } else {
                        usuarioService.excluir(id);
                        carregarUsuarios();
                        adicionarMensagem(FacesMessage.SEVERITY_INFO, 
                            "Sucesso", "Usuário excluído com sucesso!");
                    }
                } catch (ServiceException e) {
                    adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao excluir", e.getMessage());
                }
            });
    }
    
    /**
     * Adiciona mensagem ao contexto JSF
     */
    private void adicionarMensagem(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
    
    /**
     * Cancela a operação e volta para listagem
     */
    public String cancelar() {
        return "/pages/admin/usuarios.xhtml?faces-redirect=true";
    }
    
    /**
     * Retorna os perfis disponíveis para seleção
     */
    public PerfilUsuario[] getPerfis() {
        return PerfilUsuario.values();
    }
    
    // Getters e Setters
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public List<Usuario> getUsuarios() {
        return usuarios;
    }
    
    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}