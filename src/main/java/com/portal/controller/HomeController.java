package com.portal.controller;

import com.portal.model.Noticia;
import com.portal.service.NoticiaService;

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
 * Controller para a página pública (home)
 */
@Named
@RequestScoped
public class HomeController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private NoticiaService noticiaService;
    
    private List<Noticia> ultimasNoticias;
    private Noticia noticiaDetalhes;
    private Long noticiaId;
    private String termoBusca;
    private List<Noticia> resultadosBusca;
    
    @PostConstruct
    public void init() {
        carregarUltimasNoticias();
    }
    
    /**
     * Carrega as últimas notícias para exibição na home
     */
    public void carregarUltimasNoticias() {
        ultimasNoticias = noticiaService.listarUltimas(10);
    }
    
    /**
     * Carrega detalhes de uma notícia específica
     */
    public String verNoticia() {
        if (noticiaId != null) {
            Optional<Noticia> noticiaOpt = noticiaService.buscarPorId(noticiaId);
            if (noticiaOpt.isPresent()) {
                noticiaDetalhes = noticiaOpt.get();
                return "/pages/public/noticia.xhtml?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Erro", "Notícia não encontrada"));
            }
        }
        return null;
    }
    
    /**
     * Realiza busca por título
     */
    public void buscarNoticias() {
        if (termoBusca != null && !termoBusca.trim().isEmpty()) {
            resultadosBusca = noticiaService.buscarPorTitulo(termoBusca.trim());
            
            if (resultadosBusca.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Busca", "Nenhuma notícia encontrada para: " + termoBusca));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, 
                    "Atenção", "Digite um termo para buscar"));
        }
    }
    
    /**
     * Limpa os resultados da busca
     */
    public void limparBusca() {
        termoBusca = null;
        resultadosBusca = null;
    }
    
    /**
     * Formata o resumo da notícia (primeiros 200 caracteres)
     */
    public String getResumo(String conteudo) {
        if (conteudo != null && conteudo.length() > 200) {
            return conteudo.substring(0, 200) + "...";
        }
        return conteudo;
    }
    
    // Getters e Setters
    public List<Noticia> getUltimasNoticias() {
        return ultimasNoticias;
    }
    
    public void setUltimasNoticias(List<Noticia> ultimasNoticias) {
        this.ultimasNoticias = ultimasNoticias;
    }
    
    public Noticia getNoticiaDetalhes() {
        return noticiaDetalhes;
    }
    
    public void setNoticiaDetalhes(Noticia noticiaDetalhes) {
        this.noticiaDetalhes = noticiaDetalhes;
    }
    
    public Long getNoticiaId() {
        return noticiaId;
    }
    
    public void setNoticiaId(Long noticiaId) {
        this.noticiaId = noticiaId;
    }
    
    public String getTermoBusca() {
        return termoBusca;
    }
    
    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }
    
    public List<Noticia> getResultadosBusca() {
        return resultadosBusca;
    }
    
    public void setResultadosBusca(List<Noticia> resultadosBusca) {
        this.resultadosBusca = resultadosBusca;
    }
}