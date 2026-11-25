package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDetalleDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaDetalleMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaTicketService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaPublicaUrl;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;

@Tag(name = "Consultas", description = "Consultas médicas.")
@jakarta.ws.rs.Path("/consultas")
@RequestScoped
public class ConsultaResource extends GenericResource<Consulta, ConsultaDTO, UUID> {

    @Inject
    ConsultaService consultaService;

    @Inject
    ConsultaMapper consultaMapper;

    @Inject
    ConsultaTicketService consultaTicketService;

    @Inject
    ConsultaDetalleMapper consultaDetalleMapper;

    @QueryParam("pacienteId")
    String pacienteId;

    @QueryParam("idClinica") String idClinica;
    @QueryParam("idRol") String idRol;
    @QueryParam("desde") String desde;
    @QueryParam("hasta") String hasta;


    @GET
    @Path("/{id}/ticket")
    @Produces("application/pdf")
    @PermitAll
    @Operation(summary = "Visualiza el ticket PDF de cita con QR al detalle completo")
    public Response descargarTicket(@PathParam("id") String id,
            @QueryParam("frontendUrl")
            @Parameter(description = "Base HTTP(S) del frontend; tiene prioridad sobre Origin y GALENOSV_FRONTEND_URL",
                    schema = @Schema(type = SchemaType.STRING, format = "uri")) String frontendUrl,
            @HeaderParam("Origin") String origin) {
        final UUID clave;
        try {
            clave = UUID.fromString(id);
            if (!clave.toString().equalsIgnoreCase(id)) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("El identificador de consulta debe ser un UUID válido.", e);
        }
        String detalleUrl = ConsultaPublicaUrl.resolver(clave, frontendUrl, origin);
        return Response.ok(consultaTicketService.generar(clave, detalleUrl), "application/pdf")
                .header("Content-Disposition", "inline; filename=\"ticket-consulta.pdf\"")
                .header("Cache-Control", "no-store")
                .build();
    }

    @Override
    @GET
    @Path("/{id}")
    @PermitAll
    @Operation(summary = "Obtiene una consulta con sus pasos ejecutados")
    public Response obtener(@PathParam("id") String id) {
        Consulta entidad = consultaService.findById(parseId(id));
        if (entidad == null) {
            throw new NotFoundException("No existe el recurso solicitado.");
        }
        ConsultaDetalleDTO dto = consultaDetalleMapper.toDTO(entidad);
        return Response.ok(dto)
                .header("ETag", ETagUtils.calcular(dto))
                .build();
    }

