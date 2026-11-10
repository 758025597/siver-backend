package com.refricentro.siver.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.refricentro.siver.model.Cliente;
import com.refricentro.siver.model.DetalleVenta;
import com.refricentro.siver.model.Producto;
import com.refricentro.siver.model.Usuario;
import com.refricentro.siver.model.Venta;
import com.refricentro.siver.repository.ClienteRepository;
import com.refricentro.siver.repository.ProductoRepository;
import com.refricentro.siver.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Comprueba el ROLLBACK de una venta contra la base MySQL real.
 *
 * El caso: una venta de dos lineas.
 *   linea 1  producto A, 1 unidad          -> entra bien. El trigger
 *            trg_detalle_venta_after_insert ya descuenta el stock de A y
 *            escribe su fila en movimiento_inventario.
 *   linea 2  producto B, stock + 1 unidades -> el trigger
 *            trg_detalle_venta_before_insert lanza 'Stock insuficiente'.
 *
 * O sea, el error ocurre A MITAD del proceso, cuando ya se habian escrito la
 * cabecera, la primera linea, el stock y la bitacora. Como todo va dentro de
 * un @Transactional, la base debe quedar EXACTAMENTE como estaba.
 *
 * Para verlo se cuenta directo en las tablas con JdbcTemplate (SQL puro),
 * antes y despues. OJO: la prueba NO lleva @Transactional a proposito: si lo
 * llevara, la prueba misma haria rollback al final y no demostraria nada.
 *
 * Requiere MySQL encendido con siver_bd, igual que SiverApplicationTests.
 */
@SpringBootTest
class VentaRollbackTest {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void siUnaLineaFalla_noQuedaNadaGuardado() {
        // ---- Datos de la prueba, tomados de lo que ya hay en la base ----
        List<Producto> conStock = productoRepository.findByActivoTrue().stream()
                .filter(p -> p.getStock() != null && p.getStock() > 0)
                .toList();
        assertThat(conStock).as("hacen falta 2 productos activos con stock").hasSizeGreaterThanOrEqualTo(2);
        Producto a = conStock.get(0);
        Producto b = conStock.get(1);
        Cliente cliente = clienteRepository.findByActivoTrue().getFirst();
        Usuario vendedor = usuarioRepository.findByActivoTrue().getFirst();
        String comprobante = "RB-" + System.currentTimeMillis();

        // ---- Foto de la base ANTES ----
        int ventasAntes = contar("SELECT COUNT(*) FROM venta");
        int detallesAntes = contar("SELECT COUNT(*) FROM detalle_venta");
        int movimientosAntes = contar("SELECT COUNT(*) FROM movimiento_inventario");
        int stockAAntes = stock(a.getIdProducto());
        int stockBAntes = stock(b.getIdProducto());

        // ---- La venta que falla en la segunda linea ----
        Venta venta = new Venta();
        venta.setNumeroComprobante(comprobante);
        venta.setTipoComprobante(Venta.TipoComprobante.BOLETA);
        venta.setCliente(cliente);
        venta.setUsuario(vendedor);

        List<DetalleVenta> lineas = List.of(
                linea(a, 1),                    // esta SI pasa
                linea(b, stockBAntes + 1));     // esta NO: pide mas de lo que hay

        Exception error = assertThrows(Exception.class, () -> ventaService.registrar(venta, lineas));
        assertThat(mensajeCompleto(error)).contains("Stock insuficiente");

        // ---- Foto de la base DESPUES: tiene que ser identica ----
        assertThat(contar("SELECT COUNT(*) FROM venta WHERE numero_comprobante = ?", comprobante))
                .as("la cabecera de la venta NO debe quedar guardada").isZero();
        assertThat(contar("SELECT COUNT(*) FROM venta")).isEqualTo(ventasAntes);
        assertThat(contar("SELECT COUNT(*) FROM detalle_venta"))
                .as("la primera linea tambien se deshizo").isEqualTo(detallesAntes);
        assertThat(contar("SELECT COUNT(*) FROM movimiento_inventario"))
                .as("el movimiento que escribio el trigger tambien se deshizo").isEqualTo(movimientosAntes);
        assertThat(stock(a.getIdProducto()))
                .as("el stock que descontó la primera linea volvio a su valor").isEqualTo(stockAAntes);
        assertThat(stock(b.getIdProducto())).isEqualTo(stockBAntes);
    }

    private DetalleVenta linea(Producto producto, int cantidad) {
        DetalleVenta d = new DetalleVenta();
        d.setProducto(producto);
        d.setCantidad(cantidad);
        d.setPrecioUnitario(producto.getPrecioVenta());
        d.setDescuento(BigDecimal.ZERO);
        d.setSubtotal(BigDecimal.ZERO); // lo calcula el trigger
        return d;
    }

    private int contar(String sql, Object... parametros) {
        return jdbc.queryForObject(sql, Integer.class, parametros);
    }

    private int stock(Integer idProducto) {
        return contar("SELECT stock FROM producto WHERE id_producto = ?", idProducto);
    }

    /** El mensaje de MySQL viene envuelto en varias excepciones: se juntan todas. */
    private static String mensajeCompleto(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (Throwable x = t; x != null; x = x.getCause()) {
            sb.append(x.getMessage()).append(" | ");
        }
        return sb.toString();
    }
}
