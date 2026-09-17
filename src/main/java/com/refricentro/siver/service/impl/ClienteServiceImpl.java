package com.refricentro.siver.service.impl;

import com.refricentro.siver.modelos.Cliente;
import com.refricentro.siver.repository.ClienteRepository;
import com.refricentro.siver.service.ClienteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl
        extends ServicioSoftDelete<Cliente, Integer>
        implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        super(clienteRepository);
        this.clienteRepository = clienteRepository;
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
        return clienteRepository.findByActivoTrue();
    }
}