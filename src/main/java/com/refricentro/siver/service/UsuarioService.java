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

    /** Busca por correo. Sirve para el login mas adelante. */
    Usuario buscarPorCorreo(String correo);
}
