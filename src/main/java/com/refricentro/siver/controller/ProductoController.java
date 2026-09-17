package com.refricentro.siver.controller;

import com.refricentro.siver.dto.ProductoRequest;
import com.refricentro.siver.dto.ProductoResponse;
import com.refricentro.siver.modelos.Producto;
import com.refricentro.siver.service.CategoriaService;
import com.refricentro.siver.service.ProductoService;
import com.refricentro.siver.service.ProveedorService;
import java.util.List;
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
public class ProductoController
        extends ControladorGenerico<Producto, Integer, ProductoRequest, ProductoResponse> {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;

    public ProductoController(ProductoService productoService,
                              CategoriaService categoriaService,
                              ProveedorService proveedorService) {
        super(productoService);
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.proveedorService = proveedorService;
    }

    @Override
    protected Producto aEntidad(ProductoRequest request) {
        Producto producto = new Producto();

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());

        // Se buscan de verdad, no se arma un objeto vacio con el id:
        // asi se valida que existan y el error sale como 404, no como 409.
        producto.setCategoria(categoriaService.buscarPorId(request.getIdCategoria()));

        if (request.getIdProveedor() != null) {
            producto.setProveedor(proveedorService.buscarPorId(request.getIdProveedor()));
        }

        producto.setUnidadMedida(request.getUnidadMedida());
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setStockMinimo(request.getStockMinimo());

        // El stock solo se usa al CREAR. En el update, copiarDatos() lo
        // ignora a proposito para no pisar lo que hacen los triggers.
        producto.setStock(request.getStock());

        return producto;
    }

    @Override
    protected ProductoResponse aRespuesta(Producto producto) {
        return new ProductoResponse(
                producto.getIdProducto(),
                producto.getCodigo(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getCategoria() != null ? producto.getCategoria().getIdCat() : null,
                producto.getProveedor() != null ? producto.getProveedor().getIdProveedor() : null,
                producto.getUnidadMedida(),
                producto.getPrecioCompra(),
                producto.getPrecioVenta(),
                producto.getStock(),
                producto.getStockMinimo(),
                producto.getActivo(),
                producto.getFechaRegistro()
        );
    }

    /** GET /api/productos/activos - solo los vigentes. */
    @GetMapping("/activos")
    public ResponseEntity<List<ProductoResponse>> listarActivos() {
        return ResponseEntity.ok(productoService.listarActivos().stream()
                .map(this::aRespuesta)
                .toList());
    }

    /**
     * GET /api/productos/stock-bajo - los que llegaron al minimo.
     *
     * El servicio ya tenia este metodo pero nadie lo habia expuesto, asi que
     * la URL caia en el GET /{id} heredado e intentaba convertir "stock-bajo"
     * en un numero. Sirve para la alerta de reposicion del dashboard.
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponse>> listarConStockBajo() {
        return ResponseEntity.ok(productoService.buscarConStockBajo().stream()
                .map(this::aRespuesta)
                .toList());
    }
}
