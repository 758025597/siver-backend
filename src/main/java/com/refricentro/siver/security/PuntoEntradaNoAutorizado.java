package com.refricentro.siver.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Responde cuando alguien llama a un endpoint protegido sin identificarse.
 *
 * Sin esta clase Spring devuelve un 403 (prohibido) con el cuerpo vacio, que
 * confunde: 403 significa "se quien eres, pero no puedes", mientras que aqui
 * el caso real es "no se quien eres". Eso es un 401.
 *
 * Ademas devuelve el mismo formato JSON que el resto de errores de la API.
 */
@Component
public class PuntoEntradaNoAutorizado implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {"momento":"%s","estado":401,"error":"No autorizado",\
                "mensaje":"Necesitas iniciar sesion. Envia la cabecera \
                Authorization: Bearer <token> que devuelve POST /api/auth/login.",\
                "campos":null}"""
                .formatted(LocalDateTime.now()));
    }
}
