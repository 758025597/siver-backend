package com.refricentro.siver.security;

import java.text.Normalizer;

/**
 * Los roles del sistema, tal como los entiende Spring Security.
 *
 * En la tabla rol el nombre es "Administrador", "Vendedor", "Tecnico".
 * Spring los recibe como ROLE_ADMINISTRADOR, ROLE_VENDEDOR, ROLE_TECNICO
 * (ver DetalleUsuarioService). Aqui van sin el prefijo ROLE_, que es como
 * los piden hasRole() y hasAnyRole().
 *
 * Permisos de cada rol:
 *   ADMINISTRADOR  todo: usuarios, roles, catalogos, auditoria, anular ventas
 *   VENDEDOR       clientes, ventas, inventario (consulta) y reportes de venta
 *   TECNICO        clientes y ordenes de servicio
 */
public final class Roles {

    public static final String ADMIN = "ADMINISTRADOR";
    public static final String VENDEDOR = "VENDEDOR";
    public static final String TECNICO = "TECNICO";

    private Roles() {
    }

    /** "Técnico de campo" -> "TECNICO_DE_CAMPO": sin tildes, mayusculas, sin espacios. */
    public static String normalizar(String nombreRol) {
        if (nombreRol == null || nombreRol.isBlank()) {
            return "SIN_ROL";
        }
        String sinTildes = Normalizer.normalize(nombreRol.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.toUpperCase().replaceAll("\\s+", "_");
    }
}
