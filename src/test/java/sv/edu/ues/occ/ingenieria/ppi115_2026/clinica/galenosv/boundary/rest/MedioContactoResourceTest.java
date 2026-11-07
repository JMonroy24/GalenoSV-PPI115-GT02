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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.MedioContactoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.MedioContactoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.MedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedioContactoResourceTest {

    @Mock
    MedioContactoService medioContactoService;

    @Mock
    MedioContactoMapper medioContactoMapper;

    @InjectMocks
    MedioContactoResource medioContactoResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        MedioContacto entidad = new MedioContacto(UUID.randomUUID());
        MedioContactoDTO dto = new MedioContactoDTO(entidad.getIdMedioContacto(), "correo@dominio.com", null, null, null);
        when(medioContactoService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(medioContactoService.count(null)).thenReturn(1L);
        when(medioContactoMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<MedioContactoDTO> respuesta = medioContactoResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(medioContactoService).findRange(0, 100, null);
        verify(medioContactoService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(medioContactoService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> medioContactoResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        MedioContactoDTO dto = new MedioContactoDTO(null, "correo@dominio.com", null, UUID.randomUUID(), UUID.randomUUID());
        MedioContacto entidad = new MedioContacto();
        MedioContactoDTO creada = new MedioContactoDTO(UUID.randomUUID(), "correo@dominio.com", null,
                dto.idPersona(), dto.idTipoMedioContacto());
        when(medioContactoMapper.toEntity(dto)).thenReturn(entidad);
        when(medioContactoMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = medioContactoResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(medioContactoService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        MedioContacto entidad = new MedioContacto(id);
        MedioContactoDTO dto = new MedioContactoDTO(null, "7777-7777", null, UUID.randomUUID(), UUID.randomUUID());
        MedioContactoDTO esperada = new MedioContactoDTO(id, "7777-7777", null,
                dto.idPersona(), dto.idTipoMedioContacto());
        when(medioContactoService.findById(id)).thenReturn(entidad);
        when(medioContactoMapper.toDTO(entidad)).thenReturn(esperada);

        MedioContactoDTO respuesta = medioContactoResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(esperada), dto);

        assertEquals(esperada, respuesta);
        verify(medioContactoMapper).copiar(dto, entidad);
        verify(medioContactoService).actualizar(entidad);
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        MedioContacto entidad = new MedioContacto(id);
        when(medioContactoService.findById(id)).thenReturn(entidad);

        Response respuesta = medioContactoResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(medioContactoService).eliminar(entidad);
    }
}
