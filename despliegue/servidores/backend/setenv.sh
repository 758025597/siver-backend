#!/bin/sh
# =====================================================================
#  VM-Backend (Linux): variables de entorno de SIVER para Tomcat 11
#
#  Copiar a:  $CATALINA_BASE/bin/setenv.sh   (ej. /opt/tomcat/bin/setenv.sh)
#  Permisos:  chmod 750 setenv.sh   (solo el usuario tomcat puede leerlo)
#
#  Tomcat ejecuta este archivo solo al arrancar. Asi las credenciales
#  quedan en el SERVIDOR, no en el codigo ni en el repositorio (req. 4.6).
# =====================================================================

# --- Perfil de produccion (application-prod.properties) ---
export SPRING_PROFILES_ACTIVE=prod

# --- Base de datos (VM-BD) ---
export DB_HOST=192.168.56.30
export DB_PORT=3306
export DB_NAME=siver_bd
export DB_USER=siver_app
export DB_PASSWORD='CambiaEstaClave-2026'

# --- JWT ---
export JWT_SECRET='cambia-esta-clave-secreta-de-al-menos-32-caracteres'
export JWT_EXPIRACION_MS=28800000

# --- CORS: direccion REAL desde donde se abre el frontend (VM-Frontend) ---
export CORS_ORIGINS='http://192.168.56.10'

# --- Correo (opcional) ---
export MAIL_HABILITADO=false

# --- JVM: zona horaria de Peru y memoria ---
export CATALINA_OPTS="$CATALINA_OPTS -Duser.timezone=America/Lima -Xms256m -Xmx512m"
