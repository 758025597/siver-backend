@echo off
rem =====================================================================
rem  VM-Backend (Windows): variables de entorno de SIVER para Tomcat 11
rem
rem  Copiar a:  C:\apache-tomcat-11\bin\setenv.bat
rem  Tomcat lo ejecuta al arrancar (startup.bat / catalina.bat run).
rem
rem  Si Tomcat esta instalado como SERVICIO de Windows, setenv.bat no se lee:
rem  en ese caso definir las mismas variables en
rem  Sistema > Configuracion avanzada > Variables de entorno (del sistema)
rem  y reiniciar el servicio.
rem =====================================================================

set "DB_HOST=192.168.56.30"
set "DB_PORT=3306"
set "DB_NAME=siver_bd"
set "DB_USER=siver_app"
set "DB_PASSWORD=CambiaEstaClave-2026"

set "JWT_SECRET=cambia-esta-clave-secreta-de-al-menos-32-caracteres"
set "JWT_EXPIRACION_MS=28800000"

set "CORS_ORIGINS=http://192.168.56.10"

set "MAIL_HABILITADO=false"

set "CATALINA_OPTS=%CATALINA_OPTS% -Duser.timezone=America/Lima -Xms256m -Xmx512m"
