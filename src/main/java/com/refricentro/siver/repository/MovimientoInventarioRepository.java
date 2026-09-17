package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.MovimientoInventario;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository
        extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByProducto_IdProducto(Integer idProducto);

    List<MovimientoInventario> findByUsuario_IdUsuario(Integer idUsuario);

    List<MovimientoInventario> findByVenta_IdVenta(Integer idVenta);

    /**
     * Trae MovimientoInventario junto con sus relaciones en un solo SELECT con JOIN.
     *
     * Sin esto, al armar la respuesta fuera de la transaccion Hibernate
     * lanzaria LazyInitializationException, porque las relaciones son LAZY.
     */
    @Override
    @EntityGraph(attributePaths = {"producto", "usuario", "venta"})
    List<MovimientoInventario> findAll();

    @Override
    @EntityGraph(attributePaths = {"producto", "usuario", "venta"})
    Optional<MovimientoInventario> findById(Integer id);
}
