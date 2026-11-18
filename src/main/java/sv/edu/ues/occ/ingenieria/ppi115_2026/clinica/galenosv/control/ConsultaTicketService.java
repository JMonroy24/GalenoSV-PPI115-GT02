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

/** Genera el comprobante dentro de la transacción para poder leer las relaciones LAZY. */
@ApplicationScoped
public class ConsultaTicketService {
    @Inject protected ConsultaDAO consultaDAO;
    @Inject protected ConsultaProcedimientoDAO procedimientoDAO;
    @Inject protected ConsultaProcedimientoPasoDAO pasoDAO;
    @Inject protected DocumentoDAO documentoDAO;

    @Transactional
    public byte[] generar(UUID idConsulta) {
        var consulta = consultaDAO.findById(idConsulta);
        if (consulta == null) throw new NotFoundException("No existe la consulta solicitada.");
        try (var documento = new PDDocument(); var salida = new ByteArrayOutputStream()) {
            documento.getDocumentInformation().setTitle("GalenoSV - Ticket de Atención Clínica");
            documento.getDocumentInformation().setAuthor("GalenoSV");
            try (var ticket = new TicketPdf(documento, idConsulta)) {
                ticket.dato("Paciente", nombre(consulta.getIdPersonaRol()));
                var paciente = consulta.getIdPersonaRol();
                if (paciente != null && paciente.getIdPersona() != null) {
                    for (var identificacion : documentoDAO.findByPersona(paciente.getIdPersona().getIdPersona())) {
                        var tipo = identificacion.getIdTipoDocumento();
                        ticket.dato(tipo == null ? "Documento" : valor(tipo.getNombre()), valor(identificacion.getValor()));
                    }
                }
                ticket.dato("Clínica", paciente == null || paciente.getIdClinica() == null
                        ? "Sin registrar" : valor(paciente.getIdClinica().getNombre()));
                ticket.dato("Fecha de inicio", fecha(consulta.getFechaInicio()));
                ticket.dato("Fecha de fin", fecha(consulta.getFechaFin()));
                ticket.dato("Referencia externa", valor(consulta.getReferenciaExterna()));
                ticket.dato("Observaciones", valor(consulta.getObservaciones()));
                ticket.iniciarHistorial();
                var procedimientos = procedimientoDAO.findByConsulta(idConsulta);
                if (procedimientos.isEmpty()) ticket.nota("Sin procedimientos registrados.");
                for (var procedimiento : procedimientos) {
                    String titulo = procedimiento.getIdProcedimiento() == null
                            ? "Sin registrar" : valor(procedimiento.getIdProcedimiento().getNombre());
                    var pasos = pasoDAO.findByConsultaProcedimiento(procedimiento.getIdConsultaProcedimiento());
                    if (pasos.isEmpty()) ticket.procedimiento(titulo, "Sin asignar", "Sin pasos registrados.",
                            periodo(procedimiento.getFechaInicio(), procedimiento.getFechaFin()));
                    int numeroPaso = 1;
                    for (var paso : pasos) {
                        ticket.procedimiento(titulo + "\nPaso " + numeroPaso++, nombre(paso.getIdPersonaRol()),
                                valor(paso.getEstado()), periodo(paso.getFechaInicio(), paso.getFechaFin()));
                    }
                    ticket.nota("Período del procedimiento: " + fecha(procedimiento.getFechaInicio())
                            + " a " + fecha(procedimiento.getFechaFin())
                            + "\nObservaciones: " + valor(procedimiento.getObservaciones()));
                }
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

    private static String periodo(Date inicio, Date fin) {
        return "Inicio:\n" + fecha(inicio) + "\nFin:\n" + fecha(fin);
    }
}
