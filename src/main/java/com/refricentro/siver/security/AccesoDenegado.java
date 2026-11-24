package com.refricentro.siver.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Responde 403 cuando el usuario SI inicio sesion, pero su rol no le alcanza
 * para lo que pidio. Por ejemplo, un Vendedor que intenta crear un usuario.
 *
 * 401 = no se quien eres (PuntoEntradaNoAutorizado)
 * 403 = se quien eres, pero no tienes permiso (esta clase)
 *
 * Devuelve el mismo JSON que el resto de errores de la API.
 */
@Component
public class AccesoDenegado implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {"momento":"%s","estado":403,"error":"Prohibido",\
                "mensaje":"Tu rol no tiene permiso para realizar esta accion.",\
                "campos":null}"""
                .formatted(LocalDateTime.now()));
    }
}
