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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.PersonaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.PersonaMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaResourceTest {

    @Mock
    PersonaService personaService;

    @Mock
    PersonaMapper personaMapper;

    @InjectMocks
    PersonaResource personaResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        Persona entidad = new Persona(UUID.randomUUID());
        PersonaDTO dto = new PersonaDTO(entidad.getIdPersona(), "Ana", "Pérez", null, null);
        when(personaService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(personaService.count(null)).thenReturn(1L);
        when(personaMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<PersonaDTO> respuesta = personaResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(personaService).findRange(0, 100, null);
        verify(personaService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(personaService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> personaResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        PersonaDTO dto = new PersonaDTO(null, "Ana", "Pérez", null, null);
        Persona entidad = new Persona();
        PersonaDTO creada = new PersonaDTO(UUID.randomUUID(), "Ana", "Pérez", null, null);
        when(personaMapper.toEntity(dto)).thenReturn(entidad);
        when(personaMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = personaResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(personaService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        Persona entidad = new Persona(id);
        PersonaDTO dto = new PersonaDTO(null, "Ana María", "Pérez", null, null);
        PersonaDTO esperada = new PersonaDTO(id, "Ana María", "Pérez", null, null);
        when(personaService.findById(id)).thenReturn(entidad);
        when(personaMapper.toDTO(entidad)).thenReturn(esperada);

        PersonaDTO respuesta = personaResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(esperada), dto);

        assertEquals(esperada, respuesta);
        verify(personaMapper).copiar(dto, entidad);
        verify(personaService).actualizar(entidad);
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        Persona entidad = new Persona(id);
        when(personaService.findById(id)).thenReturn(entidad);

        Response respuesta = personaResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(personaService).eliminar(entidad);
    }
}
