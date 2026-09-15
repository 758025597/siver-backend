package com.refricentro.siver.modelos;

/**
 * La implementan las entidades que tienen columna `activo` en la base
 * y por lo tanto admiten SOFT DELETE (borrado logico).
 *
 * Son 6: Rol, Usuario, Cliente, Categoria, Proveedor y Producto.
 *
 * No hay que escribir estos metodos: Lombok ya los genera con @Getter/@Setter.
 * Basta con agregar "implements Activable" a la clase.
 */
public interface Activable {

    Boolean getActivo();

    void setActivo(Boolean activo);
}
