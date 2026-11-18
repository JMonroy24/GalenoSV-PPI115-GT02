package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/** Maquetación del recibo: celdas con ajuste de texto, filas divisibles y encabezados repetidos. */
final class TicketPdf implements AutoCloseable {
    private static final float MARGEN = 48;
    private static final float LIMITE_INFERIOR = 58;
    private static final float ANCHO = PDRectangle.A4.getWidth() - 2 * MARGEN;
    private static final float RELLENO = 9;
    private static final float INTERLINEADO = 14;
    private static final Color AZUL = new Color(24, 54, 91);
    private static final Color ACENTO = new Color(22, 113, 139);
    private static final Color TEXTO = new Color(43, 54, 68);
    private static final Color GRIS = new Color(101, 115, 132);
    private static final Color BORDE = new Color(213, 222, 231);
    private static final Color CABECERA = new Color(232, 240, 247);
    private static final Color ALTERNO = new Color(248, 250, 252);
    private static final float[] COLUMNAS = {ANCHO * .28f, ANCHO * .25f, ANCHO * .18f, ANCHO * .29f};
    private final PDFont normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDFont negrita = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final PDDocument documento;
    private final UUID idConsulta;
    private PDPageContentStream contenido;
    private float y;
    private boolean historial;
    private int filas;

    TicketPdf(PDDocument documento, UUID idConsulta) throws IOException {
        this.documento = documento;
        this.idConsulta = idConsulta;
        nuevaPagina();
    }

    void dato(String etiqueta, String valor) throws IOException {
        fila(new String[]{etiqueta + ":", valor}, new float[]{124, ANCHO - 124},
                new PDFont[]{negrita, normal}, 10.5f, null, false);
    }

    void iniciarHistorial() throws IOException {
        if (y - LIMITE_INFERIOR < 140) nuevaPagina();
        historial = true;
        cabeceraHistorial(false);
    }

    void procedimiento(String nombre, String responsable, String estado, String fechas) throws IOException {
        fila(new String[]{nombre, responsable, estado, fechas}, COLUMNAS,
                new PDFont[]{normal, normal, negrita, normal}, 9.5f,
                filas++ % 2 == 0 ? Color.WHITE : ALTERNO, true);
    }

    void nota(String texto) throws IOException {
        fila(new String[]{texto}, new float[]{ANCHO}, new PDFont[]{normal}, 9,
                ALTERNO, true);
    }

    private void nuevaPagina() throws IOException {
        if (contenido != null) contenido.close();
        var pagina = new PDPage(PDRectangle.A4);
        documento.addPage(pagina);
        contenido = new PDPageContentStream(documento, pagina);
        y = PDRectangle.A4.getHeight() - MARGEN;
        contenido.setNonStrokingColor(ACENTO);
        contenido.addRect(MARGEN, y - 3, ANCHO, 3);
        contenido.fill();
        String titulo = "GalenoSV - Ticket de Atención Clínica";
        float anchoTitulo = negrita.getStringWidth(titulo) / 1000 * 18;
        texto(titulo, (PDRectangle.A4.getWidth() - anchoTitulo) / 2, y - 35, negrita, 18, AZUL);
        String referencia = "Consulta: " + idConsulta;
        float anchoReferencia = normal.getStringWidth(referencia) / 1000 * 8.5f;
        texto(referencia, (PDRectangle.A4.getWidth() - anchoReferencia) / 2, y - 55, normal, 8.5f, GRIS);
        y -= 87;
        if (historial) cabeceraHistorial(true);
        else {
            texto("DATOS DE LA CONSULTA", MARGEN, y, negrita, 10, ACENTO);
            y -= 15;
        }
    }

    private void cabeceraHistorial(boolean continuacion) throws IOException {
        y -= 16;
        contenido.setStrokingColor(BORDE);
        contenido.setLineWidth(.7f);
        contenido.moveTo(MARGEN, y);
        contenido.lineTo(MARGEN + ANCHO, y);
        contenido.stroke();
        y -= 24;
        texto("Procedimientos e historial de pasos" + (continuacion ? " (continuación)" : ""),
                MARGEN, y, negrita, 12, AZUL);
        y -= 13;
        segmento(List.of(List.of("Procedimiento"), List.of("Responsable"), List.of("Estado"), List.of("Fechas")),
                COLUMNAS, new PDFont[]{negrita, negrita, negrita, negrita}, 9.5f, CABECERA, true, 0, 1);
    }

