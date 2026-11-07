package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.MedioContactoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoMedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

@ApplicationScoped
public class MedioContactoMapper implements EntityMapper<MedioContacto, MedioContactoDTO> {

    @Inject
    PersonaService personaService;

    @Inject
    TipoMedioContactoService tipoMedioContactoService;

    @Override
    public MedioContactoDTO toDTO(MedioContacto entidad) {
        if (entidad == null) {
            return null;
        }
        return new MedioContactoDTO(
                entidad.getIdMedioContacto(),
                entidad.getValor(),
                entidad.getFechaCreacion(),
                entidad.getIdPersona() == null ? null : entidad.getIdPersona().getIdPersona(),
                entidad.getIdTipoMedioContacto() == null ? null : entidad.getIdTipoMedioContacto().getIdTipoMedioContacto());
    }

    @Override
    public MedioContacto toEntity(MedioContactoDTO dto) {
        if (dto == null) {
            return null;
        }
        MedioContacto entidad = new MedioContacto();
        entidad.setIdMedioContacto(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(MedioContactoDTO dto, MedioContacto destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setValor(dto.valor());
        destino.setFechaCreacion(dto.fechaCreacion());
        destino.setIdPersona(resolverPersona(dto.idPersona()));
        destino.setIdTipoMedioContacto(resolverTipoMedioContacto(dto.idTipoMedioContacto()));
    }

    private Persona resolverPersona(UUID id) {
        if (id == null) {
            return null;
        }
        Persona persona = personaService.findById(id);
        if (persona == null) {
            throw new NotFoundException("No existe la persona solicitada.");
        }
        return persona;
    }

    private TipoMedioContacto resolverTipoMedioContacto(UUID id) {
        if (id == null) {
            return null;
        }
        TipoMedioContacto tipo = tipoMedioContactoService.findById(id);
        if (tipo == null) {
            throw new NotFoundException("No existe el tipo de medio de contacto solicitado.");
        }
        return tipo;
    }
}
