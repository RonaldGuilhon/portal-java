package com.portal.model;

/**
 * Enum que define os perfis de usuário do sistema.
 * ADMIN: Usuário com permissões administrativas (criar, editar, excluir notícias)
 * LEITOR: Usuário comum que apenas visualiza as notícias
 */
public enum PerfilUsuario {
    ADMIN("Administrador"),
    LEITOR("Leitor");
    
    private final String descricao;
    
    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}