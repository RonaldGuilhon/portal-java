@echo off
REM Script para fazer deploy da aplicação no GlassFish
REM Deve ser executado após o GlassFish estar rodando

echo Fazendo deploy da aplicação Portal de Notícias...
echo.

REM Define JAVA_HOME sem espaços
set JAVA_HOME=C:\PROGRA~1\Java\jdk1.8.0_201

REM Remove caminhos problemáticos do PATH
set PATH=%PATH:C:/Program Files/Maven/apache-maven-3.9.5/bin;=%
set PATH=%PATH:C:\Program Files\Maven\apache-maven-3.9.5\bin;=%

REM Verifica se o WAR existe
if not exist "target\portal-noticias.war" (
    echo WAR não encontrado. Compilando projeto...
    echo.
    
    REM Adiciona Maven ao PATH se existir
    if exist "C:\apache-maven-3.9.9\bin" (
        set PATH=C:\apache-maven-3.9.9\bin;%PATH%
    )
    
    mvn clean package
    if errorlevel 1 (
        echo ERRO: Falha na compilação
        pause
        exit /b 1
    )
)

echo.
echo Fazendo undeploy da versão anterior (se existir)...
C:\glassfish5\bin\asadmin.bat undeploy portal-noticias 2>nul

echo.
echo Fazendo deploy da nova versão...
C:\glassfish5\bin\asadmin.bat deploy --force --contextroot portal-noticias target\portal-noticias.war

if errorlevel 1 (
    echo.
    echo ERRO: Falha no deploy
    echo Verifique se o GlassFish está rodando
    pause
    exit /b 1
)

echo.
echo Deploy realizado com sucesso!
echo Aplicação disponível em: http://localhost:8080/portal-noticias
echo.
echo Pressione qualquer tecla para continuar...
pause > nul