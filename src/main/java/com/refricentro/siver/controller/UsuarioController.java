package com.refricentro.siver.controller;

import com.refricentro.siver.dto.UsuarioRequest;
import com.refricentro.siver.dto.UsuarioResponse;
import com.refricentro.siver.model.Usuario;
import com.refricentro.siver.service.RolService;
import com.refricentro.siver.service.UsuarioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
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
 *
 * La clave NO se devuelve nunca: UsuarioResponse no tiene ese campo.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController
        extends ControladorGenerico<Usuario, Integer, UsuarioRequest, UsuarioResponse> {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    @Qualifier("usuarioMapper")
    private final ModelMapper mapper;

    @Override
    protected UsuarioService getServicio() {
        return usuarioService;
    }

    @Override
    protected ModelMapper getMapper() {
        return mapper;
    }

    @Override
    protected Integer obtenerId(Usuario usuario) {
        return usuario.getIdUsuario();
    }

    /**
     * ModelMapper copia los datos simples; el rol se busca aparte.
     * La clave llega en texto plano a proposito: el servicio la cifra.
     */
    @Override
    protected Usuario aEntidad(UsuarioRequest request) {
        Usuario usuario = super.aEntidad(request);
        usuario.setRol(rolService.buscarPorId(request.getIdRol()));
        return usuario;
    }

    /** GET /api/usuarios/activos - solo los vigentes. */
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponse>> listarActivos() {
        return ResponseEntity.ok(aRespuestas(usuarioService.listarActivos()));
    }
}
