package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository
        extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByIdProducto(Integer idProducto);

    List<MovimientoInventario> findByIdUsuario(Integer idUsuario);

    List<MovimientoInventario> findByIdVenta(Integer idVenta);
}