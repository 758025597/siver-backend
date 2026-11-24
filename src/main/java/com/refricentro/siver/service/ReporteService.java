package com.refricentro.siver.service;

import com.refricentro.siver.dto.TablaReporte;
import java.time.LocalDate;

/** Reportes con filtros. Cada uno devuelve un TablaReporte exportable a PDF o Excel. */
public interface ReporteService {

    TablaReporte ventas(LocalDate desde, LocalDate hasta, String estado, Integer idUsuario, String metodoPago);

    /** Valorizacion del inventario y rotacion de cada producto en los ultimos `dias`. */
    TablaReporte inventario(Integer idCategoria, boolean soloStockBajo, boolean soloSinRotacion, int dias);

    /** Kardex de un producto: entradas, salidas y saldo. */
    TablaReporte kardex(Integer idProducto, LocalDate desde, LocalDate hasta);

    TablaReporte ordenes(LocalDate desde, LocalDate hasta, Integer idEstado, Integer idTecnico);

    /** El comprobante de una venta en PDF. */
    byte[] comprobante(Integer idVenta);
}
