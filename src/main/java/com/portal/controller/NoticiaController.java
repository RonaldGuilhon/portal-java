package com.portal.controller;

import com.portal.model.Noticia;
import com.portal.service.NoticiaService;
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

/**
 * Controller para gerenciamento de notícias na área administrativa
 */
@Named
@RequestScoped
public class NoticiaController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private NoticiaService noticiaService;
    
    @Inject
    private LoginController loginController;
    
    private Noticia noticia;
    private List<Noticia> noticias;
    private Long noticiaId;
    
    @PostConstruct
    public void init() {
        noticia = new Noticia();
        carregarNoticias();
    }
    
    /**
     * Carrega todas as notícias
     */
    public void carregarNoticias() {
        noticias = noticiaService.listarTodas();
    }
    
    /**
     * Prepara uma nova notícia
     */
    public String novaNoticia() {
        noticia = new Noticia();
        noticia.setAutor(loginController.getUsuarioLogado());
        return "/pages/admin/form.xhtml?faces-redirect=true";
    }
    
    /**
     * Carrega notícia para edição
     */
    public String editarNoticia() {
        if (noticiaId != null) {
            Optional<Noticia> noticiaOpt = noticiaService.buscarPorId(noticiaId);
            if (!noticiaOpt.isPresent()) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Erro", "Notícia não encontrada"));
                return null;
            }
            noticia = noticiaOpt.get();
            
            // Verifica permissão de edição
            if (!noticiaService.podeEditar(noticia, loginController.getUsuarioLogado())) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Acesso negado", 
                        "Você não tem permissão para editar esta notícia"));
                return null;
            }
            
            return "/pages/admin/form.xhtml?faces-redirect=true";
        }
        return null;
    }
    
    /**
     * Salva a notícia (nova ou editada)
     */
    public String salvar() {
        try {
            if (noticia.getId() == null) {
                // Nova notícia
                noticia.setAutor(loginController.getUsuarioLogado());
                noticiaService.salvar(noticia);
                
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Sucesso", "Notícia criada com sucesso!"));
            } else {
                // Edição
                noticiaService.atualizar(noticia);
                
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Sucesso", "Notícia atualizada com sucesso!"));
            }
            
            return "/pages/admin/listar.xhtml?faces-redirect=true";
            
        } catch (ServiceException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erro ao salvar", e.getMessage()));
            return null;
        }
    }
    
    /**
     * Exclui uma notícia
     */
    public void excluir() {
        try {
            if (noticiaId != null) {
                Optional<Noticia> noticiaOpt = noticiaService.buscarPorId(noticiaId);
                if (!noticiaOpt.isPresent()) {
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Erro", "Notícia não encontrada"));
                    return;
                }
                Noticia noticiaParaExcluir = noticiaOpt.get();
                
                // Verifica permissão de exclusão
                if (!noticiaService.podeExcluir(noticiaParaExcluir, loginController.getUsuarioLogado())) {
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Acesso negado", 
                            "Você não tem permissão para excluir esta notícia"));
                    return;
                }
                
                noticiaService.excluir(noticiaId, loginController.getUsuarioLogado());
                carregarNoticias(); // Recarrega a lista
                
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Sucesso", "Notícia excluída com sucesso!"));
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
        return "/pages/admin/listar.xhtml?faces-redirect=true";
    }
    
    // Getters e Setters
    public Noticia getNoticia() {
        return noticia;
    }
    
    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
    }
    
    public List<Noticia> getNoticias() {
        return noticias;
    }
    
    public void setNoticias(List<Noticia> noticias) {
        this.noticias = noticias;
    }
    
    public Long getNoticiaId() {
        return noticiaId;
    }
    
    public void setNoticiaId(Long noticiaId) {
        this.noticiaId = noticiaId;
    }
}