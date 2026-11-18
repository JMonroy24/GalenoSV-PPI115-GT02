package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.ws.rs.NotFoundException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import org.apache.pdfbox.Loader;
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
    @Mock ConsultaProcedimientoPasoDAO pasoDAO;
    @Mock DocumentoDAO documentoDAO;
    @InjectMocks ConsultaTicketService servicio;

    @Test
    void pdfValidoConPacienteDocumentosClinicaProcedimientosYFechasLocales() throws Exception {
        var consulta = new Consulta(UUID.randomUUID());
        var paciente = new PersonaRol(UUID.randomUUID());
        var persona = new Persona(UUID.randomUUID());
        persona.setNombres("José"); persona.setApellidos("Arce");
        paciente.setIdPersona(persona);
        var clinica = new Clinica(UUID.randomUUID()); clinica.setNombre("Clínica de prueba");
        paciente.setIdClinica(clinica); consulta.setIdPersonaRol(paciente);
        consulta.setFechaInicio(Date.from(java.time.Instant.parse("2026-11-17T16:30:00Z")));
        var documento = new Documento(UUID.randomUUID()); documento.setValor("DOC-001");
        var tipoDocumento = new TipoDocumento(UUID.randomUUID()); tipoDocumento.setNombre("Pasaporte");
        documento.setIdTipoDocumento(tipoDocumento);
        var procedimiento = new ConsultaProcedimiento(UUID.randomUUID());
        var definicion = new Procedimiento(UUID.randomUUID()); definicion.setNombre("Evaluación médica");
        procedimiento.setIdProcedimiento(definicion);
        var paso = new ConsultaProcedimientoPaso(UUID.randomUUID()); paso.setEstado("PENDIENTE");
        paso.setIdPersonaRol(paciente);
        when(consultaDAO.findById(consulta.getIdConsulta())).thenReturn(consulta);
        when(documentoDAO.findByPersona(persona.getIdPersona())).thenReturn(List.of(documento));
        when(procedimientoDAO.findByConsulta(consulta.getIdConsulta())).thenReturn(List.of(procedimiento));
        when(pasoDAO.findByConsultaProcedimiento(procedimiento.getIdConsultaProcedimiento())).thenReturn(List.of(paso));

        byte[] generado = servicio.generar(consulta.getIdConsulta());
        guardarVistaPrevia("ticket-diseno", generado);
        try (var pdf = Loader.loadPDF(generado)) {
            String texto = new PDFTextStripper().getText(pdf);
            assertEquals(1, pdf.getNumberOfPages());
            for (String esperado : List.of("José Arce", "DOC-001", "Clínica de prueba", "17/11/2026 10:30",
                    "Fecha de fin:", "Sin registrar", "Evaluación médica", "PENDIENTE", "Pasaporte:",
                    "GalenoSV - Ticket de Atención Clínica", "Procedimiento", "Responsable", "Estado", "Fechas")) {
                assertTrue(texto.contains(esperado), esperado);
            }
            var titulos = new ArrayList<List<TextPosition>>();
            var extractor = new PDFTextStripper() {
                @Override
                protected void writeString(String texto, List<TextPosition> posiciones) throws java.io.IOException {
                    if (texto.equals("GalenoSV - Ticket de Atención Clínica")) titulos.add(List.copyOf(posiciones));
                    super.writeString(texto, posiciones);
                }
            };
            extractor.getText(pdf);
            assertEquals(1, titulos.size());
            var titulo = titulos.getFirst();
            assertTrue(titulo.stream().allMatch(p -> p.getFont().getName().contains("Bold") && p.getFontSizeInPt() >= 18));
            float izquierda = titulo.getFirst().getXDirAdj();
            var ultima = titulo.getLast();
            float derecha = ultima.getXDirAdj() + ultima.getWidthDirAdj();
            assertEquals(pdf.getPage(0).getMediaBox().getWidth() / 2, (izquierda + derecha) / 2, 1);
        }
    }

    @Test
    void textoLargoSeReparteEnPaginasYConservaElFinal() throws Exception {
        var consulta = new Consulta(UUID.randomUUID());
        consulta.setObservaciones("Detalle de atención. ".repeat(1500) + "FIN-OBSERVACIONES");
        when(consultaDAO.findById(consulta.getIdConsulta())).thenReturn(consulta);
        try (var pdf = Loader.loadPDF(servicio.generar(consulta.getIdConsulta()))) {
            String texto = new PDFTextStripper().getText(pdf);
            assertTrue(pdf.getNumberOfPages() > 1);
            assertTrue(texto.replaceAll("\\s", "").contains("FIN-OBSERVACIONES"));
            assertTrue(texto.contains("Sin procedimientos registrados."));
        }
    }

    @Test
    void consultaInexistenteNoGeneraUnPdfVacio() {
        assertThrows(NotFoundException.class, () -> servicio.generar(UUID.randomUUID()));
        verifyNoInteractions(procedimientoDAO, pasoDAO, documentoDAO);
    }

    @Test
    void historialExtensoRepiteEncabezadosYConservaFilasLargasSinSalirseDeLosMargenes() throws Exception {
        var consulta = new Consulta(UUID.randomUUID());
        var procedimiento = new ConsultaProcedimiento(UUID.randomUUID());
        var definicion = new Procedimiento(UUID.randomUUID());
        definicion.setNombre("Evaluación clínica y seguimiento integral del paciente");
        procedimiento.setIdProcedimiento(definicion);
        procedimiento.setObservaciones("OBSERVACION-LARGA-SIN-ESPACIOS-".repeat(130) + "FIN-NOTA");
        var responsable = new PersonaRol(UUID.randomUUID());
        var persona = new Persona(UUID.randomUUID());
        persona.setNombres("María de los Ángeles"); persona.setApellidos("Hernández de López");
        responsable.setIdPersona(persona);
        var pasos = new ArrayList<ConsultaProcedimientoPaso>();
        for (int i = 0; i < 25; i++) {
            var paso = new ConsultaProcedimientoPaso(UUID.randomUUID());
            paso.setIdPersonaRol(responsable);
            paso.setEstado(i == 24 ? "FIN-HISTORIAL" : "COMPLETADO");
            paso.setFechaInicio(new Date(1794933000000L));
            pasos.add(paso);
        }
        when(consultaDAO.findById(consulta.getIdConsulta())).thenReturn(consulta);
        when(procedimientoDAO.findByConsulta(consulta.getIdConsulta())).thenReturn(List.of(procedimiento));
        when(pasoDAO.findByConsultaProcedimiento(procedimiento.getIdConsultaProcedimiento())).thenReturn(pasos);
        byte[] generado = servicio.generar(consulta.getIdConsulta());
        guardarVistaPrevia("ticket-historial-extenso", generado);
        try (var pdf = Loader.loadPDF(generado)) {
            assertTrue(pdf.getNumberOfPages() > 2);
            var extractor = new PDFTextStripper();
            String completo = extractor.getText(pdf).replaceAll("\\s", "");
            assertTrue(completo.contains("FIN-HISTORIAL"));
            assertTrue(completo.contains("FIN-NOTA"));
            for (int pagina = 1; pagina <= pdf.getNumberOfPages(); pagina++) {
                extractor.setStartPage(pagina); extractor.setEndPage(pagina);
                String texto = extractor.getText(pdf);
                for (String encabezado : List.of("Procedimiento", "Responsable", "Estado", "Fechas")) {
                    assertTrue(texto.contains(encabezado), "Página " + pagina + ": " + encabezado);
                }
            }
            new PDFTextStripper() {
                @Override
                protected void writeString(String texto, List<TextPosition> posiciones) throws java.io.IOException {
                    for (var posicion : posiciones) {
                        assertTrue(posicion.getXDirAdj() >= 47);
                        assertTrue(posicion.getXDirAdj() + posicion.getWidthDirAdj() <= 548);
                        assertTrue(posicion.getYDirAdj() < 823);
                    }
                    super.writeString(texto, posiciones);
                }
            }.getText(pdf);
        }
    }

    private static void guardarVistaPrevia(String nombre, byte[] pdf) throws java.io.IOException {
        String carpeta = System.getProperty("galenosv.pdf.previewDir");
        if (carpeta == null) return;
        var destino = java.nio.file.Path.of(carpeta);
        java.nio.file.Files.createDirectories(destino);
        java.nio.file.Files.write(destino.resolve(nombre + ".pdf"), pdf);
    }
}
