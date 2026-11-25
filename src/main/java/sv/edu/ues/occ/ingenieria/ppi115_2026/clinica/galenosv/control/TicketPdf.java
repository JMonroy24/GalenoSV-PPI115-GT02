package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/** Ticket A5 de una página. El detalle completo queda en el destino del QR. */
final class TicketPdf implements AutoCloseable {
    private static final float MARGEN = 32;
    private static final float ANCHO = PDRectangle.A5.getWidth() - 2 * MARGEN;
    private final PDFont normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDFont negrita = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final PDPageContentStream contenido;
    private float y = PDRectangle.A5.getHeight() - 38;

    TicketPdf(PDDocument documento, String clinica) throws IOException {
        var pagina = new PDPage(PDRectangle.A5);
        documento.addPage(pagina);
        contenido = new PDPageContentStream(documento, pagina);
        bloque(clinica, negrita, 14, 18, 2);
        y -= 8;
        bloque("GalenoSV - Ticket de Cita", normal, 13, 18, 1);
        y -= 6;
        contenido.setStrokingColor(Color.LIGHT_GRAY);
        contenido.moveTo(MARGEN, y);
        contenido.lineTo(MARGEN + ANCHO, y);
        contenido.stroke();
        y -= 24;
    }

    void dato(String etiqueta, String valor, int maxLineas) throws IOException {
        texto(etiqueta, MARGEN, y, negrita, 9, Color.DARK_GRAY);
        y -= 16;
        bloque(valor, normal, 11, 15, maxLineas);
        y -= 14;
    }

    void qr(String url) throws IOException {
        try {
            var matriz = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, 0, 0,
                    Map.of(EncodeHintType.MARGIN, 4,
                            EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                            EncodeHintType.CHARACTER_SET, "UTF-8"));
            float lado = 124;
            float modulo = lado / matriz.getWidth();
            float x = (PDRectangle.A5.getWidth() - lado) / 2;
            contenido.setNonStrokingColor(Color.BLACK);
            // Módulos vectoriales y zona blanca de cuatro módulos: sin interpolación de imágenes.
            for (int fila = 0; fila < matriz.getHeight(); fila++) {
                for (int columna = 0; columna < matriz.getWidth(); columna++) {
                    if (matriz.get(columna, fila)) {
                        contenido.addRect(x + columna * modulo, 80 + (matriz.getHeight() - fila - 1) * modulo,
                                modulo, modulo);
                    }
                }
            }
            contenido.fill();
            y = 58;
            for (String linea : ajustar("Escanea este código QR para ver el detalle completo de tu consulta", normal, 9)) {
                float ancho = normal.getStringWidth(linea) / 1000 * 9;
                texto(linea, (PDRectangle.A5.getWidth() - ancho) / 2, y, normal, 9, Color.DARK_GRAY);
                y -= 13;
            }
        } catch (WriterException e) {
            throw new IOException("No se pudo generar el código QR.", e);
        }
    }

    private void bloque(String valor, PDFont fuente, float tamano, float interlineado, int maxLineas) throws IOException {
        List<String> lineas = ajustar(valor, fuente, tamano);
        int cantidad = Math.min(maxLineas, lineas.size());
        for (int i = 0; i < cantidad; i++) {
            String linea = lineas.get(i);
            if (i == cantidad - 1 && lineas.size() > cantidad) {
                while (fuente.getStringWidth(linea + "...") / 1000 * tamano > ANCHO) {
                    linea = linea.substring(0, linea.length() - 1);
                }
                linea += "...";
            }
            texto(linea, MARGEN, y, fuente, tamano, Color.BLACK);
            y -= interlineado;
        }
    }

    private List<String> ajustar(String original, PDFont fuente, float tamano) throws IOException {
        var seguro = new StringBuilder();
        for (int punto : original.codePoints().limit(2000).toArray()) {
            String letra = Character.isWhitespace(punto) || Character.isISOControl(punto)
                    ? " " : new String(Character.toChars(punto));
            try {
                fuente.getStringWidth(letra);
                seguro.append(letra);
            } catch (IllegalArgumentException e) {
                seguro.append('?');
            }
        }
        var resultado = new ArrayList<String>();
        String linea = "";
        for (String palabra : seguro.toString().strip().split("\\s+")) {
            String candidata = linea.isEmpty() ? palabra : linea + " " + palabra;
            if (fuente.getStringWidth(candidata) / 1000 * tamano <= ANCHO) {
                linea = candidata;
                continue;
            }
            if (!linea.isEmpty()) resultado.add(linea);
            linea = "";
            for (int punto : palabra.codePoints().toArray()) {
                String letra = new String(Character.toChars(punto));
                if (fuente.getStringWidth(linea + letra) / 1000 * tamano > ANCHO) {
                    resultado.add(linea);
                    linea = "";
                }
                linea += letra;
            }
        }
        resultado.add(linea);
        return resultado;
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
    }
}
