package com.refricentro.siver.service.impl;

import com.refricentro.siver.modelos.Proveedor;
import com.refricentro.siver.repository.ProveedorRepository;
import com.refricentro.siver.service.ProveedorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorServiceImpl
        extends ServicioSoftDelete<Proveedor, Integer>
        implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        super(proveedorRepository);
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    protected String nombreRecurso() {
        return "Proveedor";
    }

    @Override
    protected void copiarDatos(Proveedor origen, Proveedor destino) {
        destino.setRuc(origen.getRuc());
        destino.setRazonSocial(origen.getRazonSocial());
        destino.setContacto(origen.getContacto());
        destino.setTelefono(origen.getTelefono());
        destino.setCorreo(origen.getCorreo());
        destino.setDireccion(origen.getDireccion());
    }

    @Override
    public List<Proveedor> listarActivos() {
        return proveedorRepository.findByActivoTrue();
    }
}