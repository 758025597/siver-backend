package com.refricentro.siver.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Un reporte listo para mostrarse o exportarse.
 *
 * El mismo objeto sirve para las tres salidas:
 *   formato=json  Angular lo pinta en una tabla (vista previa)
 *   formato=pdf   ExportadorPdf lo dibuja con OpenPDF
 *   formato=xlsx  ExportadorExcel lo escribe con Apache POI
 *
 * Asi cada reporte se arma UNA vez y se exporta a cualquier formato
 * (patron Strategy: cambia el exportador, no el reporte).
 *
 * Cada celda es texto o numero. Las fechas ya vienen como texto dd/MM/yyyy.
 */
public record TablaReporte(
        String titulo,
        String filtros,
        LocalDateTime generado,
        String generadoPor,
        List<Columna> columnas,
        List<List<Object>> filas,
        List<Object> totales) {

    public enum Tipo { TEXTO, ENTERO, MONEDA }

    public record Columna(String titulo, Tipo tipo) {

        public static Columna texto(String titulo) {
            return new Columna(titulo, Tipo.TEXTO);
        }

        public static Columna entero(String titulo) {
            return new Columna(titulo, Tipo.ENTERO);
        }

        public static Columna moneda(String titulo) {
            return new Columna(titulo, Tipo.MONEDA);
        }
    }
}
