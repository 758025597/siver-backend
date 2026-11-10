
package com.refricentro.siver.repository;

import com.refricentro.siver.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends IGenericRepository<Producto, Integer> {

    List<Producto> findByActivoTrue();

    Optional<Producto> findByIdProductoAndActivoTrue(Integer idProducto);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stock <= p.stockMinimo")
    List<Producto> findConStockBajo();

    /**
     * Busqueda paginada por codigo, nombre, descripcion, categoria o proveedor.
     *
     * JPQL con parametro NOMBRADO: el texto viaja como :texto y se enlaza con
     * @Param. Nunca se concatena dentro de la consulta, asi que un texto como
     * "' OR 1=1 --" se busca tal cual y no puede inyectar SQL.
     */
    @Query("""
        SELECT p FROM Producto p LEFT JOIN p.categoria c LEFT JOIN p.proveedor pr
         WHERE LOWER(p.codigo) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(pr.razonSocial) LIKE LOWER(CONCAT('%', :texto, '%'))
        """)
    Page<Producto> buscar(@Param("texto") String texto, Pageable pageable);
}
