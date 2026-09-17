package com.refricentro.siver.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Genera y valida los tokens JWT.
 *
 * Un JWT es una credencial firmada que el servidor entrega al iniciar sesion.
 * En cada peticion posterior el cliente lo manda en la cabecera:
 *
 *     Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 *
 * El token lleva dentro el correo del usuario y su rol, y va firmado con una
 * clave secreta. Si alguien le cambia una letra, la firma deja de coincidir y
 * el token se rechaza. Por eso el servidor no necesita guardar sesiones.
 */
@Component
public class JwtUtil {

    private final SecretKey clave;
    private final long duracionMs;

    public JwtUtil(@Value("${siver.jwt.secret}") String secreto,
                   @Value("${siver.jwt.expiracion-ms}") long duracionMs) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.duracionMs = duracionMs;
    }

    /** Crea el token al iniciar sesion. El rol viaja dentro como dato extra. */
    public String generarToken(String correo, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + duracionMs))
                .signWith(clave)
                .compact();
    }

    /** Devuelve el correo que lleva el token, o null si es invalido o vencio. */
    public String extraerCorreo(String token) {
        try {
            return leerContenido(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public String extraerRol(String token) {
        try {
            return leerContenido(token).get("rol", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /** true si la firma es correcta y el token no ha vencido. */
    public boolean esValido(String token) {
        try {
            leerContenido(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims leerContenido(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getDuracionMs() {
        return duracionMs;
    }
}
