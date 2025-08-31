@echo off
REM Script para iniciar o GlassFish com variaveis de ambiente corretas
REM Corrige problemas com espacos nos caminhos

echo Configurando variaveis de ambiente...

REM Define JAVA_HOME sem espacos (formato 8.3)
set "JAVA_HOME=C:\PROGRA~1\Java\jdk1.8.0_201"

REM Limpa completamente o PATH e reconstroi apenas com caminhos seguros
setlocal EnableDelayedExpansion
set "PATH=C:\Windows\System32;C:\Windows;C:\Windows\System32\Wbem"

REM Adiciona o Java ao PATH
set "PATH=!PATH!;!JAVA_HOME!\bin"

REM Adiciona o Maven sem espacos se existir
if exist "C:\apache-maven-3.9.9\bin" (
    set "PATH=!PATH!;C:\apache-maven-3.9.9\bin"
)

REM Adiciona o GlassFish bin
set "PATH=!PATH!;C:\glassfish5\bin"

echo JAVA_HOME: !JAVA_HOME!
echo.

REM Verifica se o Java esta funcionando
echo Testando Java...
"!JAVA_HOME!\bin\java" -version
if errorlevel 1 (
    echo ERRO: Java nao encontrado em !JAVA_HOME!
    echo Verifique se o Java 8 esta instalado corretamente.
    pause
    exit /b 1
)

echo.
echo Iniciando GlassFish...
echo.

REM Inicia o dominio do GlassFish
C:\glassfish5\bin\asadmin.bat start-domain domain1

if errorlevel 1 (
    echo.
    echo ERRO: Falha ao iniciar o GlassFish
    echo Verifique os logs em C:\glassfish5\glassfish\domains\domain1\logs\server.log
    pause
    exit /b 1
)

echo.
echo GlassFish iniciado com sucesso!
echo Painel administrativo: http://localhost:4848
echo Aplicacao: http://localhost:8080/portal-noticias
echo.
echo Pressione qualquer tecla para continuar...
pause > nul