package com.refricentro.siver.controller;

import com.refricentro.siver.dto.TablaReporte;
import com.refricentro.siver.reporte.ExportadorExcel;
import com.refricentro.siver.reporte.ExportadorPdf;
import com.refricentro.siver.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reportes con filtros y exportacion.
 *
 * Todos aceptan ?formato=json (por defecto), pdf o xlsx:
 *   GET /api/reportes/ventas?desde=2026-11-01&hasta=2026-11-30&formato=pdf
 *
 * Los permisos van por metodo con @PreAuthorize (EnableMethodSecurity):
 * ventas e inventario para ADMINISTRADOR y VENDEDOR; ordenes para
 * ADMINISTRADOR y TECNICO.
 */
@Tag(name = "Reportes", description = "Reportes con filtros y exportacion a PDF y Excel.")
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private static final MediaType XLSX =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ReporteService servicio;
    private final ExportadorPdf exportadorPdf;
    private final ExportadorExcel exportadorExcel;

    @Operation(summary = "Reporte de ventas por periodo, estado, vendedor y metodo de pago")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    @GetMapping("/ventas")
    public ResponseEntity<?> ventas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @Parameter(description = "EMITIDA o ANULADA") @RequestParam(required = false) String estado,
            @Parameter(description = "Id del vendedor") @RequestParam(required = false) Integer idUsuario,
            @RequestParam(required = false) String metodoPago,
            @Parameter(description = "json, pdf o xlsx") @RequestParam(defaultValue = "json") String formato) {
        return responder(servicio.ventas(desde, hasta, estado, idUsuario, metodoPago), formato, "ventas");
    }

    @Operation(summary = "Inventario valorizado, stock bajo y productos sin rotacion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    @GetMapping("/inventario")
    public ResponseEntity<?> inventario(
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(defaultValue = "false") boolean soloStockBajo,
            @RequestParam(defaultValue = "false") boolean soloSinRotacion,
            @Parameter(description = "Dias hacia atras para medir la rotacion") @RequestParam(defaultValue = "30") int dias,
            @RequestParam(defaultValue = "json") String formato) {
        return responder(servicio.inventario(idCategoria, soloStockBajo, soloSinRotacion, dias),
                formato, "inventario");
    }

    @Operation(summary = "Kardex de un producto (entradas, salidas y saldo)")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    @GetMapping("/kardex/{idProducto}")
    public ResponseEntity<?> kardex(
            @PathVariable Integer idProducto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "json") String formato) {
        return responder(servicio.kardex(idProducto, desde, hasta), formato, "kardex-" + idProducto);
    }

    @Operation(summary = "Ordenes de servicio por periodo, estado y tecnico")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TECNICO')")
    @GetMapping("/ordenes")
    public ResponseEntity<?> ordenes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer idEstado,
            @RequestParam(required = false) Integer idTecnico,
            @RequestParam(defaultValue = "json") String formato) {
        return responder(servicio.ordenes(desde, hasta, idEstado, idTecnico), formato, "ordenes");
    }

    @Operation(summary = "Comprobante de una venta en PDF (boleta, factura o nota de venta)")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    @GetMapping("/ventas/{idVenta}/comprobante")
    public ResponseEntity<byte[]> comprobante(@PathVariable Integer idVenta) {
        return archivo(servicio.comprobante(idVenta), MediaType.APPLICATION_PDF,
                "comprobante-" + idVenta + ".pdf", true);
    }

    private ResponseEntity<?> responder(TablaReporte reporte, String formato, String nombre) {
        String sello = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return switch (formato.toLowerCase()) {
            case "pdf" -> archivo(exportadorPdf.exportar(reporte), MediaType.APPLICATION_PDF,
                    "siver-" + nombre + "-" + sello + ".pdf", false);
            case "xlsx", "excel" -> archivo(exportadorExcel.exportar(reporte), XLSX,
                    "siver-" + nombre + "-" + sello + ".xlsx", false);
            case "json" -> ResponseEntity.ok(reporte);
            default -> throw new IllegalArgumentException("Formato no valido: usa json, pdf o xlsx.");
        };
    }

    private static ResponseEntity<byte[]> archivo(byte[] contenido, MediaType tipo, String nombre, boolean enLinea) {
        ContentDisposition disposicion = (enLinea ? ContentDisposition.inline() : ContentDisposition.attachment())
                .filename(nombre).build();
        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicion.toString())
                .body(contenido);
    }
}
