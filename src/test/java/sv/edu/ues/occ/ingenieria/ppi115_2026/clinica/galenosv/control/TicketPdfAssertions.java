package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

/** Decodifica el QR desde la página renderizada, no desde los datos de entrada. */
public final class TicketPdfAssertions {
    private TicketPdfAssertions() { }

    public static String leerQr(PDDocument pdf) throws Exception {
        var imagen = new PDFRenderer(pdf).renderImageWithDPI(0, 200);
        int ancho = imagen.getWidth(), alto = imagen.getHeight();
        int[] pixeles = imagen.getRGB(0, 0, ancho, alto, null, 0, ancho);
        var fuente = new RGBLuminanceSource(ancho, alto, pixeles);
        return new QRCodeReader().decode(new BinaryBitmap(new HybridBinarizer(fuente))).getText();
    }
}
