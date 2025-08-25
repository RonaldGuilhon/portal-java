package com.portal.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Entidade JPA que representa um usuário do sistema.
 * Contém informações básicas do usuário e seu perfil de acesso.
 */
@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @NotNull(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @NotNull(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    @Column(name = "senha", nullable = false, length = 100)
    private String senha;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false)
    private PerfilUsuario perfil = PerfilUsuario.LEITOR;
    
    // Relacionamento com notícias (um usuário pode ter várias notícias)
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Noticia> noticias;
    
    // Construtores
    public Usuario() {}
    
    public Usuario(String nome, String email, String senha, PerfilUsuario perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
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
    
    public PerfilUsuario getPerfil() {
        return perfil;
    }
    
    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }
    
    public List<Noticia> getNoticias() {
        return noticias;
    }
    
    public void setNoticias(List<Noticia> noticias) {
        this.noticias = noticias;
    }
    
    // Métodos utilitários
    public boolean isAdmin() {
        return PerfilUsuario.ADMIN.equals(this.perfil);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return id != null && id.equals(usuario.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", perfil=" + perfil +
                '}';
    }
}