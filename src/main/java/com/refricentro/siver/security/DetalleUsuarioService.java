package com.refricentro.siver.security;

import com.refricentro.siver.modelos.Usuario;
import com.refricentro.siver.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Le dice a Spring Security como buscar un usuario en NUESTRA base.
 *
 * Spring no sabe nada de la tabla `usuario`: hay que traducirle nuestra
 * entidad a su formato (UserDetails). El "username" para nosotros es el
 * correo, porque es la columna UNIQUE con la que la gente inicia sesion.
 *
 * El rol se entrega como "ROLE_ADMINISTRADOR", "ROLE_VENDEDOR", etc.
 * El prefijo ROLE_ es una convencion que Spring Security espera.
 */
@Service
public class DetalleUsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public DetalleUsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe un usuario con el correo " + correo));

        return new org.springframework.security.core.userdetails.User(
                usuario.getCorreo(),
                usuario.getClave(),                       // el hash BCrypt
                Boolean.TRUE.equals(usuario.getActivo()),  // un usuario desactivado no entra
                true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + nombreRol(usuario))));
    }

    /** ADMINISTRADOR, VENDEDOR, TECNICO... en mayusculas y sin espacios. */
    private String nombreRol(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getNombre() == null) {
            return "SIN_ROL";
        }
        return usuario.getRol().getNombre().toUpperCase().replace(' ', '_');
    }
}
