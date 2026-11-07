package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.PersonaRolDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.PersonaRolMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaRolResourceTest {

    @Mock
    PersonaRolService personaRolService;

    @Mock
    PersonaRolMapper personaRolMapper;

    @InjectMocks
    PersonaRolResource personaRolResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        PersonaRol entidad = new PersonaRol(UUID.randomUUID());
        PersonaRolDTO dto = new PersonaRolDTO(entidad.getIdPersonaRol(), null, null, null, null);
        when(personaRolService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(personaRolService.count(null)).thenReturn(1L);
        when(personaRolMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<PersonaRolDTO> respuesta = personaRolResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(personaRolService).findRange(0, 100, null);
        verify(personaRolService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(personaRolService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> personaRolResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        PersonaRolDTO dto = new PersonaRolDTO(null, null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        PersonaRol entidad = new PersonaRol();
        PersonaRolDTO creada = new PersonaRolDTO(UUID.randomUUID(), null,
                dto.idClinica(), dto.idPersona(), dto.idRol());
        when(personaRolMapper.toEntity(dto)).thenReturn(entidad);
        when(personaRolMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = personaRolResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(personaRolService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        PersonaRol entidad = new PersonaRol(id);
        PersonaRolDTO dto = new PersonaRolDTO(null, null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        PersonaRolDTO esperada = new PersonaRolDTO(id, null,
                dto.idClinica(), dto.idPersona(), dto.idRol());
        when(personaRolService.findById(id)).thenReturn(entidad);
        when(personaRolMapper.toDTO(entidad)).thenReturn(esperada);

        PersonaRolDTO respuesta = personaRolResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(esperada), dto);

        assertEquals(esperada, respuesta);
        verify(personaRolMapper).copiar(dto, entidad);
        verify(personaRolService).actualizar(entidad);
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        PersonaRol entidad = new PersonaRol(id);
        when(personaRolService.findById(id)).thenReturn(entidad);

        Response respuesta = personaRolResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(personaRolService).eliminar(entidad);
    }
}
