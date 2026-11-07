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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.OrdenExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.OrdenExamenMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenExamenResourceTest {

    @Mock
    OrdenExamenService ordenExamenService;

    @Mock
    OrdenExamenMapper ordenExamenMapper;

    @InjectMocks
    OrdenExamenResource ordenExamenResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        OrdenExamen entidad = new OrdenExamen(UUID.randomUUID());
        OrdenExamenDTO dto = new OrdenExamenDTO(entidad.getIdOrdenExamen(),
                null, "Indicaciones", UUID.randomUUID());
        when(ordenExamenService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(ordenExamenService.count(null)).thenReturn(1L);
        when(ordenExamenMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<OrdenExamenDTO> respuesta = ordenExamenResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(ordenExamenService).findRange(0, 100, null);
        verify(ordenExamenService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(ordenExamenService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> ordenExamenResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        OrdenExamenDTO dto = new OrdenExamenDTO(null, null, "Indicaciones", UUID.randomUUID());
        OrdenExamen entidad = new OrdenExamen();
        OrdenExamenDTO creada = new OrdenExamenDTO(UUID.randomUUID(),
                dto.fechaCreacion(), dto.indicaciones(), dto.idConsultaProcedimientoPaso());
        when(ordenExamenMapper.toEntity(dto)).thenReturn(entidad);
        when(ordenExamenMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = ordenExamenResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(ordenExamenService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        OrdenExamen entidad = new OrdenExamen(id);
        OrdenExamenDTO dto = new OrdenExamenDTO(null, null, "Indicaciones", UUID.randomUUID());
        OrdenExamenDTO esperada = new OrdenExamenDTO(id,
                dto.fechaCreacion(), dto.indicaciones(), dto.idConsultaProcedimientoPaso());
        when(ordenExamenService.findById(id)).thenReturn(entidad);
        when(ordenExamenMapper.toDTO(entidad)).thenReturn(esperada);

        OrdenExamenDTO respuesta = ordenExamenResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(esperada), dto);

        assertEquals(esperada, respuesta);
        verify(ordenExamenMapper).copiar(dto, entidad);
        verify(ordenExamenService).actualizar(entidad);
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        OrdenExamen entidad = new OrdenExamen(id);
        when(ordenExamenService.findById(id)).thenReturn(entidad);

        Response respuesta = ordenExamenResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(ordenExamenService).eliminar(entidad);
    }
}
