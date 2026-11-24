package com.refricentro.siver.service.impl;

import com.refricentro.siver.model.Proveedor;
import com.refricentro.siver.repository.IGenericRepository;
import com.refricentro.siver.repository.ProveedorRepository;
import com.refricentro.siver.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl
        extends ServicioSoftDelete<Proveedor, Integer>
        implements ProveedorService {

    private final ProveedorRepository repo;

    @Override
    protected IGenericRepository<Proveedor, Integer> getRepo() {
        return repo;
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
        return repo.findByActivoTrue();
    }

    /** Busqueda paginada con la consulta JPQL del repositorio. */
    @Override
    protected Page<Proveedor> buscar(String texto, Pageable pageable) {
        return repo.buscar(texto, pageable);
    }
}
