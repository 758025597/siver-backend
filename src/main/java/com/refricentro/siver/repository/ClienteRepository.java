package com.refricentro.siver.repository;

import com.refricentro.siver.modelos.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    List<Cliente> findByActivoTrue();

    Optional<Cliente> findByIdClienteAndActivoTrue(Integer idCliente);

    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);
}