package com.refricentro.siver.service.impl;

import com.refricentro.siver.modelos.Producto;
import com.refricentro.siver.repository.ProductoRepository;
import com.refricentro.siver.service.ProductoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl
        extends ServicioSoftDelete<Producto, Integer>
        implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        super(productoRepository);
        this.productoRepository = productoRepository;
    }

    @Override
    protected String nombreRecurso() {
        return "Producto";
    }

    @Override
    protected void copiarDatos(Producto origen, Producto destino) {
        destino.setCodigo(origen.getCodigo());
        destino.setNombre(origen.getNombre());
        destino.setDescripcion(origen.getDescripcion());
        destino.setCategoria(origen.getCategoria());
        destino.setProveedor(origen.getProveedor());
        destino.setUnidadMedida(origen.getUnidadMedida());
        destino.setPrecioCompra(origen.getPrecioCompra());
        destino.setPrecioVenta(origen.getPrecioVenta());
        destino.setStock(origen.getStock());
        destino.setStockMinimo(origen.getStockMinimo());
    }

    @Override
    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrue();
    }

    @Override
    public List<Producto> buscarConStockBajo() {
        return productoRepository.findConStockBajo();
    }
}