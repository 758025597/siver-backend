# =====================================================================
#  SIVER - Backend (Spring Boot) en Apache Tomcat 11
#
#  Construccion en dos etapas (multi-stage):
#    1. "build":  Maven compila y empaqueta target/siver.war
#    2. final:    imagen oficial de Tomcat 11 con Java 25, solo con el WAR
#
#  La imagen final NO lleva Maven, ni el codigo fuente, ni credenciales:
#  las variables de entorno (DB_*, JWT_*, CORS_ORIGINS) se pasan al crear
#  el contenedor (docker-compose.yml), no se escriben en la imagen.
# =====================================================================

# ---------- Etapa 1: compilar ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Primero solo el pom: si no cambian las dependencias, Docker reutiliza esta
# capa y no las vuelve a descargar en cada construccion.
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# lombok.config es OBLIGATORIO: le dice a Lombok que copie @Qualifier al
# constructor. Sin el, cada controlador recibiria el ModelMapper por defecto
# (sin sus reglas) y la API responderia los ids en null.
COPY lombok.config .
COPY src ./src
# Las pruebas de integracion necesitan una base MySQL: se ejecutan fuera de
# Docker (mvn test). Aqui solo se empaqueta.
RUN mvn -q -B -DskipTests package

# ---------- Etapa 2: Tomcat 11 ----------
FROM tomcat:11.0-jdk25-temurin

ENV TZ=America/Lima \
    JAVA_OPTS="-Duser.timezone=America/Lima -Xms256m -Xmx512m"

# Se quitan las aplicaciones de ejemplo de Tomcat (manager, docs...): menos
# superficie de ataque y arranque mas rapido.
RUN rm -rf /usr/local/tomcat/webapps/*

# siver.war -> la aplicacion queda en http://<host>:8080/siver/
COPY --from=build /app/target/siver.war /usr/local/tomcat/webapps/siver.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
