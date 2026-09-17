package com.refricentro.siver.controller;

import com.refricentro.siver.dto.ProductoRequest;
import com.refricentro.siver.dto.ProductoResponse;
import com.refricentro.siver.modelos.Categoria;
import com.refricentro.siver.modelos.Producto;
import com.refricentro.siver.modelos.Proveedor;
import com.refricentro.siver.service.ProductoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
public class ProductoController
        extends ControladorGenerico<Producto, Integer, ProductoRequest, ProductoResponse> {

    public ProductoController(ProductoService servicio) {
        super(servicio);
    }

    @Override
    protected Producto aEntidad(ProductoRequest request) {
        Producto producto = new Producto();

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());

        Categoria categoria = new Categoria();
        categoria.setIdCat(request.getIdCategoria());
        producto.setCategoria(categoria);

        if (request.getIdProveedor() != null) {
            Proveedor proveedor = new Proveedor();
            proveedor.setIdProveedor(request.getIdProveedor());
            producto.setProveedor(proveedor);
        }

        producto.setUnidadMedida(request.getUnidadMedida());
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setStock(request.getStock());
        producto.setStockMinimo(request.getStockMinimo());

        return producto;
    }

    @Override
    protected ProductoResponse aRespuesta(Producto producto) {
        return new ProductoResponse(
                producto.getIdProducto(),
                producto.getCodigo(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getCategoria() != null
                        ? producto.getCategoria().getIdCat()
                        : null,
                producto.getProveedor() != null
                        ? producto.getProveedor().getIdProveedor()
                        : null,
                producto.getUnidadMedida(),
                producto.getPrecioCompra(),
                producto.getPrecioVenta(),
                producto.getStock(),
                producto.getStockMinimo(),
                producto.getActivo(),
                producto.getFechaRegistro()
        );
    }
}