package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ClinicaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ClinicaMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ClinicaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;

@Tag(name = "Clínicas", description = "Gestión de clínicas.")
@jakarta.ws.rs.Path("/clinicas")
@ApplicationScoped
public class ClinicaResource extends GenericResource<Clinica, ClinicaDTO, UUID> {

    @Inject
    ClinicaService clinicaService;

    @Inject
    ClinicaMapper clinicaMapper;

    @Override
    protected DefaultService<Clinica, UUID> getService() {
        return clinicaService;
    }

    @Override
    protected EntityMapper<Clinica, ClinicaDTO> getMapper() {
        return clinicaMapper;
    }

    @Override
    protected UUID getId(ClinicaDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(Clinica entidad, UUID id) {
        entidad.setIdClinica(id);
    }

    @Override
    protected void asignarNuevoId(Clinica entidad) {
        entidad.setIdClinica(UUID.randomUUID());
    }

    @Override
    protected boolean borradoLogico() {
        return true;
    }

    @Override
    protected void marcarInactivo(Clinica entidad) {
        entidad.setActivo(Boolean.FALSE);
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
