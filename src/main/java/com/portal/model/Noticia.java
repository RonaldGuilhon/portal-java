package com.portal.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidade JPA que representa uma notícia do portal.
 * Contém título, conteúdo, data de publicação e referência ao autor.
 */
@Entity
@Table(name = "noticias")
public class Noticia implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Título é obrigatório")
    @Size(min = 5, max = 200, message = "Título deve ter entre 5 e 200 caracteres")
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;
    
    @NotNull(message = "Conteúdo é obrigatório")
    @Size(min = 10, message = "Conteúdo deve ter pelo menos 10 caracteres")
    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;
    
    @Column(name = "data_publicacao", nullable = false)
    private LocalDateTime dataPublicacao;
    
    // Relacionamento com usuário (muitas notícias para um usuário)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;
    
    // Construtores
    public Noticia() {
        this.dataPublicacao = LocalDateTime.now();
    }
    
    public Noticia(String titulo, String conteudo, Usuario autor) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.autor = autor;
        this.dataPublicacao = LocalDateTime.now();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getConteudo() {
        return conteudo;
    }
    
    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
    
    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }
    
    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }
    
    public Usuario getAutor() {
        return autor;
    }
    
    public void setAutor(Usuario autor) {
        this.autor = autor;
    }
    
    // Métodos utilitários
    public String getDataPublicacaoFormatada() {
        if (dataPublicacao != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return dataPublicacao.format(formatter);
        }
        return "";
    }
    
    public String getNomeAutor() {
        return autor != null ? autor.getNome() : "Autor desconhecido";
    }
    
    public String getResumo() {
        if (conteudo != null && conteudo.length() > 150) {
            return conteudo.substring(0, 150) + "...";
        }
        return conteudo;
    }
    
    @PrePersist
    protected void onCreate() {
        if (dataPublicacao == null) {
            dataPublicacao = LocalDateTime.now();
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Noticia noticia = (Noticia) obj;
        return id != null && id.equals(noticia.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "Noticia{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", dataPublicacao=" + dataPublicacao +
                ", autor=" + (autor != null ? autor.getNome() : "null") +
                '}';
    }
}