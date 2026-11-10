
package com.refricentro.siver.repository;

import com.refricentro.siver.model.Venta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Acceso a datos de la cabecera de las ventas. */
@Repository
public interface VentaRepository extends IGenericRepository<Venta, Integer> {

    /** El comprobante es único por tipo. */
    Optional<Venta> findByTipoComprobanteAndNumeroComprobante(
            Venta.TipoComprobante tipoComprobante, String numeroComprobante);

    boolean existsByTipoComprobanteAndNumeroComprobante(
            Venta.TipoComprobante tipoComprobante, String numeroComprobante);

    List<Venta> findByEstado(Venta.EstadoVenta estado);

    List<Venta> findByCliente_IdCliente(Integer idCliente);

    List<Venta> findByUsuario_IdUsuario(Integer idUsuario);

    /** Para reportes de ventas por período. */
    List<Venta> findByFechaVentaBetween(LocalDateTime desde, LocalDateTime hasta);

    @Override
    @EntityGraph(attributePaths = {"cliente", "usuario"})
    List<Venta> findAll();

    @Override
    @EntityGraph(attributePaths = {"cliente", "usuario"})
    Optional<Venta> findById(Integer id);

    @Override
    @EntityGraph(attributePaths = {"cliente", "usuario"})
    Page<Venta> findAll(Pageable pageable);

    /**
     * Busqueda paginada por numero de comprobante o nombre del cliente.
     *
     * JPQL con parametro NOMBRADO: el texto viaja como :texto y se enlaza con
     * @Param. Nunca se concatena dentro de la consulta, asi que un texto como
     * "' OR 1=1 --" se busca tal cual y no puede inyectar SQL.
     */
    @EntityGraph(attributePaths = {"cliente", "usuario"})
    @Query("""
        SELECT v FROM Venta v JOIN v.cliente c
         WHERE LOWER(v.numeroComprobante) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(c.nombres) LIKE LOWER(CONCAT('%', :texto, '%'))
        """)
    Page<Venta> buscar(@Param("texto") String texto, Pageable pageable);
}