    @Override
    @GET
    @PermitAll
    @Operation(summary = "Lista consultas filtradas antes de paginar",
            description = "Todos los filtros se combinan con AND. idRol corresponde al paso vigente de cada "
                    + "procedimiento: EN_CURSO, luego PENDIENTE, o el último completado si no hay abiertos. "
                    + "total y total-registros contienen el conteo filtrado.")
    @Parameters({
        @Parameter(name = "pacienteId", in = ParameterIn.QUERY, description = "UUID de la relación paciente persona-rol",
                schema = @Schema(type = SchemaType.STRING, format = "uuid")),
        @Parameter(name = "idClinica", in = ParameterIn.QUERY, description = "UUID de la clínica de la consulta",
                schema = @Schema(type = SchemaType.STRING, format = "uuid")),
        @Parameter(name = "idRol", in = ParameterIn.QUERY, description = "UUID del rol responsable del paso vigente",
                schema = @Schema(type = SchemaType.STRING, format = "uuid")),
        // Ambos filtros admiten fecha o fecha-hora; un único format excluiría una variante válida.
        @Parameter(name = "desde", in = ParameterIn.QUERY, description = "Inicio inclusivo de fechaInicio: YYYY-MM-DD en America/El_Salvador o fecha-hora ISO 8601 con zona",
                schema = @Schema(type = SchemaType.STRING)),
        @Parameter(name = "hasta", in = ParameterIn.QUERY, description = "Fin inclusivo de fechaInicio; YYYY-MM-DD incluye todo el día en America/El_Salvador, o fecha-hora ISO 8601 con zona",
                schema = @Schema(type = SchemaType.STRING))
    })
    public PageResponse<ConsultaDTO> listar(
            @QueryParam("first") @jakarta.ws.rs.DefaultValue("0") int first,
            @QueryParam("max") @jakarta.ws.rs.DefaultValue("20") int max,
            @QueryParam("q") String q) {
        if (idClinica != null || idRol != null || desde != null || hasta != null) {
            if (first < 0 || max < 1) throw new BadRequestException("Paginación inválida.");
            var inicio = fechaFiltro(desde, false);
            var fin = fechaFiltro(hasta, true);
            if (inicio != null && fin != null && inicio.after(fin)) {
                throw new BadRequestException("desde no puede ser posterior a hasta.");
            }
            var filtro = new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.FiltroConsultasRest(
                    uuidFiltro(pacienteId, "pacienteId"), uuidFiltro(idClinica, "idClinica"),
                    uuidFiltro(idRol, "idRol"), inicio, fin, q);
            max = Math.min(max, 100);
            var items = consultaService.findRangeRest(first, max, filtro).stream().map(getMapper()::toDTO).toList();
            return new PageResponse<>(items, consultaService.countRest(filtro), first, max);
        }
        if (pacienteId == null) {
            return super.listar(first, max, q);
        }
        UUID id;
        try {
            id = UUID.fromString(pacienteId.trim());
            if (!id.toString().equalsIgnoreCase(pacienteId.trim())) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("El parámetro pacienteId debe ser un UUID válido.", ex);
        }
        if (first < 0) {
            throw new BadRequestException("El parámetro first debe ser mayor o igual a 0.");
        }
        if (max < 1) {
            throw new BadRequestException("El parámetro max debe ser mayor a 0.");
        }
        max = Math.min(max, 100);
        String filtro = q == null || q.isBlank() ? null : q.trim();
        var items = consultaService.findRange(first, max, filtro, id).stream()
                .map(getMapper()::toDTO)
                .toList();
        long total = consultaService.count(filtro, id);
        return new PageResponse<>(items, total, first, max);
    }

    private UUID uuidFiltro(String valor, String nombre) {
        if (valor == null) return null;
        try {
            UUID id = UUID.fromString(valor.trim());
            if (!id.toString().equalsIgnoreCase(valor.trim())) throw new IllegalArgumentException();
            return id;
        } catch (IllegalArgumentException e) { throw new BadRequestException(nombre + " debe ser un UUID válido."); }
    }

    private java.util.Date fechaFiltro(String valor, boolean fin) {
        if (valor == null) return null;
        try {
            if (valor.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                var fecha = java.time.LocalDate.parse(valor);
                var zona = java.time.ZoneId.of("America/El_Salvador");
                var instante = (fin ? fecha.plusDays(1) : fecha).atStartOfDay(zona).toInstant();
                return java.util.Date.from(fin ? instante.minusMillis(1) : instante);
            }
            return java.util.Date.from(java.time.OffsetDateTime.parse(valor).toInstant());
        } catch (java.time.DateTimeException e) { throw new BadRequestException("Fecha inválida: use YYYY-MM-DD o fecha-hora ISO 8601 con zona."); }
    }

    @Override
    protected DefaultService<Consulta, UUID> getService() {
        return consultaService;
    }

    @Override
    protected EntityMapper<Consulta, ConsultaDTO> getMapper() {
        return consultaMapper;
    }

    @Override
    protected UUID getId(ConsultaDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(Consulta entidad, UUID id) {
        entidad.setIdConsulta(id);
    }

    @Override
    protected void asignarNuevoId(Consulta entidad) {
        entidad.setIdConsulta(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
