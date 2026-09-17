package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    List<Proveedor> findByActivoTrue();

    Optional<Proveedor> findByIdProveedorAndActivoTrue(Integer idProveedor);

    @Query("SELECT p FROM Proveedor p WHERE p.ruc = :ruc")
    Optional<Proveedor> findByRuc(String ruc);

    boolean existsByRuc(String ruc);
}