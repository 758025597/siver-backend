package com.refricentro.siver.config;

import com.refricentro.siver.dto.DetalleVentaRequest;
import com.refricentro.siver.dto.DetalleVentaResponse;
import com.refricentro.siver.dto.EstadoOrdenResponse;
import com.refricentro.siver.dto.MovimientoInventarioResponse;
import com.refricentro.siver.dto.OrdenServicioRequest;
import com.refricentro.siver.dto.OrdenServicioResponse;
import com.refricentro.siver.dto.ProductoRequest;
import com.refricentro.siver.dto.ProductoResponse;
import com.refricentro.siver.dto.RolResponse;
import com.refricentro.siver.dto.UsuarioRequest;
import com.refricentro.siver.dto.UsuarioResponse;
import com.refricentro.siver.dto.VentaRequest;
import com.refricentro.siver.dto.VentaResponse;
import com.refricentro.siver.model.DetalleVenta;
import com.refricentro.siver.model.EstadoOrden;
import com.refricentro.siver.model.MovimientoInventario;
import com.refricentro.siver.model.OrdenServicio;
import com.refricentro.siver.model.Producto;
import com.refricentro.siver.model.Rol;
import com.refricentro.siver.model.Usuario;
import com.refricentro.siver.model.Venta;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Los ModelMapper del proyecto.
 *
 * ModelMapper copia solo los atributos que se llaman igual en el DTO y en la
 * entidad. Por eso hay dos tipos de mapper:
 *
 *   defaultMapper   para las entidades cuyos DTO tienen los mismos nombres
 *                   (Categoria, Cliente, Proveedor).
 *
 *   xxxMapper       para las que tienen nombres distintos. Por ejemplo, en
 *                   Producto la entidad tiene `categoria` (un objeto) y el
 *                   DTO tiene `idCategoria` (un numero): hay que decirle a
 *                   ModelMapper que idCategoria sale de categoria.idCat.
 *
 * Cada controlador elige el suyo con @Qualifier("nombreDelBean").
 *
 * Se usa la estrategia STRICT: solo empareja nombres exactos. La estandar
 * "adivina" parecidos (idCategoria con categoria.idCat, idUsuario con
 * usuario.rol.idRol...) y puede llenar un campo con el dato equivocado sin
 * que nadie se entere.
 *
 * Se lee por getters y no por atributos: las relaciones LAZY llegan como
 * proxies de Hibernate, y los atributos de un proxy estan vacios.
 *
 * Al pasar de Request a entidad, los ids de las relaciones (idCategoria,
 * idRol, idCliente...) se saltan a proposito: el controlador busca la entidad
 * real con su servicio, asi valida que exista y responde 404 si no.
 */
@Configuration
public class MapperConfig {

