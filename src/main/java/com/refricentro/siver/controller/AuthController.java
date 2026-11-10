package com.refricentro.siver.controller;

import com.refricentro.siver.dto.LoginRequest;
import com.refricentro.siver.dto.LoginResponse;
import com.refricentro.siver.exception.ErrorRespuesta;
import com.refricentro.siver.model.Usuario;
import com.refricentro.siver.security.JwtUtil;
import com.refricentro.siver.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
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
@Tag(name = "Autenticacion", description = "Inicio de sesion con JWT. Es el unico endpoint abierto sin token.")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    /**
     * POST /api/auth/login
     *
     * El AuthenticationManager compara la clave que llega con el hash BCrypt
     * guardado. Si no coincide lanza BadCredentialsException; si el usuario
     * esta desactivado, DisabledException. Las dos terminan en un 401.
     *
     * Nunca se dice si fallo el correo o la clave: decirlo le confirmaria a
     * un atacante que ese correo existe.
     *
     * @SecurityRequirements() vacio: en Swagger este endpoint NO pide el
     * candado, porque justamente es el que entrega el token.
     */
    @Operation(summary = "Inicia sesion y devuelve el JWT",
            description = "Copia el token de la respuesta y pegalo en el boton Authorize de Swagger.")
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Login correcto, devuelve el token",
            content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "400", description = "Falta el correo o la clave",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "401", description = "Correo o clave incorrectos, o cuenta desactivada")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getClave()));
        } catch (DisabledException e) {
            return ResponseEntity.status(401).body(error("La cuenta esta desactivada"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(error("Correo o clave incorrectos"));
        }

        Usuario usuario = usuarioService.buscarPorCorreo(request.getCorreo());
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