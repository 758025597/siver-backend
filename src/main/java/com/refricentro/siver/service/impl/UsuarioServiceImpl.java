package com.refricentro.siver.service.impl;

import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.modelos.Usuario;
import com.refricentro.siver.repository.UsuarioRepository;
import com.refricentro.siver.service.UsuarioService;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio de usuario.
 *
 * Hereda de ServicioSoftDelete: eliminar() deja activo = false. Es lo
 * correcto porque un usuario firmo ventas y ordenes de servicio, y las
 * llaves foraneas son ON DELETE RESTRICT. Borrarlo destruiria la
 * trazabilidad de quien hizo cada operacion.
 *
 * El cifrado de la clave se hace AQUI y no en el controlador: es una regla
 * de negocio. Asi la contrasena en texto plano nunca sale de esta clase.
 */
@Service
public class UsuarioServiceImpl extends ServicioSoftDelete<Usuario, Integer>
        implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected String nombreRecurso() {
        return "Usuario";
    }

    /**
     * Campos editables con un PUT.
     *
     * La clave se vuelve a cifrar porque el controlador siempre la manda en
     * texto plano. No se copian el id, ni activo, ni ultimo_acceso (lo pone
     * el login), ni fecha_registro (la pone MySQL).
     */
    @Override
    protected void copiarDatos(Usuario origen, Usuario destino) {
        destino.setRol(origen.getRol());
        destino.setNombres(origen.getNombres());
        destino.setApellidos(origen.getApellidos());
        destino.setDni(origen.getDni());
        destino.setCorreo(origen.getCorreo());
        destino.setTelefono(origen.getTelefono());
        destino.setClave(passwordEncoder.encode(origen.getClave()));
    }

    /**
     * Se sobrescribe solo para cifrar la clave antes de guardar.
     * El resto del trabajo lo hace la clase padre.
     */
    @Override
    @Transactional
    public Usuario crear(Usuario usuario) {
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        return super.crear(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con correo", correo));
    }
}