    /** Crea un ModelMapper con la configuracion comun del proyecto. */
    private ModelMapper nuevoMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper;
    }

    /**
     * El mapper por defecto: nombres iguales en DTO y entidad.
     * @Primary hace que sea el que se inyecta cuando no se pone @Qualifier.
     */
    @Bean("defaultMapper")
    @Primary
    public ModelMapper defaultMapper() {
        return nuevoMapper();
    }

    /** Rol: la entidad usa idRol y el DTO usa id. */
    @Bean("rolMapper")
    public ModelMapper rolMapper() {
        ModelMapper mapper = nuevoMapper();
        mapper.createTypeMap(Rol.class, RolResponse.class)
                .addMapping(Rol::getIdRol, RolResponse::setId);
        return mapper;
    }

    /** EstadoOrden: la entidad usa idEstado y el DTO usa id. */
    @Bean("estadoOrdenMapper")
    public ModelMapper estadoOrdenMapper() {
        ModelMapper mapper = nuevoMapper();
        mapper.createTypeMap(EstadoOrden.class, EstadoOrdenResponse.class)
                .addMapping(EstadoOrden::getIdEstado, EstadoOrdenResponse::setId);
        return mapper;
    }

    /** Usuario: id, y el rol aplanado en idRol + nombreRol. La clave nunca sale. */
    @Bean("usuarioMapper")
    public ModelMapper usuarioMapper() {
        ModelMapper mapper = nuevoMapper();
        mapper.createTypeMap(Usuario.class, UsuarioResponse.class)
                .addMapping(Usuario::getIdUsuario, UsuarioResponse::setId)
                .addMapping(u -> u.getRol().getIdRol(), UsuarioResponse::setIdRol)
                .addMapping(u -> u.getRol().getNombre(), UsuarioResponse::setNombreRol);
        mapper.createTypeMap(UsuarioRequest.class, Usuario.class)
                .addMappings(m -> m.skip(Usuario::setRol));
        return mapper;
    }

    /** Producto: categoria y proveedor viajan como ids. */
    @Bean("productoMapper")
    public ModelMapper productoMapper() {
        ModelMapper mapper = nuevoMapper();
        mapper.createTypeMap(Producto.class, ProductoResponse.class)
                .addMapping(p -> p.getCategoria().getIdCat(), ProductoResponse::setIdCategoria)
                .addMapping(p -> p.getProveedor().getIdProveedor(), ProductoResponse::setIdProveedor);
        mapper.createTypeMap(ProductoRequest.class, Producto.class)
                .addMappings(m -> {
                    m.skip(Producto::setCategoria);
                    m.skip(Producto::setProveedor);
                });
        return mapper;
    }

    /** OrdenServicio: id, y cliente, tecnico y estado viajan como ids. */
    @Bean("ordenServicioMapper")
    public ModelMapper ordenServicioMapper() {
        ModelMapper mapper = nuevoMapper();
        mapper.createTypeMap(OrdenServicio.class, OrdenServicioResponse.class)
                .addMapping(OrdenServicio::getIdOrden, OrdenServicioResponse::setId)
                .addMapping(o -> o.getCliente().getIdCliente(), OrdenServicioResponse::setIdCliente)
                .addMapping(o -> o.getTecnico().getIdUsuario(), OrdenServicioResponse::setIdUsuario)
                .addMapping(o -> o.getEstado().getIdEstado(), OrdenServicioResponse::setIdEstado)
                .addMapping(o -> o.getEstado().getNombre(), OrdenServicioResponse::setNombreEstado);
        mapper.createTypeMap(OrdenServicioRequest.class, OrdenServicio.class)
                .addMappings(m -> {
                    m.skip(OrdenServicio::setCliente);
                    m.skip(OrdenServicio::setTecnico);
                    m.skip(OrdenServicio::setEstado);
                });
        return mapper;
    }

    /** Venta y sus lineas. */
    @Bean("ventaMapper")
    public ModelMapper ventaMapper() {
        ModelMapper mapper = nuevoMapper();
        mapper.createTypeMap(Venta.class, VentaResponse.class)
                .addMapping(Venta::getIdVenta, VentaResponse::setId)
                .addMapping(v -> v.getCliente().getIdCliente(), VentaResponse::setIdCliente)
                .addMapping(v -> v.getCliente().getNombres(), VentaResponse::setNombreCliente)
                .addMapping(v -> v.getUsuario().getIdUsuario(), VentaResponse::setIdUsuario)
                // Las lineas las arma el controlador con listarDetalles().
                .addMappings(m -> m.skip(VentaResponse::setDetalles));
        mapper.createTypeMap(DetalleVenta.class, DetalleVentaResponse.class)
                .addMapping(DetalleVenta::getIdDetalle, DetalleVentaResponse::setId)
                .addMapping(d -> d.getProducto().getIdProducto(), DetalleVentaResponse::setIdProducto)
                .addMapping(d -> d.getProducto().getNombre(), DetalleVentaResponse::setNombreProducto);
        mapper.createTypeMap(VentaRequest.class, Venta.class)
                .addMappings(m -> {
                    m.skip(Venta::setCliente);
                    m.skip(Venta::setUsuario);
                    m.skip(Venta::setDetalles);
                });
        mapper.createTypeMap(DetalleVentaRequest.class, DetalleVenta.class)
                .addMappings(m -> m.skip(DetalleVenta::setProducto));
        return mapper;
    }

    /** Movimiento de inventario: producto, usuario y venta viajan como ids. */
    @Bean("movimientoMapper")
    public ModelMapper movimientoMapper() {
        ModelMapper mapper = nuevoMapper();
        mapper.createTypeMap(MovimientoInventario.class, MovimientoInventarioResponse.class)
                .addMapping(m -> m.getProducto().getIdProducto(), MovimientoInventarioResponse::setIdProducto)
                .addMapping(m -> m.getUsuario().getIdUsuario(), MovimientoInventarioResponse::setIdUsuario)
                .addMapping(m -> m.getVenta().getIdVenta(), MovimientoInventarioResponse::setIdVenta);
        return mapper;
    }
}
