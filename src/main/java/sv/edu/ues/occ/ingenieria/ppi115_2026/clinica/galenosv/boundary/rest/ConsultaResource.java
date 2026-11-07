package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;

@Tag(name = "Consultas", description = "Consultas médicas.")
@jakarta.ws.rs.Path("/consultas")
@ApplicationScoped
public class ConsultaResource extends GenericResource<Consulta, ConsultaDTO, UUID> {

    @Inject
    ConsultaService consultaService;

    @Inject
    ConsultaMapper consultaMapper;

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
