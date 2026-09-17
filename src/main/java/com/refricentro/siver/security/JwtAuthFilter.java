package com.refricentro.siver.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Revisa el token en CADA peticion que llega.
 *
 * Como funciona, paso a paso:
 *   1. Busca la cabecera Authorization: Bearer <token>
 *   2. Si no hay, deja pasar sin identificar. Spring decidira despues si ese
 *      endpoint necesitaba o no estar autenticado.
 *   3. Si hay token, comprueba la firma y que no haya vencido.
 *   4. Si es valido, carga al usuario y lo deja registrado como autenticado
 *      para el resto de la peticion.
 *
 * Extiende OncePerRequestFilter para garantizar que corre una sola vez por
 * peticion, aunque haya reenvios internos.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String CABECERA = "Authorization";
    private static final String PREFIJO = "Bearer ";

    private final JwtUtil jwtUtil;
    private final DetalleUsuarioService detalleUsuarioService;

    public JwtAuthFilter(JwtUtil jwtUtil, DetalleUsuarioService detalleUsuarioService) {
        this.jwtUtil = jwtUtil;
        this.detalleUsuarioService = detalleUsuarioService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String cabecera = request.getHeader(CABECERA);

        if (cabecera != null && cabecera.startsWith(PREFIJO)) {
            String token = cabecera.substring(PREFIJO.length());
            String correo = jwtUtil.extraerCorreo(token);

            boolean sinAutenticarAun = SecurityContextHolder.getContext().getAuthentication() == null;

            if (correo != null && sinAutenticarAun && jwtUtil.esValido(token)) {
                try {
                    UserDetails usuario = detalleUsuarioService.loadUserByUsername(correo);

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    usuario, null, usuario.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    // Token de un usuario que ya no existe o fue desactivado:
                    // se deja pasar sin autenticar y Spring respondera 401.
                    SecurityContextHolder.clearContext();
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
