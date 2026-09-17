package com.refricentro.siver.service;

import com.refricentro.siver.modelos.Usuario;
import java.util.List;

/**
 * Operaciones disponibles sobre usuario.
 * Los 5 del CRUD los hereda de CrudService.
 */
public interface UsuarioService extends CrudService<Usuario, Integer> {

    /** Listado sin los usuarios desactivados. */
    List<Usuario> listarActivos();

    /** Busca por correo. Lo usa el login. */
    Usuario buscarPorCorreo(String correo);

    /**
     * Guarda la fecha del ultimo acceso tras un login correcto.
     *
     * No pasa por actualizar(): ese metodo vuelve a cifrar la clave, y aqui
     * la que trae el objeto YA es el hash. Si se recifrara, el usuario no
     * podria volver a entrar nunca.
     */
    void registrarAcceso(Usuario usuario);
}
