package com.refricentro.siver.controller;

import com.refricentro.siver.dto.ProductoRequest;
import com.refricentro.siver.dto.ProductoResponse;
import com.refricentro.siver.model.Producto;
import com.refricentro.siver.service.CategoriaService;
import com.refricentro.siver.service.ProductoService;
import com.refricentro.siver.service.ProveedorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de producto.
 *
 * Hereda los 5 del CRUD de ControladorGenerico. El DELETE es logico:
 * deja activo = false, porque un producto que ya se vendio esta en
 * detalle_venta y la llave foranea es ON DELETE RESTRICT.
 *
 * Inyecta CategoriaService y ProveedorService para convertir los ids que
 * llegan en la peticion en las entidades reales. De paso quedan validados:
 * si la categoria no existe, responde 404 con un mensaje claro en vez de
 * dejar que reviente la llave foranea de MySQL.
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController
        extends ControladorGenerico<Producto, Integer, ProductoRequest, ProductoResponse> {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;

    @Qualifier("productoMapper")
    private final ModelMapper mapper;

    @Override
    protected ProductoService getServicio() {
        return productoService;
    }

    @Override
    protected ModelMapper getMapper() {
        return mapper;
    }

    @Override
    protected Integer obtenerId(Producto producto) {
        return producto.getIdProducto();
    }

    /**
     * ModelMapper copia los datos simples (codigo, precios, stock...).
     * La categoria y el proveedor se buscan de verdad, no se arma un objeto
     * vacio con el id: asi se valida que existan y el error sale como 404.
     *
     * El stock solo se usa al CREAR. En el update, copiarDatos() lo ignora
     * a proposito para no pisar lo que hacen los triggers.
     */
    @Override
    protected Producto aEntidad(ProductoRequest request) {
        Producto producto = super.aEntidad(request);
        producto.setCategoria(categoriaService.buscarPorId(request.getIdCategoria()));
        if (request.getIdProveedor() != null) {
            producto.setProveedor(proveedorService.buscarPorId(request.getIdProveedor()));
        }
        return producto;
    }

    /** GET /api/productos/activos - solo los vigentes. */
    @GetMapping("/activos")
    public ResponseEntity<List<ProductoResponse>> listarActivos() {
        return ResponseEntity.ok(aRespuestas(productoService.listarActivos()));
    }

    /**
     * GET /api/productos/stock-bajo - los que llegaron al minimo.
     * Sirve para la alerta de reposicion del dashboard.
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponse>> listarConStockBajo() {
        return ResponseEntity.ok(aRespuestas(productoService.buscarConStockBajo()));
    }
}
