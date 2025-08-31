package com.portal.service;

import com.portal.dao.NoticiaDAO;
import com.portal.model.Noticia;
import com.portal.model.Usuario;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        Optional.ofNullable(noticia.getDataPublicacao())
            .orElseGet(() -> {
                LocalDateTime agora = LocalDateTime.now();
                noticia.setDataPublicacao(agora);
                return agora;
            });
        
        noticiaDAO.save(noticia);
    }
    
    /**
     * Atualiza uma notícia existente
     */
    public Noticia atualizar(Noticia noticia) throws ServiceException {
        validarNoticia(noticia);
        
        noticiaDAO.findById(noticia.getId())
            .orElseThrow(() -> new ServiceException("Notícia não encontrada"));
        
        return noticiaDAO.update(noticia);
    }
    
    /**
     * Busca notícia por ID
     */
    public Optional<Noticia> buscarPorId(Long id) {
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
        return Optional.ofNullable(titulo)
            .filter(t -> !t.isEmpty())
            .filter(t -> !t.trim().isEmpty())
            .map(String::trim)
            .map(noticiaDAO::findByTitulo)
            .orElseGet(this::listarTodas);
    }
    
    /**
     * Lista as últimas notícias
     */
    public List<Noticia> listarUltimas(int limite) {
        int limiteValido = limite <= 0 ? 10 : limite; // Padrão
        return noticiaDAO.findLatest(limiteValido);
    }
    
    /**
     * Busca notícias por palavras-chave
     */
    public List<Noticia> buscarPorPalavrasChave(String keywords) {
        return Optional.ofNullable(keywords)
            .filter(k -> !k.isEmpty())
            .filter(k -> !k.trim().isEmpty())
            .map(String::trim)
            .map(noticiaDAO::findByKeywords)
            .orElse(java.util.Collections.emptyList());
    }
    
    /**
     * Exclui uma notícia
     */
    public void excluir(Long id, Usuario usuarioLogado) throws ServiceException {
        Noticia noticia = noticiaDAO.findById(id)
            .orElseThrow(() -> new ServiceException("Notícia não encontrada"));
        
        // Verifica se o usuário pode excluir a notícia
        Optional.of(podeExcluir(noticia, usuarioLogado))
            .filter(Boolean::booleanValue)
            .orElseThrow(() -> new ServiceException("Você não tem permissão para excluir esta notícia"));
        
        noticiaDAO.delete(id);
    }
    
    /**
     * Verifica se o usuário pode editar a notícia
     */
    public boolean podeEditar(Noticia noticia, Usuario usuario) {
        return Optional.ofNullable(usuario)
            .filter(u -> noticia != null)
            .map(u -> u.isAdmin() || 
                Optional.ofNullable(noticia.getAutor())
                    .map(autor -> autor.getId().equals(u.getId()))
                    .orElse(false))
            .orElse(false);
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
        Optional.ofNullable(noticia)
            .orElseThrow(() -> new ServiceException("Notícia não pode ser nula"));
        
        validarTitulo(noticia.getTitulo());
        validarConteudo(noticia.getConteudo());
        
        Optional.ofNullable(noticia.getAutor())
            .orElseThrow(() -> new ServiceException("Autor é obrigatório"));
    }
    
    /**
     * Valida o título da notícia
     */
    private void validarTitulo(String titulo) throws ServiceException {
        Optional.ofNullable(titulo)
            .filter(t -> !t.isEmpty())
            .filter(t -> !t.trim().isEmpty())
            .orElseThrow(() -> new ServiceException("Título é obrigatório"));
        
        Optional.ofNullable(titulo)
            .filter(t -> t.length() >= 5)
            .orElseThrow(() -> new ServiceException("Título deve ter pelo menos 5 caracteres"));
    }
    
    /**
     * Valida o conteúdo da notícia
     */
    private void validarConteudo(String conteudo) throws ServiceException {
        Optional.ofNullable(conteudo)
            .filter(c -> !c.isEmpty())
            .filter(c -> !c.trim().isEmpty())
            .orElseThrow(() -> new ServiceException("Conteúdo é obrigatório"));
        
        Optional.ofNullable(conteudo)
            .filter(c -> c.length() >= 10)
            .orElseThrow(() -> new ServiceException("Conteúdo deve ter pelo menos 10 caracteres"));
    }
}