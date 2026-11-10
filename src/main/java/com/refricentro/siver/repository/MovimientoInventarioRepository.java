
package com.refricentro.siver.repository;

import com.refricentro.siver.model.MovimientoInventario;
import com.refricentro.siver.model.enums.TipoMovimiento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoInventarioRepository
        extends IGenericRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByProducto_IdProducto(Integer idProducto);

    List<MovimientoInventario> findByUsuario_IdUsuario(Integer idUsuario);

    List<MovimientoInventario> findByVenta_IdVenta(Integer idVenta);

    @Override
    @EntityGraph(attributePaths = {"producto", "usuario", "venta"})
    List<MovimientoInventario> findAll();

    @Override
    @EntityGraph(attributePaths = {"producto", "usuario", "venta"})
    Optional<MovimientoInventario> findById(Integer id);

    /**
     * Kardex paginado, filtrado por texto, tipo de movimiento y producto.
     *
     * Tres parametros NOMBRADOS, cada uno con su @Param:
     *   :texto       busca en el codigo y nombre del producto y en el motivo
     *                (vacio = no filtra por texto)
     *   :tipo        ENTRADA, SALIDA o AJUSTE (null = todos los tipos)
     *   :idProducto  el kardex de un solo producto (null = todos)
     */
    @EntityGraph(attributePaths = {"producto", "usuario", "venta"})
    @Query("""
        SELECT m FROM MovimientoInventario m JOIN m.producto p
         WHERE (:tipo IS NULL OR m.tipoMovimiento = :tipo)
           AND (:idProducto IS NULL OR p.idProducto = :idProducto)
           AND (:texto = ''
                OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(m.motivo) LIKE LOWER(CONCAT('%', :texto, '%')))
        """)
    Page<MovimientoInventario> buscar(@Param("texto") String texto,
                                      @Param("tipo") TipoMovimiento tipo,
                                      @Param("idProducto") Integer idProducto,
                                      Pageable pageable);
}
