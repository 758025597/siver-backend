package com.refricentro.siver.reporte;

import com.refricentro.siver.dto.TablaReporte;
import com.refricentro.siver.dto.TablaReporte.Columna;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 * Escribe un TablaReporte como libro de Excel (.xlsx) con Apache POI.
 *
 * Los montos se guardan como NUMEROS con formato "S/ #,##0.00", no como
 * texto: asi en Excel se pueden sumar, filtrar y graficar.
 */
@Component
public class ExportadorExcel {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] exportar(TablaReporte reporte) {
        try (Workbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Sheet hoja = libro.createSheet("Reporte");
            List<Columna> columnas = reporte.columnas();
            int ultimaColumna = Math.max(columnas.size() - 1, 0);

            CellStyle titulo = estiloTitulo(libro);
            CellStyle cabecera = estiloCabecera(libro);
            CellStyle moneda = estiloNumero(libro, "\"S/\" #,##0.00", false);
            CellStyle entero = estiloNumero(libro, "#,##0", false);
            CellStyle monedaTotal = estiloNumero(libro, "\"S/\" #,##0.00", true);
            CellStyle enteroTotal = estiloNumero(libro, "#,##0", true);
            CellStyle textoTotal = estiloNumero(libro, "@", true);

            int fila = 0;
            Row r = hoja.createRow(fila++);
            r.createCell(0).setCellValue("SIVER · Refricentro Peregrino — " + reporte.titulo());
            r.getCell(0).setCellStyle(titulo);
            hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, ultimaColumna));

            hoja.createRow(fila++).createCell(0).setCellValue(reporte.filtros());
            hoja.createRow(fila++).createCell(0).setCellValue("Generado el " + reporte.generado().format(FECHA)
                    + " por " + reporte.generadoPor());
            fila++;

            Row filaCabecera = hoja.createRow(fila++);
            for (int c = 0; c < columnas.size(); c++) {
                Cell celda = filaCabecera.createCell(c);
                celda.setCellValue(columnas.get(c).titulo());
                celda.setCellStyle(cabecera);
            }
            int primeraFilaDatos = fila;

            for (List<Object> datos : reporte.filas()) {
                Row row = hoja.createRow(fila++);
                escribirFila(row, datos, columnas, moneda, entero, null);
            }

            if (reporte.totales() != null) {
                Row row = hoja.createRow(fila);
                escribirFila(row, reporte.totales(), columnas, monedaTotal, enteroTotal, textoTotal);
            }

            hoja.createFreezePane(0, primeraFilaDatos);
            for (int c = 0; c < columnas.size(); c++) {
                hoja.autoSizeColumn(c);
                hoja.setColumnWidth(c, Math.min(hoja.getColumnWidth(c) + 512, 60 * 256));
            }

            libro.write(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el Excel", e);
        }
    }

    private static void escribirFila(Row row, List<Object> datos, List<Columna> columnas,
                                     CellStyle moneda, CellStyle entero, CellStyle texto) {
        for (int c = 0; c < datos.size() && c < columnas.size(); c++) {
            Object valor = datos.get(c);
            Cell celda = row.createCell(c);
            if (valor instanceof BigDecimal n) {
                celda.setCellValue(n.doubleValue());
                celda.setCellStyle(moneda);
            } else if (valor instanceof Number n) {
                celda.setCellValue(n.doubleValue());
                celda.setCellStyle(entero);
            } else {
                celda.setCellValue(valor == null ? "" : valor.toString());
                if (texto != null) {
                    celda.setCellStyle(texto);
                }
            }
        }
    }

    private static CellStyle estiloTitulo(Workbook libro) {
        Font fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setFontHeightInPoints((short) 14);
        CellStyle estilo = libro.createCellStyle();
        estilo.setFont(fuente);
        return estilo;
    }

    private static CellStyle estiloCabecera(Workbook libro) {
        Font fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setColor(IndexedColors.WHITE.getIndex());
        CellStyle estilo = libro.createCellStyle();
        estilo.setFont(fuente);
        estilo.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setBorderBottom(BorderStyle.THIN);
        return estilo;
    }

    private static CellStyle estiloNumero(Workbook libro, String formato, boolean negrita) {
        CellStyle estilo = libro.createCellStyle();
        estilo.setDataFormat(libro.createDataFormat().getFormat(formato));
        if (negrita) {
            Font fuente = libro.createFont();
            fuente.setBold(true);
            estilo.setFont(fuente);
            estilo.setBorderTop(BorderStyle.THIN);
        }
        return estilo;
    }
}
