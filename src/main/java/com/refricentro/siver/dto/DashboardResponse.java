package com.refricentro.siver.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Los indicadores del panel principal.
 *
 * Se adapta al rol: un TECNICO no recibe montos de venta (llegan en null y
 * con listas vacias) y un VENDEDOR no recibe los datos de ordenes.
 */
public record DashboardResponse(
        String rol,

        // --- Ventas (ADMINISTRADOR y VENDEDOR) ---
        Long ventasHoy,
        BigDecimal montoHoy,
        Long ventasMes,
        BigDecimal montoMes,
        BigDecimal ticketPromedioMes,
        List<PuntoSerie> ventasUltimos7Dias,
        List<Etiqueta> ventasPorMetodoPago,
        List<TopProducto> productosMasVendidos,

        // --- Inventario ---
        Long productosActivos,
        Long productosStockBajo,
        BigDecimal valorInventario,
        List<StockBajo> stockBajo,

        // --- Clientes ---
        Long clientesActivos,

        // --- Ordenes de servicio (ADMINISTRADOR y TECNICO) ---
        Long ordenesAbiertas,
        Long ordenesVencidas,
        Long ordenesFinalizadasMes,
        List<Etiqueta> ordenesPorEstado) {

    public record PuntoSerie(LocalDate fecha, long cantidad, BigDecimal monto) {
    }

    public record Etiqueta(String etiqueta, BigDecimal valor) {
    }

    public record TopProducto(String codigo, String nombre, long cantidad, BigDecimal monto) {
    }

    public record StockBajo(Integer idProducto, String codigo, String nombre, int stock, int stockMinimo) {
    }
}
