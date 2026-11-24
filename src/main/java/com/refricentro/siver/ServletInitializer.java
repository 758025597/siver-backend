package com.refricentro.siver;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Punto de entrada cuando el backend corre como WAR dentro de un Apache
 * Tomcat 11 externo (despliegue del EF).
 *
 * Con mvn spring-boot:run arranca SiverApplication.main() y Spring levanta su
 * propio Tomcat. En cambio, cuando se copia siver.war a la carpeta webapps de
 * Tomcat, nadie llama a main(): Tomcat busca una clase que extienda
 * SpringBootServletInitializer y la usa para arrancar la aplicacion.
 *
 * En ese caso la API queda en  http://IP-DEL-BACKEND:8080/siver/api/...
 * (siver = nombre del WAR = contexto de la aplicacion).
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SiverApplication.class);
    }
}
