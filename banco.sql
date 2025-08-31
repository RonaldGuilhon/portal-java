-- Script de criação do banco de dados para o Portal de Notícias
-- MySQL 8.0+

-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS portal_noticias_java
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE portal_noticias_java;

-- Tabela de usuários
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('ADMIN', 'EDITOR') NOT NULL DEFAULT 'EDITOR',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_perfil (perfil),
    INDEX idx_ativo (ativo)
) ENGINE=InnoDB;

-- Tabela de notícias
CREATE TABLE noticias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    resumo TEXT,
    conteudo LONGTEXT NOT NULL,
    imagem_url VARCHAR(500),
    publicada BOOLEAN NOT NULL DEFAULT FALSE,
    data_publicacao TIMESTAMP NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_id BIGINT NOT NULL,
    
    INDEX idx_publicada (publicada),
    INDEX idx_data_publicacao (data_publicacao),
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_titulo (titulo),
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Inserir usuário administrador padrão
-- Senha: admin123 (hash BCrypt)
INSERT INTO usuarios (nome, email, senha, perfil, ativo) VALUES 
('Administrador', 'admin@portal.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLyEbeZTOWxS', 'ADMIN', TRUE);

-- Inserir usuário editor de exemplo
-- Senha: editor123 (hash BCrypt)
INSERT INTO usuarios (nome, email, senha, perfil, ativo) VALUES 
('Editor Portal', 'editor@portal.com', '$2a$10$8K1p/wFkTbXkVpNkL2Qo4eFjxjZeuFvxdgvOmMzqvqFjxjZeuFvxdg', 'EDITOR', TRUE);

-- Inserir notícias de exemplo
INSERT INTO noticias (titulo, resumo, conteudo, publicada, data_publicacao, usuario_id) VALUES 
(
    'Bem-vindos ao Portal de Notícias',
    'Inauguramos hoje nosso novo portal de notícias com as últimas informações e novidades.',
    '<p>É com grande satisfação que inauguramos hoje o nosso novo <strong>Portal de Notícias</strong>!</p>\n<p>Aqui você encontrará as mais recentes informações, análises aprofundadas e conteúdo de qualidade sobre os temas que mais importam.</p>\n<p>Nossa equipe de jornalistas está comprometida em trazer para você:</p>\n<ul>\n<li>Notícias atualizadas em tempo real</li>\n<li>Análises especializadas</li>\n<li>Conteúdo multimídia de qualidade</li>\n<li>Interface moderna e responsiva</li>\n</ul>\n<p>Fique sempre conectado conosco e não perca nenhuma novidade!</p>',
    TRUE,
    NOW(),
    1
),
(
    'Tecnologia e Inovação em 2024',
    'As principais tendências tecnológicas que estão moldando o futuro dos negócios e da sociedade.',
    '<p>O ano de 2024 promete ser um marco na evolução tecnológica, com inovações que estão transformando diversos setores.</p>\n<p><strong>Principais tendências:</strong></p>\n<h3>Inteligência Artificial</h3>\n<p>A IA continua avançando rapidamente, com aplicações práticas em:</p>\n<ul>\n<li>Automação de processos</li>\n<li>Análise de dados</li>\n<li>Atendimento ao cliente</li>\n<li>Desenvolvimento de software</li>\n</ul>\n<h3>Sustentabilidade Digital</h3>\n<p>Empresas estão investindo em tecnologias verdes e práticas sustentáveis para reduzir o impacto ambiental.</p>\n<p>Essas inovações estão criando novas oportunidades e desafios para profissionais e empresas em todo o mundo.</p>',
    TRUE,
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    2
),
(
    'Dicas de Produtividade para Desenvolvedores',
    'Estratégias e ferramentas para aumentar a eficiência no desenvolvimento de software.',
    '<p>A produtividade é fundamental para o sucesso de qualquer desenvolvedor. Aqui estão algumas dicas valiosas:</p>\n<h3>Organização do Ambiente</h3>\n<ul>\n<li>Use um IDE configurado adequadamente</li>\n<li>Mantenha seu workspace organizado</li>\n<li>Configure atalhos de teclado personalizados</li>\n</ul>\n<h3>Metodologias Ágeis</h3>\n<p>Implemente práticas como:</p>\n<ul>\n<li>Scrum para gestão de projetos</li>\n<li>Code review sistemático</li>\n<li>Testes automatizados</li>\n<li>Integração contínua</li>\n</ul>\n<h3>Ferramentas Essenciais</h3>\n<p>Invista em ferramentas que automatizem tarefas repetitivas e melhorem seu fluxo de trabalho.</p>\n<p>Lembre-se: a produtividade não é sobre trabalhar mais, mas sim trabalhar de forma mais inteligente!</p>',
    TRUE,
    DATE_SUB(NOW(), INTERVAL 5 DAY),
    1
),
(
    'O Futuro do Desenvolvimento Web',
    'Explorando as novas tecnologias e frameworks que estão revolucionando o desenvolvimento web.',
    '<p>O desenvolvimento web está em constante evolução, com novas tecnologias surgindo regularmente.</p>\n<h3>Frameworks Modernos</h3>\n<p>Os frameworks atuais oferecem:</p>\n<ul>\n<li>Melhor performance</li>\n<li>Desenvolvimento mais rápido</li>\n<li>Manutenção simplificada</li>\n<li>Experiência do usuário aprimorada</li>\n</ul>\n<h3>Tendências Emergentes</h3>\n<ul>\n<li><strong>JAMstack:</strong> JavaScript, APIs e Markup</li>\n<li><strong>Progressive Web Apps:</strong> Aplicações web com funcionalidades nativas</li>\n<li><strong>Serverless:</strong> Arquitetura sem servidor</li>\n<li><strong>WebAssembly:</strong> Performance próxima ao código nativo</li>\n</ul>\n<p>Essas tecnologias estão redefinindo como criamos e distribuímos aplicações web, oferecendo novas possibilidades para desenvolvedores e usuários.</p>',
    FALSE,
    NULL,
    2
);

