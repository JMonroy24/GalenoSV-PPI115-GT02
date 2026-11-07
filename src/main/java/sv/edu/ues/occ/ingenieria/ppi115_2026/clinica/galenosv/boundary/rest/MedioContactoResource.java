package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.MedioContactoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.MedioContactoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.MedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;

@Tag(name = "Medios de contacto", description = "Medios de contacto de personas.")
@jakarta.ws.rs.Path("/medios-contacto")
@ApplicationScoped
public class MedioContactoResource extends GenericResource<MedioContacto, MedioContactoDTO, UUID> {

    @Inject
    MedioContactoService medioContactoService;

    @Inject
    MedioContactoMapper medioContactoMapper;

    @Override
    protected DefaultService<MedioContacto, UUID> getService() {
        return medioContactoService;
    }

    @Override
    protected EntityMapper<MedioContacto, MedioContactoDTO> getMapper() {
        return medioContactoMapper;
    }

    @Override
    protected UUID getId(MedioContactoDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(MedioContacto entidad, UUID id) {
        entidad.setIdMedioContacto(id);
    }

    @Override
    protected void asignarNuevoId(MedioContacto entidad) {
        entidad.setIdMedioContacto(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
