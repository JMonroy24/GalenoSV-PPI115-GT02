package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDetalleDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaDetalleMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaTicketService;
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

    @GET
    @Path("/{id}/ticket")
    @Produces("application/pdf")
    @PermitAll
    @Operation(summary = "Descarga el ticket PDF de una consulta con procedimientos y pasos")
    public Response descargarTicket(@PathParam("id") String id) {
        final UUID clave;
        try {
            clave = UUID.fromString(id);
            if (!clave.toString().equalsIgnoreCase(id)) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("El identificador de consulta debe ser un UUID válido.", e);
        }
        return Response.ok(consultaTicketService.generar(clave), "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"ticket-cita-" + clave + ".pdf\"")
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
    @Operation(summary = "Lista los recursos de forma paginada con filtro opcional por pacienteId")
    public PageResponse<ConsultaDTO> listar(
            @QueryParam("first") @jakarta.ws.rs.DefaultValue("0") int first,
            @QueryParam("max") @jakarta.ws.rs.DefaultValue("20") int max,
            @QueryParam("q") String q) {
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
