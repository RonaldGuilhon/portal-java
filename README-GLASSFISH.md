# Portal de Notícias - Configuração GlassFish

Este projeto foi refatorado para funcionar com o servidor de aplicação GlassFish.

## Pré-requisitos

1. **Java 8 ou superior**
2. **MySQL 8.0+**
3. **GlassFish Server 5.1+**
4. **Maven 3.6+**

## Configuração do Banco de Dados

1. Execute o script SQL localizado em `banco.sql` para criar o banco de dados:
```sql
mysql -u root -p < banco.sql
```

## Configuração do GlassFish

### 1. Instalar o Driver MySQL

1. Baixe o MySQL Connector/J (mysql-connector-java-8.0.33.jar)
2. Copie o arquivo JAR para: `{GLASSFISH_HOME}/glassfish/domains/domain1/lib/`
3. Reinicie o GlassFish

### 2. Configurar o Data Source

O arquivo `glassfish-resources.xml` será automaticamente processado durante o deploy. Alternativamente, você pode configurar manualmente:

1. Acesse o Admin Console: http://localhost:4848
2. Navegue para: Resources > JDBC > JDBC Connection Pools
3. Crie um novo pool com as seguintes configurações:
   - Pool Name: `PortalNoticiasPool`
   - Resource Type: `javax.sql.DataSource`
   - Database Driver Vendor: `MySQL`
   - Datasource Classname: `com.mysql.cj.jdbc.MysqlDataSource`

4. Configure as propriedades:
   - serverName: `localhost`
   - portNumber: `3306`
   - databaseName: `portal_noticias_java`
   - User: `root`
   - Password: `root`
   - URL: `jdbc:mysql://localhost:3306/portal_noticias_java?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`

5. Crie o JDBC Resource:
   - JNDI Name: `jdbc/PortalNoticiasDS`
   - Pool Name: `PortalNoticiasPool`

## Build e Deploy

### 1. Compilar o projeto
```bash
mvn clean package
```

### 2. Deploy no GlassFish

**Opção 1: Via Admin Console**
1. Acesse: http://localhost:4848
2. Navegue para: Applications
3. Clique em "Deploy"
4. Selecione o arquivo `target/portal-noticias.war`
5. Context Root: `/portal-noticias`

**Opção 2: Via linha de comando**
```bash
asadmin deploy --contextroot /portal-noticias target/portal-noticias.war
```

**Opção 3: Via Maven Plugin**
```bash
mvn glassfish:deploy
```

## Acessar a Aplicação

Após o deploy bem-sucedido:

- **Página inicial**: http://localhost:8080/portal-noticias/
- **Área administrativa**: http://localhost:8080/portal-noticias/pages/admin/login.xhtml
- **Admin Console GlassFish**: http://localhost:4848

## Credenciais Padrão

- **Usuário**: admin
- **Senha**: admin123

## Estrutura do Projeto

```
portal-java/
├── src/main/java/com/portal/
│   ├── controller/     # Managed Beans JSF
│   ├── dao/           # Data Access Objects
│   ├── filter/        # Filtros de autenticação
│   ├── model/         # Entidades JPA
│   ├── service/       # Camada de serviços
│   └── validator/     # Validadores customizados
├── src/main/resources/
│   └── META-INF/
│       └── persistence.xml  # Configuração JPA
├── src/main/webapp/
│   ├── pages/         # Páginas XHTML
│   ├── resources/     # CSS, JS, imagens
│   └── WEB-INF/
│       ├── web.xml
│       ├── faces-config.xml
│       └── glassfish-resources.xml
└── banco.sql          # Script do banco de dados
```

## Troubleshooting

### Problema: ClassNotFoundException para MySQL Driver
**Solução**: Certifique-se de que o mysql-connector-java-8.0.33.jar está em `{GLASSFISH_HOME}/glassfish/domains/domain1/lib/`

### Problema: Erro de conexão com banco
**Solução**: Verifique se o MySQL está rodando e as credenciais estão corretas no glassfish-resources.xml

### Problema: Páginas JSF não carregam
**Solução**: Verifique se o JSF está habilitado no GlassFish e se as dependências estão corretas

## Comandos Úteis do GlassFish

```bash
# Iniciar o servidor
asadmin start-domain

# Parar o servidor
asadmin stop-domain

# Listar aplicações
asadmin list-applications

# Fazer undeploy
asadmin undeploy portal-noticias

# Verificar status
asadmin list-domains
```

## Tecnologias Utilizadas

- **Java 8**
- **JSF 2.3** (JavaServer Faces)
- **JPA 2.0** (Java Persistence API)
- **Hibernate 5.6** (Implementação JPA)
- **MySQL 8.0** (Banco de dados)
- **GlassFish 5.1** (Servidor de aplicação)
- **Maven 3** (Gerenciamento de dependências)