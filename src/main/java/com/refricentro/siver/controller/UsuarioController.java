package com.refricentro.siver.controller;

import com.refricentro.siver.dto.UsuarioRequest;
import com.refricentro.siver.dto.UsuarioResponse;
import com.refricentro.siver.modelos.Usuario;
import com.refricentro.siver.service.RolService;
import com.refricentro.siver.service.UsuarioService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de usuario.
 *
 * Los 5 del CRUD los hereda de ControladorGenerico:
 *   GET    /api/usuarios
 *   GET    /api/usuarios/{id}
 *   POST   /api/usuarios
 *   PUT    /api/usuarios/{id}
 *   DELETE /api/usuarios/{id}   -> borrado logico, deja activo = false
 *
 * Inyecta tambien RolService porque la peticion trae un idRol y hay que
 * convertirlo en el objeto Rol. De paso queda validado: si el rol no
 * existe, responde 404 con un mensaje claro.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController
        extends ControladorGenerico<Usuario, Integer, UsuarioRequest, UsuarioResponse> {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        super(usuarioService);
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @Override
    protected Usuario aEntidad(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setRol(rolService.buscarPorId(request.idRol()));
        usuario.setNombres(request.nombres());
        usuario.setApellidos(request.apellidos());
        usuario.setDni(request.dni());
        usuario.setCorreo(request.correo());
        usuario.setTelefono(request.telefono());
        // En texto plano a proposito: el servicio la cifra antes de guardar.
        usuario.setClave(request.clave());
        return usuario;
    }

    @Override
    protected UsuarioResponse aRespuesta(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getRol() != null ? usuario.getRol().getIdRol() : null,
                usuario.getRol() != null ? usuario.getRol().getNombre() : null,
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getDni(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getActivo(),
                usuario.getUltimoAcceso(),
                usuario.getFechaRegistro());
        // Nota: la clave NO se devuelve nunca.
    }

    /** GET /api/usuarios/activos - solo los vigentes. */
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponse>> listarActivos() {
        List<UsuarioResponse> respuesta = usuarioService.listarActivos().stream()
                .map(this::aRespuesta)
                .toList();
        return ResponseEntity.ok(respuesta);
    }
}
