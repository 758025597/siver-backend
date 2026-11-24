package com.refricentro.siver.security;

import com.refricentro.siver.model.Usuario;
import com.refricentro.siver.repository.UsuarioRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Quien esta haciendo la peticion, segun el JWT.
 *
 * JwtAuthFilter deja al usuario en el SecurityContext; esta clase lo lee para
 * que los servicios sepan quien registro una venta, un ajuste de stock o
 * quien leyo una notificacion, sin tener que confiar en un id que mande el
 * cliente (que podria ser falso).
 */
@Component
@RequiredArgsConstructor
public class UsuarioActual {

    private final UsuarioRepository usuarioRepository;

    /** El correo del usuario autenticado, o empty si la peticion no trae token. */
    public Optional<String> correo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return Optional.ofNullable(auth.getName());
    }

    /** ADMINISTRADOR, VENDEDOR, TECNICO... sin el prefijo ROLE_. */
    public Optional<String> rol() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst();
    }

    public boolean tieneRol(String rol) {
        return rol().map(rol::equals).orElse(false);
    }

    /** La entidad Usuario completa (con su rol). */
    public Optional<Usuario> obtener() {
        return correo().flatMap(usuarioRepository::findByCorreoIgnoreCase);
    }
}
