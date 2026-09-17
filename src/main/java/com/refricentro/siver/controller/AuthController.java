package com.refricentro.siver.controller;

import com.refricentro.siver.dto.LoginRequest;
import com.refricentro.siver.dto.LoginResponse;
import com.refricentro.siver.modelos.Usuario;
import com.refricentro.siver.security.JwtUtil;
import com.refricentro.siver.service.UsuarioService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Autenticacion.
 *
 * Es el UNICO controlador abierto sin token: si estuviera protegido, nadie
 * podria iniciar sesion nunca.
 *
 *   POST /api/auth/login  ->  devuelve el JWT
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    /**
     * POST /api/auth/login
     *
     * El AuthenticationManager compara la clave que llega con el hash BCrypt
     * guardado. Si no coincide lanza BadCredentialsException; si el usuario
     * esta desactivado, DisabledException. Las dos terminan en un 401.
     *
     * Nunca se dice si fallo el correo o la clave: decirlo le confirmaria a
     * un atacante que ese correo existe.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.correo(), request.clave()));
        } catch (DisabledException e) {
            return ResponseEntity.status(401).body(error("La cuenta esta desactivada"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(error("Correo o clave incorrectos"));
        }

        Usuario usuario = usuarioService.buscarPorCorreo(request.correo());
        String rol = usuario.getRol() != null ? usuario.getRol().getNombre() : "SIN_ROL";

        // Deja constancia de cuando entro. Es para lo que existe la columna.
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioService.registrarAcceso(usuario);

        String token = jwtUtil.generarToken(usuario.getCorreo(), rol.toUpperCase().replace(' ', '_'));

        return ResponseEntity.ok(new LoginResponse(
                token, "Bearer", jwtUtil.getDuracionMs(),
                usuario.getIdUsuario(), usuario.getNombres(), usuario.getApellidos(),
                usuario.getCorreo(), rol));
    }

    private java.util.Map<String, Object> error(String mensaje) {
        return java.util.Map.of(
                "momento", LocalDateTime.now().toString(),
                "estado", 401,
                "error", "No autorizado",
                "mensaje", mensaje);
    }
}
