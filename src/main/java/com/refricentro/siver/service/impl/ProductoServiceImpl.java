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
        destino.setStockMinimo(origen.getStockMinimo());

        // OJO: el stock NO se copia a proposito.
        //
        // Lo mantienen los triggers de MySQL: trg_detalle_venta_after_insert
        // hace UPDATE producto SET stock = stock - cantidad en cada venta.
        //
        // Si se copiara, editar el nombre de un producto desde el frontend
        // reescribiria el stock con el valor que el usuario tenia en pantalla
        // y borraria las ventas descontadas mientras tanto. Es una perdida de
        // datos silenciosa: la respuesta seria 200 y nadie se enteraria.
        //
        // El stock inicial si se define al CREAR. Para ajustarlo despues esta
        // movimiento_inventario, que ademas deja registro de quien y por que.
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