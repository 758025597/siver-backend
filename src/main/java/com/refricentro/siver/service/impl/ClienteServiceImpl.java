package com.refricentro.siver.service.impl;

import com.refricentro.siver.model.Cliente;
import com.refricentro.siver.repository.ClienteRepository;
import com.refricentro.siver.repository.IGenericRepository;
import com.refricentro.siver.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl
        extends ServicioSoftDelete<Cliente, Integer>
        implements ClienteService {

    private final ClienteRepository repo;

    @Override
    protected IGenericRepository<Cliente, Integer> getRepo() {
        return repo;
    }

    @Override
    protected String nombreRecurso() {
        return "Cliente";
    }

    @Override
    protected void copiarDatos(Cliente origen, Cliente destino) {
        destino.setTipoDocumento(origen.getTipoDocumento());
        destino.setNumeroDocumento(origen.getNumeroDocumento());
        destino.setNombres(origen.getNombres());
        destino.setTelefono(origen.getTelefono());
        destino.setCorreo(origen.getCorreo());
        destino.setDireccion(origen.getDireccion());
    }

    @Override
    public List<Cliente> listarActivos() {
        return repo.findByActivoTrue();
    }
}