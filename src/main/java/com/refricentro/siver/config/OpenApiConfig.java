package com.refricentro.siver.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Documentacion Swagger / OpenAPI de la API.
 *
 *   Swagger UI:    http://localhost:8080/swagger-ui/index.html
 *   JSON OpenAPI:  http://localhost:8080/v3/api-docs
 *
 * Como probar un endpoint protegido desde Swagger:
 *   1. POST /api/auth/login con el correo y la clave
 *   2. Copiar el "token" de la respuesta
 *   3. Boton "Authorize" (candado) -> pegar el token -> Authorize
 *   Desde ahi Swagger manda "Authorization: Bearer <token>" en cada llamada.
 *
 * OJO: Swagger solo DOCUMENTA la seguridad. Quien de verdad rechaza las
 * peticiones sin token es Spring Security (SecurityConfig). Por eso las
 * rutas de Swagger se abren alli con permitAll, y todo lo demas sigue
 * pidiendo token aunque Swagger muestre o no el candado.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SIVER API - Refricentro Peregrino",
                version = "R3",
                description = "Ventas, inventario y servicio tecnico. "
                        + "Todos los endpoints, salvo el login, exigen un JWT.",
                contact = @Contact(name = "Equipo SIVER - UPN")),
        // Candado en TODOS los endpoints. El login lo quita con @SecurityRequirements.
        security = @SecurityRequirement(name = OpenApiConfig.ESQUEMA_JWT))
@SecurityScheme(
        name = OpenApiConfig.ESQUEMA_JWT,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Pega solo el token, sin la palabra Bearer.")
public class OpenApiConfig {

    public static final String ESQUEMA_JWT = "bearerAuth";
}
