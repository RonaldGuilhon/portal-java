package com.portal.service;

import com.portal.dao.NoticiaDAO;
import com.portal.model.Noticia;
import com.portal.model.Usuario;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço para operações de negócio relacionadas a notícias.
 * Contém validações e regras de negócio específicas.
 */
@ApplicationScoped
public class NoticiaService {
    
    @Inject
    private NoticiaDAO noticiaDAO;
    
    /**
     * Salva uma nova notícia com validações
     */
    public void salvar(Noticia noticia) throws ServiceException {
        validarNoticia(noticia);
        
        // Define a data de publicação se não foi definida
        if (noticia.getDataPublicacao() == null) {
            noticia.setDataPublicacao(LocalDateTime.now());
        }
        
        noticiaDAO.save(noticia);
    }
    
    /**
     * Atualiza uma notícia existente
     */
    public Noticia atualizar(Noticia noticia) throws ServiceException {
        validarNoticia(noticia);
        
        Noticia noticiaExistente = noticiaDAO.findById(noticia.getId());
        if (noticiaExistente == null) {
            throw new ServiceException("Notícia não encontrada");
        }
        
        return noticiaDAO.update(noticia);
    }
    
    /**
     * Busca notícia por ID
     */
    public Noticia buscarPorId(Long id) {
        return noticiaDAO.findById(id);
    }
    
    /**
     * Lista todas as notícias ordenadas por data
     */
    public List<Noticia> listarTodas() {
        return noticiaDAO.findAllOrderByDate();
    }
    
    /**
     * Lista notícias de um autor específico
     */
    public List<Noticia> listarPorAutor(Usuario autor) {
        return noticiaDAO.findByAutor(autor);
    }
    
    /**
     * Busca notícias por título
     */
    public List<Noticia> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return listarTodas();
        }
        return noticiaDAO.findByTitulo(titulo.trim());
    }
    
    /**
     * Lista as últimas notícias
     */
    public List<Noticia> listarUltimas(int limite) {
        if (limite <= 0) {
            limite = 10; // Padrão
        }
        return noticiaDAO.findLatest(limite);
    }
    
    /**
     * Exclui uma notícia
     */
    public void excluir(Long id, Usuario usuarioLogado) throws ServiceException {
        Noticia noticia = noticiaDAO.findById(id);
        if (noticia == null) {
            throw new ServiceException("Notícia não encontrada");
        }
        
        // Verifica se o usuário pode excluir a notícia
        if (!podeExcluir(noticia, usuarioLogado)) {
            throw new ServiceException("Você não tem permissão para excluir esta notícia");
        }
        
        noticiaDAO.delete(id);
    }
    
    /**
     * Verifica se o usuário pode editar a notícia
     */
    public boolean podeEditar(Noticia noticia, Usuario usuario) {
        if (usuario == null || noticia == null) {
            return false;
        }
        
        // Admin pode editar qualquer notícia
        if (usuario.isAdmin()) {
            return true;
        }
        
        // Autor pode editar sua própria notícia
        return noticia.getAutor() != null && noticia.getAutor().getId().equals(usuario.getId());
    }
    
    /**
     * Verifica se o usuário pode excluir a notícia
     */
    public boolean podeExcluir(Noticia noticia, Usuario usuario) {
        return podeEditar(noticia, usuario); // Mesma regra de edição
    }
    
    /**
     * Valida os dados da notícia
     */
    private void validarNoticia(Noticia noticia) throws ServiceException {
        if (noticia == null) {
            throw new ServiceException("Notícia não pode ser nula");
        }
        
        if (noticia.getTitulo() == null || noticia.getTitulo().trim().isEmpty()) {
            throw new ServiceException("Título é obrigatório");
        }
        
        if (noticia.getTitulo().length() < 5) {
            throw new ServiceException("Título deve ter pelo menos 5 caracteres");
        }
        
        if (noticia.getConteudo() == null || noticia.getConteudo().trim().isEmpty()) {
            throw new ServiceException("Conteúdo é obrigatório");
        }
        
        if (noticia.getConteudo().length() < 10) {
            throw new ServiceException("Conteúdo deve ter pelo menos 10 caracteres");
        }
        
        if (noticia.getAutor() == null) {
            throw new ServiceException("Autor é obrigatório");
        }
    }
}