-- Criar índices adicionais para otimização de consultas
CREATE INDEX idx_noticias_busca ON noticias(titulo, resumo(255));
CREATE INDEX idx_noticias_publicadas_data ON noticias(publicada, data_publicacao DESC);

-- Criar view para notícias publicadas com informações do autor
CREATE VIEW vw_noticias_publicadas AS
SELECT 
    n.id,
    n.titulo,
    n.resumo,
    n.conteudo,
    n.imagem_url,
    n.data_publicacao,
    n.data_criacao,
    u.nome as autor_nome,
    u.email as autor_email
FROM noticias n
INNER JOIN usuarios u ON n.usuario_id = u.id
WHERE n.publicada = TRUE
ORDER BY n.data_publicacao DESC;

-- Procedure para publicar notícia
DELIMITER //
CREATE PROCEDURE PublicarNoticia(IN noticia_id BIGINT)
BEGIN
    UPDATE noticias 
    SET publicada = TRUE, 
        data_publicacao = NOW(),
        data_atualizacao = NOW()
    WHERE id = noticia_id;
END //
DELIMITER ;

-- Procedure para despublicar notícia
DELIMITER //
CREATE PROCEDURE DespublicarNoticia(IN noticia_id BIGINT)
BEGIN
    UPDATE noticias 
    SET publicada = FALSE, 
        data_publicacao = NULL,
        data_atualizacao = NOW()
    WHERE id = noticia_id;
END //
DELIMITER ;

-- Comentários finais
-- Para conectar à aplicação, use as seguintes configurações:
-- Host: localhost
-- Porta: 3306
-- Database: portal_noticias
-- Usuário: root (ou crie um usuário específico)
-- Senha: (sua senha do MySQL)

-- Usuários padrão criados:
-- Admin: admin@portal.com / admin123
-- Editor: editor@portal.com / editor123