package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaProcedimientoPasoActualizacionDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionRequiredException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaProcedimientoPasoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaProcedimientoPasoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;

@Tag(name = "Consultas procedimientos pasos", description = "Pasos ejecutados de un procedimiento dentro de una consulta.")
@jakarta.ws.rs.Path("/consultas-procedimientos-pasos")
@ApplicationScoped
public class ConsultaProcedimientoPasoResource
        extends GenericResource<ConsultaProcedimientoPaso, ConsultaProcedimientoPasoDTO, UUID> {

    @Inject
    ConsultaProcedimientoPasoService consultaProcedimientoPasoService;

    @Inject
    ConsultaProcedimientoPasoMapper consultaProcedimientoPasoMapper;

    @PATCH
    @Path("/{id}")
    @PermitAll
    @Operation(summary = "Actualiza el paso y ejecuta su transición usando If-Match",
            description = "Al completar activa el sucesor o cierra la consulta. Para volver al paso anterior, "
                    + "envíe accion=RETROCEDER manteniendo estadoPaso y valor. Devuelve 412 si el ETag cambió.")
    public Response actualizarFormulario(@PathParam("id") String id,
            @HeaderParam("If-Match") String ifMatch,
            @Valid ConsultaProcedimientoPasoActualizacionDTO dto) {
        if (ifMatch == null || ifMatch.isBlank()) {
            throw new PreconditionRequiredException(
                    "Debe enviar el encabezado If-Match con el ETag actual del recurso.");
        }
        if (dto.accion() != null && !"RETROCEDER".equals(dto.accion())) {
            throw new jakarta.ws.rs.BadRequestException("Acción no válida.");
        }
        ConsultaProcedimientoPaso entidad = "RETROCEDER".equals(dto.accion())
                ? consultaProcedimientoPasoService.retroceder(parseId(id),
                        paso -> ETagUtils.coincide(ifMatch, ETagUtils.calcular(getMapper().toDTO(paso))), dto.valor())
                : consultaProcedimientoPasoService.actualizarFormulario(
                parseId(id),
                paso -> ETagUtils.coincide(ifMatch, ETagUtils.calcular(getMapper().toDTO(paso))),
                dto.estadoPaso(), dto.valor());
        ConsultaProcedimientoPasoDTO actualizada = getMapper().toDTO(entidad);
        return Response.ok(actualizada)
                .header("ETag", ETagUtils.calcularFuerte(actualizada))
                .build();
    }

    @Override
    protected DefaultService<ConsultaProcedimientoPaso, UUID> getService() {
        return consultaProcedimientoPasoService;
    }

    @Override
    protected EntityMapper<ConsultaProcedimientoPaso, ConsultaProcedimientoPasoDTO> getMapper() {
        return consultaProcedimientoPasoMapper;
    }

    @Override
    protected UUID getId(ConsultaProcedimientoPasoDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(ConsultaProcedimientoPaso entidad, UUID id) {
        entidad.setIdConsultaProcedimientoPaso(id);
    }

    @Override
    protected void asignarNuevoId(ConsultaProcedimientoPaso entidad) {
        entidad.setIdConsultaProcedimientoPaso(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
