package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.PersonaRolDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ClinicaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

@ApplicationScoped
public class PersonaRolMapper implements EntityMapper<PersonaRol, PersonaRolDTO> {

    @Inject
    ClinicaService clinicaService;

    @Inject
    PersonaService personaService;

    @Inject
    RolService rolService;

    @Override
    public PersonaRolDTO toDTO(PersonaRol entidad) {
        if (entidad == null) {
            return null;
        }
        return new PersonaRolDTO(
                entidad.getIdPersonaRol(),
                entidad.getFechaCreacion(),
                entidad.getIdClinica() == null ? null : entidad.getIdClinica().getIdClinica(),
                entidad.getIdPersona() == null ? null : entidad.getIdPersona().getIdPersona(),
                entidad.getIdRol() == null ? null : entidad.getIdRol().getIdRol());
    }

    @Override
    public PersonaRol toEntity(PersonaRolDTO dto) {
        if (dto == null) {
            return null;
        }
        PersonaRol entidad = new PersonaRol();
        entidad.setIdPersonaRol(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(PersonaRolDTO dto, PersonaRol destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setIdClinica(resolverClinica(dto.idClinica()));
        destino.setIdPersona(resolverPersona(dto.idPersona()));
        destino.setIdRol(resolverRol(dto.idRol()));
    }

    private Clinica resolverClinica(UUID id) {
        if (id == null) {
            return null;
        }
        Clinica clinica = clinicaService.findById(id);
        if (clinica == null) {
            throw new NotFoundException("No existe la clinica solicitada.");
        }
        return clinica;
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

    private Rol resolverRol(UUID id) {
        if (id == null) {
            return null;
        }
        Rol rol = rolService.findById(id);
        if (rol == null) {
            throw new NotFoundException("No existe el rol solicitado.");
        }
        return rol;
    }
}