    private void fila(String[] celdas, float[] anchos, PDFont[] fuentes, float tamano,
                      Color fondo, boolean bordes) throws IOException {
        List<List<String>> lineas = new ArrayList<>();
        for (int i = 0; i < celdas.length; i++) {
            lineas.add(ajustar(celdas[i], fuentes[i], tamano, anchos[i] - 2 * RELLENO));
        }
        int total = lineas.stream().mapToInt(List::size).max().orElse(1);
        float altoTotal = total * INTERLINEADO + 2 * RELLENO;
        // Mantener juntas las filas habituales; las excepcionalmente largas se dividen sin perder texto.
        if (altoTotal <= 450 && y - altoTotal < LIMITE_INFERIOR) nuevaPagina();
        int desde = 0;
        while (desde < total) {
            int disponibles = (int) ((y - LIMITE_INFERIOR - 2 * RELLENO) / INTERLINEADO);
            if (disponibles < 1) {
                nuevaPagina();
                continue;
            }
            int cantidad = Math.min(total - desde, disponibles);
            segmento(lineas, anchos, fuentes, tamano, fondo, bordes, desde, cantidad);
            desde += cantidad;
            if (desde < total) nuevaPagina();
        }
    }

    private void segmento(List<List<String>> celdas, float[] anchos, PDFont[] fuentes, float tamano,
                          Color fondo, boolean bordes, int desde, int cantidad) throws IOException {
        float alto = cantidad * INTERLINEADO + 2 * RELLENO;
        float x = MARGEN;
        for (int columna = 0; columna < celdas.size(); columna++) {
            if (fondo != null) {
                contenido.setNonStrokingColor(fondo);
                contenido.addRect(x, y - alto, anchos[columna], alto);
                contenido.fill();
            }
            if (bordes) {
                contenido.setStrokingColor(BORDE);
                contenido.setLineWidth(.5f);
                contenido.addRect(x, y - alto, anchos[columna], alto);
                contenido.stroke();
            }
            List<String> lineas = celdas.get(columna);
            for (int linea = desde; linea < Math.min(lineas.size(), desde + cantidad); linea++) {
                texto(lineas.get(linea), x + RELLENO, y - RELLENO - tamano - (linea - desde) * INTERLINEADO,
                        fuentes[columna], tamano, TEXTO);
            }
            x += anchos[columna];
        }
        y -= alto;
    }

    private List<String> ajustar(String original, PDFont fuente, float tamano, float ancho) throws IOException {
        var seguro = new StringBuilder();
        for (int punto : original.codePoints().toArray()) {
            if (punto == '\n') { seguro.append('\n'); continue; }
            if (Character.isISOControl(punto)) { seguro.append(' '); continue; }
            String letra = new String(Character.toChars(punto));
            try {
                fuente.getStringWidth(letra);
                seguro.append(letra);
            } catch (IllegalArgumentException e) {
                seguro.append('?');
            }
        }
        List<String> resultado = new ArrayList<>();
        for (String parrafo : seguro.toString().split("\n", -1)) {
            String linea = "";
            for (String palabra : parrafo.strip().split("\\s+")) {
                if (palabra.isEmpty()) continue;
                String candidata = linea.isEmpty() ? palabra : linea + " " + palabra;
                if (ancho(candidata, fuente, tamano) <= ancho) { linea = candidata; continue; }
                if (!linea.isEmpty()) { resultado.add(linea); linea = ""; }
                for (int punto : palabra.codePoints().toArray()) {
                    String letra = new String(Character.toChars(punto));
                    if (!linea.isEmpty() && ancho(linea + letra, fuente, tamano) > ancho) {
                        resultado.add(linea);
                        linea = "";
                    }
                    linea += letra;
                }
            }
            resultado.add(linea);
        }
        return resultado;
    }

    private float ancho(String texto, PDFont fuente, float tamano) throws IOException {
        return fuente.getStringWidth(texto) / 1000 * tamano;
    }

    private void texto(String texto, float x, float base, PDFont fuente, float tamano, Color color) throws IOException {
        contenido.beginText();
        contenido.setFont(fuente, tamano);
        contenido.setNonStrokingColor(color);
        contenido.newLineAtOffset(x, base);
        contenido.showText(texto);
        contenido.endText();
    }

    @Override
    public void close() throws IOException {
        contenido.close();
        int paginas = documento.getNumberOfPages();
        for (int numero = 0; numero < paginas; numero++) {
            try (var pie = new PDPageContentStream(documento, documento.getPage(numero),
                    PDPageContentStream.AppendMode.APPEND, true, true)) {
                contenido = pie;
                pie.setStrokingColor(BORDE);
                pie.setLineWidth(.5f);
                pie.moveTo(MARGEN, 40);
                pie.lineTo(MARGEN + ANCHO, 40);
                pie.stroke();
                texto("GalenoSV | Atención clínica", MARGEN, 26, normal, 8, GRIS);
                String pagina = "Página " + (numero + 1) + " de " + paginas;
                texto(pagina, MARGEN + ANCHO - ancho(pagina, normal, 8), 26, normal, 8, GRIS);
            }
        }
    }
}
