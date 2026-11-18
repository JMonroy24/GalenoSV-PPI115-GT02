package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenAsignableDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ExamenAsignableMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ProcedimientoPasoExamenMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

@Tag(name = "Exámenes por paso de procedimiento", description = "Exámenes requeridos por cada paso de procedimiento.")
@jakarta.ws.rs.Path("/procedimientos-pasos-examenes")
@ApplicationScoped
public class ProcedimientoPasoExamenResource
        extends GenericResource<ProcedimientoPasoExamen, ProcedimientoPasoExamenDTO, UUID> {

    @Inject
    ProcedimientoPasoExamenService procedimientoPasoExamenService;

    @Inject
    ProcedimientoPasoExamenMapper procedimientoPasoExamenMapper;

    @Inject
    ExamenAsignableMapper examenAsignableMapper;

    @GET
    @Path("/asignables")
    @PermitAll
    @Operation(summary = "Lista los exámenes asignables a un paso con su categoría")
    public PageResponse<ExamenAsignableDTO> asignablesPorPaso(
            @QueryParam("idProcedimientoPaso") UUID idProcedimientoPaso,
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("20") int max,
            @QueryParam("q") String q) {
        if (first < 0) {
            throw new BadRequestException("El parámetro first debe ser mayor o igual a 0.");
        }
        if (max < 1) {
            throw new BadRequestException("El parámetro max debe ser mayor a 0.");
        }
        max = Math.min(max, 100);
        String filtro = q == null || q.isBlank() ? null : q.trim().toLowerCase(Locale.ROOT);
        List<ExamenAsignableDTO> encontrados = procedimientoPasoExamenService.findByPaso(idProcedimientoPaso)
                .stream()
                .map(examenAsignableMapper::toDTO)
                .filter(item -> filtro == null || coincide(item, filtro))
                .toList();
        List<ExamenAsignableDTO> items = first >= encontrados.size()
                ? List.of()
                : encontrados.subList(first, Math.min(first + max, encontrados.size()));
        return new PageResponse<>(items, encontrados.size(), first, max);
    }

    private boolean coincide(ExamenAsignableDTO item, String filtro) {
        return contiene(item.nombre(), filtro) || contiene(item.nombreTipoExamen(), filtro)
                || (item.idExamen() != null && item.idExamen().toString().contains(filtro));
    }

    private boolean contiene(String valor, String filtro) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(filtro);
    }

    @Override
    protected DefaultService<ProcedimientoPasoExamen, UUID> getService() {
        return procedimientoPasoExamenService;
    }

    @Override
    protected EntityMapper<ProcedimientoPasoExamen, ProcedimientoPasoExamenDTO> getMapper() {
        return procedimientoPasoExamenMapper;
    }

    @Override
    protected UUID getId(ProcedimientoPasoExamenDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(ProcedimientoPasoExamen entidad, UUID id) {
        entidad.setIdProcedimientoPasoExamen(id);
    }

    @Override
    protected void asignarNuevoId(ProcedimientoPasoExamen entidad) {
        entidad.setIdProcedimientoPasoExamen(UUID.randomUUID());
    }

    @Override
    protected boolean borradoLogico() {
        return true;
    }

    @Override
    protected void marcarInactivo(ProcedimientoPasoExamen entidad) {
        entidad.setActivo(Boolean.FALSE);
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
