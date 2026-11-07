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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ProcedimientoPasoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoResourceTest {

    @Mock
    ProcedimientoPasoService procedimientoPasoService;

    @Mock
    ProcedimientoPasoMapper procedimientoPasoMapper;

    @InjectMocks
    ProcedimientoPasoResource procedimientoPasoResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        ProcedimientoPaso entidad = new ProcedimientoPaso(UUID.randomUUID());
        ProcedimientoPasoDTO dto = new ProcedimientoPasoDTO(entidad.getIdProcedimientoPaso(),
                "Paso", Boolean.FALSE, UUID.randomUUID(), UUID.randomUUID());
        when(procedimientoPasoService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(procedimientoPasoService.count(null)).thenReturn(1L);
        when(procedimientoPasoMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<ProcedimientoPasoDTO> respuesta = procedimientoPasoResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(procedimientoPasoService).findRange(0, 100, null);
        verify(procedimientoPasoService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(procedimientoPasoService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> procedimientoPasoResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        ProcedimientoPasoDTO dto = new ProcedimientoPasoDTO(null, "Paso", Boolean.FALSE,
                UUID.randomUUID(), UUID.randomUUID());
        ProcedimientoPaso entidad = new ProcedimientoPaso();
        ProcedimientoPasoDTO creada = new ProcedimientoPasoDTO(UUID.randomUUID(),
                dto.nombre(), dto.indicaFin(), dto.idProcedimiento(), dto.idRol());
        when(procedimientoPasoMapper.toEntity(dto)).thenReturn(entidad);
        when(procedimientoPasoMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = procedimientoPasoResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(procedimientoPasoService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        ProcedimientoPaso entidad = new ProcedimientoPaso(id);
        ProcedimientoPasoDTO dto = new ProcedimientoPasoDTO(null, "Paso", Boolean.FALSE,
                UUID.randomUUID(), UUID.randomUUID());
        ProcedimientoPasoDTO esperada = new ProcedimientoPasoDTO(id,
                dto.nombre(), dto.indicaFin(), dto.idProcedimiento(), dto.idRol());
        when(procedimientoPasoService.findById(id)).thenReturn(entidad);
        when(procedimientoPasoMapper.toDTO(entidad)).thenReturn(esperada);

        ProcedimientoPasoDTO respuesta = procedimientoPasoResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(esperada), dto);

        assertEquals(esperada, respuesta);
        verify(procedimientoPasoMapper).copiar(dto, entidad);
        verify(procedimientoPasoService).actualizar(entidad);
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        ProcedimientoPaso entidad = new ProcedimientoPaso(id);
        when(procedimientoPasoService.findById(id)).thenReturn(entidad);

        Response respuesta = procedimientoPasoResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(procedimientoPasoService).eliminar(entidad);
    }
}
