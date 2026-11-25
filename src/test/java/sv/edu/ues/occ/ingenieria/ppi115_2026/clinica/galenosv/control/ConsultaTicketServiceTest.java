package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaTicketServiceTest {
    @Mock ConsultaDAO consultaDAO;
    @Mock ConsultaProcedimientoDAO procedimientoDAO;
    @InjectMocks ConsultaTicketService servicio;

    @Test
    void pdfMinimalistaConDatosEsencialesFechaLocalYQrEscaneable() throws Exception {
        var consulta = consulta();
        consulta.setReferenciaExterna("CITA-018");
        consulta.setObservaciones("OBSERVACIONES-PRIVADAS");
        when(procedimientoDAO.findByConsulta(consulta.getIdConsulta())).thenReturn(List.of(procedimiento("Evaluación médica")));
        String url = "https://pares.example/galeno/#consulta/" + consulta.getIdConsulta();
        byte[] generado = servicio.generar(consulta.getIdConsulta(), url);
        assertEquals("%PDF-", new String(generado, 0, 5, java.nio.charset.StandardCharsets.US_ASCII));
        guardarVistaPrevia("ticket-minimalista", generado);
        try (var pdf = Loader.loadPDF(generado)) {
            assertEquals(1, pdf.getNumberOfPages());
            String texto = new PDFTextStripper().getText(pdf);
            for (String esperado : List.of("José Arce", "Clínica de prueba", "17/11/2026 10:30", "Evaluación médica",
                    "CITA-018", "GalenoSV - Ticket de Cita")) assertTrue(texto.contains(esperado), esperado);
            assertTrue(texto.replaceAll("\\s+", " ").contains(
                    "Escanea este código QR para ver el detalle completo de tu consulta"));
            for (String excluido : List.of("OBSERVACIONES-PRIVADAS", "Fecha de fin", "Responsable", "Historial", "Documento")) {
                assertFalse(texto.contains(excluido), excluido);
            }
            assertEquals(url, TicketPdfAssertions.leerQr(pdf));
        }
    }

    @Test
    void datosAusentesProducenTicketDeUnaPaginaConReferenciaCorta() throws Exception {
        var consulta = new Consulta(UUID.randomUUID());
        when(consultaDAO.findById(consulta.getIdConsulta())).thenReturn(consulta);
        String url = "http://localhost:3000/#consulta/" + consulta.getIdConsulta();
        try (var pdf = Loader.loadPDF(servicio.generar(consulta.getIdConsulta(), url))) {
            String texto = new PDFTextStripper().getText(pdf);
            assertEquals(1, pdf.getNumberOfPages());
            assertTrue(texto.contains("Sin registrar"));
            assertTrue(texto.contains("Sin procedimientos registrados"));
            assertTrue(texto.contains(consulta.getIdConsulta().toString().substring(0, 8)));
            assertFalse(texto.contains(consulta.getIdConsulta().toString()));
            assertEquals(url, TicketPdfAssertions.leerQr(pdf));
        }
    }

    @Test
    void consultaInexistenteNoGeneraUnPdfVacio() {
        assertThrows(NotFoundException.class, () -> servicio.generar(UUID.randomUUID(), "http://localhost:3000"));
        verifyNoInteractions(procedimientoDAO);
    }

    @Test
    void muchosProcedimientosSeResumenSinDuplicadosNiHistorialInterno() throws Exception {
        var consulta = consulta();
        var primero = procedimiento("Evaluación");
        when(procedimientoDAO.findByConsulta(consulta.getIdConsulta())).thenReturn(List.of(primero, primero,
                procedimiento("Vacunación"), procedimiento("Control"), procedimiento("INTERNO-CUARTO")));
        try (var pdf = Loader.loadPDF(servicio.generar(consulta.getIdConsulta(), "http://localhost:3000/#consulta/" + consulta.getIdConsulta()))) {
            String texto = new PDFTextStripper().getText(pdf);
            assertEquals(1, pdf.getNumberOfPages());
            assertTrue(texto.contains("Evaluación; Vacunación; Control; y 1 más"));
            assertFalse(texto.contains("INTERNO-CUARTO"));
        }
    }

    @Test
    void camposLargosYUnicodeNoDesbordanLaPaginaNiInvadenElQr() throws Exception {
        var consulta = consulta();
        consulta.getIdPersonaRol().getIdPersona().setNombres("NombreExtremadamenteLargo".repeat(80) + " 🩺");
        consulta.getIdPersonaRol().getIdClinica().setNombre("Clínica de atención ".repeat(120));
        consulta.setReferenciaExterna("REFERENCIA-".repeat(1000));
        consulta.setObservaciones("OBSERVACIONES-".repeat(10000));
        when(procedimientoDAO.findByConsulta(consulta.getIdConsulta())).thenReturn(
                List.of(procedimiento("ProcedimientoSinEspacios".repeat(200))));
        String url = "https://pares.example/" + "ruta/".repeat(30) + "#consulta/" + consulta.getIdConsulta();
        byte[] generado = servicio.generar(consulta.getIdConsulta(), url);
        guardarVistaPrevia("ticket-campos-largos", generado);
        try (var pdf = Loader.loadPDF(generado)) {
            assertEquals(1, pdf.getNumberOfPages());
            assertFalse(new PDFTextStripper().getText(pdf).contains("OBSERVACIONES-"));
            assertEquals(url, TicketPdfAssertions.leerQr(pdf));
            var posiciones = new ArrayList<TextPosition>();
            new PDFTextStripper() {
                @Override
                protected void writeString(String texto, List<TextPosition> letras) throws java.io.IOException {
                    posiciones.addAll(letras);
                    super.writeString(texto, letras);
                }
            }.getText(pdf);
            float ancho = pdf.getPage(0).getMediaBox().getWidth();
            float alto = pdf.getPage(0).getMediaBox().getHeight();
            for (var posicion : posiciones) {
                assertTrue(posicion.getXDirAdj() >= 31);
                assertTrue(posicion.getXDirAdj() + posicion.getWidthDirAdj() <= ancho - 31);
                float base = alto - posicion.getYDirAdj();
                assertTrue(base > 204 || base < 65, "El texto invade la zona reservada al QR");
            }
        }
    }

    private Consulta consulta() {
        var consulta = new Consulta(UUID.randomUUID());
        var paciente = new PersonaRol(UUID.randomUUID());
        var persona = new Persona(UUID.randomUUID());
        persona.setNombres("José"); persona.setApellidos("Arce");
        var clinica = new Clinica(UUID.randomUUID()); clinica.setNombre("Clínica de prueba");
        paciente.setIdPersona(persona); paciente.setIdClinica(clinica);
        consulta.setIdPersonaRol(paciente);
        consulta.setFechaInicio(Date.from(java.time.Instant.parse("2026-11-17T16:30:00Z")));
        when(consultaDAO.findById(consulta.getIdConsulta())).thenReturn(consulta);
        return consulta;
    }

    private ConsultaProcedimiento procedimiento(String nombre) {
        var procedimiento = new ConsultaProcedimiento(UUID.randomUUID());
        var definicion = new Procedimiento(UUID.randomUUID()); definicion.setNombre(nombre);
        procedimiento.setIdProcedimiento(definicion);
        return procedimiento;
    }

    private static void guardarVistaPrevia(String nombre, byte[] pdf) throws java.io.IOException {
        String carpeta = System.getProperty("galenosv.pdf.previewDir");
        if (carpeta == null) return;
        var destino = java.nio.file.Path.of(carpeta);
        java.nio.file.Files.createDirectories(destino);
        java.nio.file.Files.write(destino.resolve(nombre + ".pdf"), pdf);
        try (var documento = Loader.loadPDF(pdf)) {
            javax.imageio.ImageIO.write(new PDFRenderer(documento).renderImageWithDPI(0, 144),
                    "png", destino.resolve(nombre + ".png").toFile());
        }
    }
}
