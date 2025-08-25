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
        try {
            usuarios = usuarioService.listarTodos();
        } catch (ServiceException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erro ao carregar usuários", e.getMessage()));
        }
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
        try {
            if (usuarioId != null) {
                usuario = usuarioService.buscarPorId(usuarioId);
                return "/pages/admin/usuario-form.xhtml?faces-redirect=true";
            }
        } catch (ServiceException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erro ao carregar usuário", e.getMessage()));
        }
        return null;
    }
    
    /**
     * Salva o usuário (novo ou editado)
     */
    public String salvar() {
        try {
            if (usuario.getId() == null) {
                // Novo usuário
                usuarioService.salvar(usuario);
                
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Sucesso", "Usuário criado com sucesso!"));
            } else {
                // Edição
                usuarioService.atualizar(usuario);
                
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Sucesso", "Usuário atualizado com sucesso!"));
            }
            
            return "/pages/admin/usuarios.xhtml?faces-redirect=true";
            
        } catch (ServiceException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erro ao salvar", e.getMessage()));
            return null;
        }
    }
    
    /**
     * Exclui um usuário
     */
    public void excluir() {
        try {
            if (usuarioId != null) {
                // Não permite excluir o próprio usuário
                if (usuarioId.equals(loginController.getUsuarioLogado().getId())) {
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Erro", "Você não pode excluir seu próprio usuário"));
                    return;
                }
                
                usuarioService.excluir(usuarioId);
                carregarUsuarios(); // Recarrega a lista
                
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Sucesso", "Usuário excluído com sucesso!"));
            }
        } catch (ServiceException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erro ao excluir", e.getMessage()));
        }
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