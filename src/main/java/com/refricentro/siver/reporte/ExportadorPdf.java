package com.refricentro.siver.reporte;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.refricentro.siver.dto.TablaReporte;
import com.refricentro.siver.dto.TablaReporte.Columna;
import com.refricentro.siver.dto.TablaReporte.Tipo;
import com.refricentro.siver.model.DetalleVenta;
import com.refricentro.siver.model.Venta;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Dibuja los PDF con OpenPDF: los reportes (TablaReporte) y el comprobante
 * de una venta (boleta / factura / nota de venta).
 */
@Component
public class ExportadorPdf {

    private static final Color MARCA = new Color(0x3b, 0x34, 0x86);
    private static final Color GRIS = new Color(0xf1, 0xf5, 0xf9);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DecimalFormat MONTO =
            new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
    private static final DecimalFormat ENTERO =
            new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));

    private static final Font F_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, MARCA);
    private static final Font F_SUB = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
    private static final Font F_CAB = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
    private static final Font F_CELDA = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
    private static final Font F_TOTAL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);

    /** Reporte tabular. Si tiene muchas columnas, la hoja va horizontal. */
    public byte[] exportar(TablaReporte reporte) {
        List<Columna> columnas = reporte.columnas();
        Rectangle hoja = columnas.size() > 6 ? PageSize.A4.rotate() : PageSize.A4;
        Document doc = new Document(hoja, 28, 28, 28, 28);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(doc, salida);
            doc.addTitle(reporte.titulo());
            doc.addAuthor("SIVER");
            doc.open();

            doc.add(new Paragraph("SIVER · Refricentro Peregrino", F_SUB));
            doc.add(new Paragraph(reporte.titulo(), F_TITULO));
            doc.add(new Paragraph(reporte.filtros(), F_SUB));
            Paragraph generado = new Paragraph("Generado el " + reporte.generado().format(FECHA)
                    + " por " + reporte.generadoPor(), F_SUB);
            generado.setSpacingAfter(10);
            doc.add(generado);

            PdfPTable tabla = new PdfPTable(anchos(reporte));
            tabla.setWidthPercentage(100);
            tabla.setHeaderRows(1);   // la cabecera se repite en cada pagina
            for (Columna c : columnas) {
                PdfPCell celda = new PdfPCell(new Phrase(c.titulo(), F_CAB));
                celda.setBackgroundColor(MARCA);
                celda.setPadding(5);
                celda.setHorizontalAlignment(alineacion(c.tipo()));
                tabla.addCell(celda);
            }

            int n = 0;
            for (List<Object> fila : reporte.filas()) {
                Color fondo = (n++ % 2 == 0) ? Color.WHITE : GRIS;
                agregarFila(tabla, fila, columnas, F_CELDA, fondo);
            }
            if (reporte.filas().isEmpty()) {
                PdfPCell vacio = new PdfPCell(new Phrase("No hay registros con esos filtros", F_CELDA));
                vacio.setColspan(columnas.size());
                vacio.setPadding(8);
                vacio.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(vacio);
            }
            if (reporte.totales() != null) {
                agregarFila(tabla, reporte.totales(), columnas, F_TOTAL, GRIS);
            }
            doc.add(tabla);

            Paragraph pie = new Paragraph(reporte.filas().size() + " registro(s)", F_SUB);
            pie.setSpacingBefore(6);
            doc.add(pie);
        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el PDF", e);
        } finally {
            doc.close();
        }
        return salida.toByteArray();
    }

    /** Comprobante de una venta, con sus lineas y los totales con IGV. */
    public byte[] comprobante(Venta venta, List<DetalleVenta> detalles) {
        Document doc = new Document(PageSize.A5, 24, 24, 24, 24);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(doc, salida);
            doc.open();

            Paragraph empresa = new Paragraph("REFRICENTRO PEREGRINO", F_TITULO);
            empresa.setAlignment(Element.ALIGN_CENTER);
            doc.add(empresa);
            Paragraph rubro = new Paragraph("Venta de repuestos y servicio tecnico de refrigeracion", F_SUB);
            rubro.setAlignment(Element.ALIGN_CENTER);
            doc.add(rubro);

            String tipo = switch (venta.getTipoComprobante()) {
                case BOLETA -> "BOLETA DE VENTA";
                case FACTURA -> "FACTURA";
                case NOTA_VENTA -> "NOTA DE VENTA";
            };
            Paragraph numero = new Paragraph(tipo + "  N° " + venta.getNumeroComprobante(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK));
            numero.setAlignment(Element.ALIGN_CENTER);
            numero.setSpacingBefore(8);
            numero.setSpacingAfter(8);
            doc.add(numero);

            if (venta.getEstado() == Venta.EstadoVenta.ANULADA) {
                Paragraph anulada = new Paragraph("*** COMPROBANTE ANULADO ***",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.RED));
                anulada.setAlignment(Element.ALIGN_CENTER);
                doc.add(anulada);
            }

            var cliente = venta.getCliente();
            doc.add(new Paragraph("Fecha: " + venta.getFechaVenta().format(FECHA), F_CELDA));
            doc.add(new Paragraph("Cliente: " + cliente.getNombres(), F_CELDA));
            doc.add(new Paragraph(cliente.getTipoDocumento() + ": " + cliente.getNumeroDocumento(), F_CELDA));
            doc.add(new Paragraph("Metodo de pago: " + venta.getMetodoPago(), F_CELDA));
            Paragraph vendedor = new Paragraph("Atendido por: " + venta.getUsuario().getNombres()
                    + " " + venta.getUsuario().getApellidos(), F_CELDA);
            vendedor.setSpacingAfter(8);
            doc.add(vendedor);

            PdfPTable tabla = new PdfPTable(new float[] {1.2f, 4f, 1.6f, 1.4f, 1.8f});
            tabla.setWidthPercentage(100);
            for (String c : List.of("Cant.", "Descripcion", "P. unit.", "Dscto.", "Importe")) {
                PdfPCell celda = new PdfPCell(new Phrase(c, F_CAB));
                celda.setBackgroundColor(MARCA);
                celda.setPadding(4);
                tabla.addCell(celda);
            }
            for (DetalleVenta d : detalles) {
                tabla.addCell(celda(ENTERO.format(d.getCantidad()), Element.ALIGN_RIGHT, F_CELDA, Color.WHITE));
                tabla.addCell(celda(d.getProducto().getNombre(), Element.ALIGN_LEFT, F_CELDA, Color.WHITE));
                tabla.addCell(celda(MONTO.format(d.getPrecioUnitario()), Element.ALIGN_RIGHT, F_CELDA, Color.WHITE));
                tabla.addCell(celda(MONTO.format(d.getDescuento()), Element.ALIGN_RIGHT, F_CELDA, Color.WHITE));
                tabla.addCell(celda(MONTO.format(d.getSubtotal()), Element.ALIGN_RIGHT, F_CELDA, Color.WHITE));
            }
            doc.add(tabla);

            PdfPTable totales = new PdfPTable(new float[] {3f, 1.5f});
            totales.setWidthPercentage(55);
            totales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totales.setSpacingBefore(6);
            totales.addCell(celda("Op. gravada", Element.ALIGN_LEFT, F_CELDA, Color.WHITE));
            totales.addCell(celda("S/ " + MONTO.format(venta.getSubtotal()), Element.ALIGN_RIGHT, F_CELDA, Color.WHITE));
            totales.addCell(celda("IGV (18%)", Element.ALIGN_LEFT, F_CELDA, Color.WHITE));
            totales.addCell(celda("S/ " + MONTO.format(venta.getIgv()), Element.ALIGN_RIGHT, F_CELDA, Color.WHITE));
            totales.addCell(celda("TOTAL", Element.ALIGN_LEFT, F_TOTAL, GRIS));
            totales.addCell(celda("S/ " + MONTO.format(venta.getTotal()), Element.ALIGN_RIGHT, F_TOTAL, GRIS));
            doc.add(totales);

            if (venta.getObservacion() != null && !venta.getObservacion().isBlank()) {
                Paragraph obs = new Paragraph("Observacion: " + venta.getObservacion(), F_SUB);
                obs.setSpacingBefore(8);
                doc.add(obs);
            }
            Paragraph gracias = new Paragraph("Gracias por su preferencia", F_SUB);
            gracias.setAlignment(Element.ALIGN_CENTER);
            gracias.setSpacingBefore(14);
            doc.add(gracias);
        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el comprobante", e);
        } finally {
            doc.close();
        }
        return salida.toByteArray();
    }

    /**
     * Ancho relativo de cada columna segun lo que contiene (el texto mas
     * largo de las primeras filas), para que "Cliente" sea mas ancha que "IGV".
     */
    private static float[] anchos(TablaReporte reporte) {
        List<Columna> columnas = reporte.columnas();
        float[] anchos = new float[columnas.size()];
        for (int c = 0; c < columnas.size(); c++) {
            int largo = columnas.get(c).titulo().length();
            for (List<Object> fila : reporte.filas().subList(0, Math.min(60, reporte.filas().size()))) {
                if (c < fila.size()) {
                    largo = Math.max(largo, formatear(fila.get(c)).length());
                }
            }
            anchos[c] = Math.max(9, Math.min(largo + 2, 34));
        }
        return anchos;
    }

    private static void agregarFila(PdfPTable tabla, List<Object> fila, List<Columna> columnas,
                                    Font fuente, Color fondo) {
        for (int c = 0; c < columnas.size(); c++) {
            Object valor = c < fila.size() ? fila.get(c) : null;
            tabla.addCell(celda(formatear(valor), alineacion(columnas.get(c).tipo()), fuente, fondo));
        }
    }

    private static PdfPCell celda(String texto, int alineacion, Font fuente, Color fondo) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setHorizontalAlignment(alineacion);
        celda.setBackgroundColor(fondo);
        celda.setPadding(4);
        celda.setBorderColor(new Color(0xe2, 0xe8, 0xf0));
        return celda;
    }

    private static String formatear(Object valor) {
        if (valor == null) {
            return "";
        }
        if (valor instanceof BigDecimal n) {
            return "S/ " + MONTO.format(n);
        }
        if (valor instanceof Number n) {
            return ENTERO.format(n);
        }
        return valor.toString();
    }

    private static int alineacion(Tipo tipo) {
        return tipo == Tipo.TEXTO ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT;
    }
}
