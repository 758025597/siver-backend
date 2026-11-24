package com.refricentro.siver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registra el interceptor de auditoria para toda la API.
 *
 * Ademas activa:
 *   @EnableScheduling  tareas programadas (revision diaria de stock y ordenes)
 *   @EnableAsync       el envio de correos en segundo plano, para que la
 *                      respuesta al usuario no espere al servidor SMTP
 */
@Configuration
@EnableAsync
@EnableScheduling
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuditoriaInterceptor auditoriaInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditoriaInterceptor).addPathPatterns("/api/**");
    }
}
