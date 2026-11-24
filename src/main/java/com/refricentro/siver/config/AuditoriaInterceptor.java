package com.refricentro.siver.config;

import com.refricentro.siver.model.Auditoria;
import com.refricentro.siver.security.Roles;
import com.refricentro.siver.security.UsuarioActual;
import com.refricentro.siver.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Registra en la tabla auditoria cada operacion que CAMBIA datos.
 *
 * Un HandlerInterceptor corre alrededor de cada peticion que llega a un
 * controlador. Se usa afterCompletion() porque ahi ya se conoce el codigo
 * HTTP de la respuesta (201, 400, 409...) y el Location del registro creado.
 *
 * Los GET no se auditan: solo leen. Tampoco /api/auth (el login lo audita
 * AuthController, que conoce el correo) ni /api/notificaciones (marcar como
 * leida no es una accion sobre datos del negocio).
 *
 * Asi la auditoria cubre los 11 modulos sin tocar ningun controlador.
 */
@Component
@RequiredArgsConstructor
public class AuditoriaInterceptor implements HandlerInterceptor {

    private static final Set<String> METODOS_QUE_CAMBIAN = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final AuditoriaService auditoriaService;
    private final UsuarioActual usuarioActual;

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {

        String metodo = request.getMethod();
        if (!METODOS_QUE_CAMBIAN.contains(metodo)) {
            return;
        }

        // Sin el contexto (/siver en Tomcat): /api/clientes/5
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        if (!ruta.startsWith("/api/") || ruta.startsWith("/api/auth")
                || ruta.startsWith("/api/notificaciones")) {
            return;
        }

        String[] partes = ruta.substring("/api/".length()).split("/");
        String modulo = partes[0];

        Auditoria a = new Auditoria();
        usuarioActual.obtener().ifPresentOrElse(u -> {
            a.setIdUsuario(u.getIdUsuario());
            a.setUsuario(u.getCorreo());
            a.setRol(Roles.normalizar(u.getRol() != null ? u.getRol().getNombre() : null));
        }, () -> a.setUsuario(usuarioActual.correo().orElse("anonimo")));

        a.setAccion(accion(metodo, ruta));
        a.setModulo(recortar(modulo, 50));
        a.setIdRegistro(idRegistro(partes, response));
        a.setMetodo(metodo);
        a.setRuta(recortar(ruta, 200));
        a.setEstadoHttp(response.getStatus());
        a.setIp(recortar(ipCliente(request), 45));

        auditoriaService.registrar(a);
    }

    private static String accion(String metodo, String ruta) {
        return switch (metodo) {
            case "POST" -> "CREAR";
            case "PUT" -> "ACTUALIZAR";
            case "DELETE" -> "ELIMINAR";
            default -> ruta.endsWith("/anular") ? "ANULAR"
                    : ruta.contains("/estado") ? "CAMBIAR_ESTADO" : "ACTUALIZAR";
        };
    }

    /** El id sale de la URL (/clientes/5) o, en un POST, del Location (/api/clientes/12). */
    private static String idRegistro(String[] partes, HttpServletResponse response) {
        if (partes.length > 1 && partes[1].matches("\\d+")) {
            return partes[1];
        }
        String location = response.getHeader("Location");
        if (location != null) {
            String ultimo = location.substring(location.lastIndexOf('/') + 1);
            if (ultimo.matches("\\d+")) {
                return ultimo;
            }
        }
        return null;
    }

    /** Si hay un proxy delante (Apache, Docker), la IP real viene en X-Forwarded-For. */
    private static String ipCliente(HttpServletRequest request) {
        String reenviada = request.getHeader("X-Forwarded-For");
        if (reenviada != null && !reenviada.isBlank()) {
            return reenviada.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static String recortar(String texto, int max) {
        return texto == null || texto.length() <= max ? texto : texto.substring(0, max);
    }
}
