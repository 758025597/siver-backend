package com.refricentro.siver.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Permite que el frontend Angular consuma esta API.
 *
 * El navegador considera "origen" a la combinacion protocolo + IP + puerto.
 * Si el frontend se abre desde un origen distinto al del backend, el
 * navegador bloquea las peticiones salvo que el backend lo autorice aqui.
 *
 * Los origenes NO estan escritos en el codigo: vienen de la variable de
 * entorno CORS_ORIGINS (propiedad siver.cors.origenes), separados por coma.
 *
 *   desarrollo:  http://localhost:4200
 *   VMs / PCs:   http://192.168.56.10   (la IP real del servidor frontend)
 *
 * Expone Content-Disposition para que Angular pueda leer el nombre de los
 * archivos PDF y Excel que descarga.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] origenes;

    public CorsConfig(@Value("${siver.cors.origenes:http://localhost:4200}") String origenes) {
        this.origenes = Arrays.stream(origenes.split(","))
                .map(String::trim)
                .filter(o -> !o.isEmpty())
                .toArray(String[]::new);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(origenes)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition", "Location");
    }
}
