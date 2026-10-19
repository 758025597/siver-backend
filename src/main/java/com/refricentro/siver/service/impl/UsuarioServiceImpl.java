
package com.refricentro.siver.service.impl;

import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.model.Usuario;
import com.refricentro.siver.repository.IGenericRepository;
import com.refricentro.siver.repository.UsuarioRepository;
import com.refricentro.siver.service.UsuarioService;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl extends ServicioSoftDelete<Usuario, Integer>
        implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected IGenericRepository<Usuario, Integer> getRepo() {
        return usuarioRepository;
    }

    @Override
    protected String nombreRecurso() {
        return "Usuario";
    }

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
    @Transactional
    public void registrarAcceso(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Usuario con correo", correo));
    }
}
