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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoDocumentoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.TipoDocumentoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoDocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoDocumentoResourceTest {

    @Mock
    TipoDocumentoService tipoDocumentoService;

    @Mock
    TipoDocumentoMapper tipoDocumentoMapper;

    @InjectMocks
    TipoDocumentoResource tipoDocumentoResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        TipoDocumento entidad = new TipoDocumento(UUID.randomUUID());
        TipoDocumentoDTO dto = new TipoDocumentoDTO(entidad.getIdTipoDocumento(), "DUI", null, null, Boolean.TRUE);
        when(tipoDocumentoService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(tipoDocumentoService.count(null)).thenReturn(1L);
        when(tipoDocumentoMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<TipoDocumentoDTO> respuesta = tipoDocumentoResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(tipoDocumentoService).findRange(0, 100, null);
        verify(tipoDocumentoService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(tipoDocumentoService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> tipoDocumentoResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        TipoDocumentoDTO dto = new TipoDocumentoDTO(null, "DUI", null, null, Boolean.TRUE);
        TipoDocumento entidad = new TipoDocumento();
        TipoDocumentoDTO creada = new TipoDocumentoDTO(UUID.randomUUID(), "DUI", null, null, Boolean.TRUE);
        when(tipoDocumentoMapper.toEntity(dto)).thenReturn(entidad);
        when(tipoDocumentoMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = tipoDocumentoResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(tipoDocumentoService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        TipoDocumento entidad = new TipoDocumento(id);
        TipoDocumentoDTO dto = new TipoDocumentoDTO(null, "Pasaporte", null, null, Boolean.TRUE);
        TipoDocumentoDTO esperada = new TipoDocumentoDTO(id, "Pasaporte", null, null, Boolean.TRUE);
        when(tipoDocumentoService.findById(id)).thenReturn(entidad);
        when(tipoDocumentoMapper.toDTO(entidad)).thenReturn(esperada);

        TipoDocumentoDTO respuesta = tipoDocumentoResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(esperada), dto);

        assertEquals(esperada, respuesta);
        verify(tipoDocumentoMapper).copiar(dto, entidad);
        verify(tipoDocumentoService).actualizar(entidad);
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        TipoDocumento entidad = new TipoDocumento(id);
        when(tipoDocumentoService.findById(id)).thenReturn(entidad);

        Response respuesta = tipoDocumentoResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        assertFalse(entidad.getActivo());
        verify(tipoDocumentoService).actualizar(entidad);
    }
}
