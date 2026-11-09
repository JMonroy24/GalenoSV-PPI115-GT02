package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.PersonaRolDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ClinicaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaRolMapperTest {

    @Mock
    ClinicaService clinicaService;

    @Mock
    PersonaService personaService;

    @Mock
    RolService rolService;

    @InjectMocks
    PersonaRolMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelaciones() {
        UUID id = UUID.randomUUID();
        Date fecha = new Date(1700000000000L);
        Clinica clinica = new Clinica(UUID.randomUUID());
        Persona persona = new Persona(UUID.randomUUID());
        Rol rol = new Rol(UUID.randomUUID());
        PersonaRol entidad = new PersonaRol(id);
        entidad.setFechaCreacion(fecha);
        entidad.setIdClinica(clinica);
        entidad.setIdPersona(persona);
        entidad.setIdRol(rol);

        PersonaRolDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals(fecha, dto.fechaCreacion());
        assertEquals(clinica.getIdClinica(), dto.idClinica());
        assertEquals(persona.getIdPersona(), dto.idPersona());
        assertEquals(rol.getIdRol(), dto.idRol());

        when(clinicaService.findById(clinica.getIdClinica())).thenReturn(clinica);
        when(personaService.findById(persona.getIdPersona())).thenReturn(persona);
        when(rolService.findById(rol.getIdRol())).thenReturn(rol);
        PersonaRol vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdPersonaRol());
        assertNull(vuelta.getFechaCreacion());
        assertSame(clinica, vuelta.getIdClinica());
        assertSame(persona, vuelta.getIdPersona());
        assertSame(rol, vuelta.getIdRol());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        PersonaRol destino = new PersonaRol(idDestino);
        Date fechaOriginal = new Date(1600000000000L);
        destino.setFechaCreacion(fechaOriginal);
        Clinica clinica = new Clinica(UUID.randomUUID());
        Persona persona = new Persona(UUID.randomUUID());
        Rol rol = new Rol(UUID.randomUUID());
        PersonaRolDTO dto = new PersonaRolDTO(UUID.randomUUID(), new Date(1800000000000L),
                clinica.getIdClinica(), persona.getIdPersona(), rol.getIdRol());
        when(clinicaService.findById(clinica.getIdClinica())).thenReturn(clinica);
        when(personaService.findById(persona.getIdPersona())).thenReturn(persona);
        when(rolService.findById(rol.getIdRol())).thenReturn(rol);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdPersonaRol());
        assertEquals(fechaOriginal, destino.getFechaCreacion());
        assertSame(clinica, destino.getIdClinica());
        assertSame(persona, destino.getIdPersona());
        assertSame(rol, destino.getIdRol());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        Clinica clinica = new Clinica(UUID.randomUUID());
        UUID idPersona = UUID.randomUUID();
        PersonaRolDTO dto = new PersonaRolDTO(null, null,
                clinica.getIdClinica(), idPersona, UUID.randomUUID());
        when(clinicaService.findById(clinica.getIdClinica())).thenReturn(clinica);
        when(personaService.findById(idPersona)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}
