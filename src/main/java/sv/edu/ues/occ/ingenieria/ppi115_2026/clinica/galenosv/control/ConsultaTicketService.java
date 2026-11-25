package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import org.apache.pdfbox.pdmodel.PDDocument;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

/** Genera el comprobante dentro de la transacción para leer las relaciones LAZY. */
@ApplicationScoped
public class ConsultaTicketService {
    @Inject protected ConsultaDAO consultaDAO;
    @Inject protected ConsultaProcedimientoDAO procedimientoDAO;

    @Transactional
    public byte[] generar(UUID idConsulta, String detalleUrl) {
        var consulta = consultaDAO.findById(idConsulta);
        if (consulta == null) throw new NotFoundException("No existe la consulta solicitada.");
        var paciente = consulta.getIdPersonaRol();
        String clinica = paciente == null || paciente.getIdClinica() == null
                ? "Clínica sin registrar" : valor(paciente.getIdClinica().getNombre());
        var procedimientos = procedimientoDAO.findByConsulta(idConsulta).stream()
                .map(p -> p.getIdProcedimiento() == null ? "Sin registrar" : valor(p.getIdProcedimiento().getNombre()))
                .distinct().toList();
        // Resumen acotado: los procedimientos adicionales se consultan mediante el QR.
        String resumen = procedimientos.isEmpty() ? "Sin procedimientos registrados"
                : String.join("; ", procedimientos.stream().limit(3).toList())
                        + (procedimientos.size() > 3 ? "; y " + (procedimientos.size() - 3) + " más" : "");
        String referencia = consulta.getReferenciaExterna();
        if (referencia == null || referencia.isBlank()) referencia = idConsulta.toString().substring(0, 8);
        try (var documento = new PDDocument(); var salida = new ByteArrayOutputStream()) {
            documento.getDocumentInformation().setTitle("GalenoSV - Ticket de Cita");
            documento.getDocumentInformation().setAuthor("GalenoSV");
            try (var ticket = new TicketPdf(documento, clinica)) {
                ticket.dato("Paciente", nombre(paciente), 2);
                ticket.dato("Fecha y hora", fecha(consulta.getFechaInicio()), 1);
                ticket.dato("Procedimientos", resumen, 4);
                ticket.dato("Referencia", referencia, 1);
                ticket.qr(detalleUrl);
            }
            documento.save(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el ticket PDF.", e);
        }
    }

    private static String valor(String texto) {
        return texto == null || texto.isBlank() ? "Sin registrar" : texto;
    }

    private static String nombre(PersonaRol asignacion) {
        if (asignacion == null || asignacion.getIdPersona() == null) return "Sin registrar";
        var persona = asignacion.getIdPersona();
        return valor(((persona.getNombres() == null ? "" : persona.getNombres()) + " "
                + (persona.getApellidos() == null ? "" : persona.getApellidos())).trim());
    }

    private static String fecha(Date fecha) {
        if (fecha == null) return "Sin registrar";
        var formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        formato.setTimeZone(TimeZone.getTimeZone("America/El_Salvador"));
        return formato.format(fecha);
    }
}
