package com.portal.controller;

import com.portal.model.Usuario;
import com.portal.service.UsuarioService;
import com.portal.service.ServiceException;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Controller responsável pela autenticação de usuários
 */
@Named
@SessionScoped
public class LoginController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private UsuarioService usuarioService;
    
    private String email;
    private String senha;
    private Usuario usuarioLogado;
    
    /**
     * Realiza o login do usuário
     */
    public String login() {
        try {
            usuarioLogado = usuarioService.autenticar(email, senha);
            
            if (usuarioLogado != null) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Login realizado com sucesso!", 
                        "Bem-vindo, " + usuarioLogado.getNome()));
                
                // Redireciona baseado no perfil
                if (usuarioLogado.getPerfil().name().equals("ADMIN")) {
                    return "/pages/admin/listar.xhtml?faces-redirect=true";
                } else {
                    return "/pages/public/home.xhtml?faces-redirect=true";
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Erro de autenticação", 
                        "Email ou senha inválidos"));
                return null;
            }
            
        } catch (ServiceException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erro no sistema", e.getMessage()));
            return null;
        }
    }
    
    /**
     * Realiza o logout do usuário
     */
    public String logout() {
        usuarioLogado = null;
        email = null;
        senha = null;
        
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, 
                "Logout realizado", 
                "Você foi desconectado com sucesso"));
        
        return "/pages/public/home.xhtml?faces-redirect=true";
    }
    
    /**
     * Verifica se o usuário está logado
     */
    public boolean isLogado() {
        return usuarioLogado != null;
    }
    
    /**
     * Verifica se o usuário é administrador
     */
    public boolean isAdmin() {
        return isLogado() && usuarioLogado.getPerfil().name().equals("ADMIN");
    }
    
    /**
     * Verifica se o usuário é editor
     */
    public boolean isEditor() {
        return isLogado() && (usuarioLogado.getPerfil().name().equals("ADMIN") || 
                             usuarioLogado.getPerfil().name().equals("EDITOR"));
    }
    
    // Getters e Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    
    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }
}