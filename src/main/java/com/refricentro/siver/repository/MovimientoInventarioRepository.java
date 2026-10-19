
package com.refricentro.siver.repository;

import com.refricentro.siver.model.MovimientoInventario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
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
}
