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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.RolDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.RolMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolResourceTest {

    @Mock
    RolService rolService;

    @Mock
    RolMapper rolMapper;

    @InjectMocks
    RolResource rolResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        Rol entidad = new Rol(UUID.randomUUID());
        RolDTO dto = new RolDTO(entidad.getIdRol(), "Rol", Boolean.TRUE, null);
        when(rolService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(rolService.count(null)).thenReturn(1L);
        when(rolMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<RolDTO> respuesta = rolResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(rolService).findRange(0, 100, null);
        verify(rolService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(rolService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> rolResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        RolDTO dto = new RolDTO(null, "Nuevo", Boolean.TRUE, null);
        Rol entidad = new Rol();
        RolDTO creada = new RolDTO(UUID.randomUUID(), "Nuevo", Boolean.TRUE, null);
        when(rolMapper.toEntity(dto)).thenReturn(entidad);
        when(rolMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = rolResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(rolService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        Rol entidad = new Rol(id);
        RolDTO dto = new RolDTO(null, "Actualizado", Boolean.TRUE, null);
        RolDTO esperada = new RolDTO(id, "Actualizado", Boolean.TRUE, null);
        when(rolService.findById(id)).thenReturn(entidad);
        when(rolMapper.toDTO(entidad)).thenReturn(esperada);

        RolDTO respuesta = rolResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(esperada), dto);

        assertEquals(esperada, respuesta);
        verify(rolMapper).copiar(dto, entidad);
        verify(rolService).actualizar(entidad);
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        Rol entidad = new Rol(id);
        when(rolService.findById(id)).thenReturn(entidad);

        Response respuesta = rolResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        assertFalse(entidad.getActivo());
        verify(rolService).actualizar(entidad);
    }
}
