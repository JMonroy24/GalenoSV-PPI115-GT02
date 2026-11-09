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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.TipoExamenMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoExamenResourceTest {

    @Mock
    TipoExamenService tipoExamenService;

    @Mock
    TipoExamenMapper tipoExamenMapper;

    @InjectMocks
    TipoExamenResource tipoExamenResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        TipoExamen entidad = new TipoExamen(UUID.randomUUID());
        TipoExamenDTO dto = new TipoExamenDTO(entidad.getIdTipoExamen(), "Hemograma", Boolean.TRUE, null);
        when(tipoExamenService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(tipoExamenService.count(null)).thenReturn(1L);
        when(tipoExamenMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<TipoExamenDTO> respuesta = tipoExamenResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(tipoExamenService).findRange(0, 100, null);
        verify(tipoExamenService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(tipoExamenService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> tipoExamenResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        TipoExamenDTO dto = new TipoExamenDTO(null, "Hemograma", Boolean.TRUE, null);
        TipoExamen entidad = new TipoExamen();
        TipoExamenDTO creada = new TipoExamenDTO(UUID.randomUUID(), "Hemograma", Boolean.TRUE, null);
        when(tipoExamenMapper.toEntity(dto)).thenReturn(entidad);
        when(tipoExamenMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = tipoExamenResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(tipoExamenService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        TipoExamen entidad = new TipoExamen(id);
        TipoExamenDTO dto = new TipoExamenDTO(null, "Química sanguínea", Boolean.TRUE, null);
        TipoExamenDTO esperada = new TipoExamenDTO(id, "Química sanguínea", Boolean.TRUE, null);
        when(tipoExamenMapper.toDTO(entidad)).thenReturn(esperada);
        when(tipoExamenService.actualizarSi(any(), any(), any())).thenAnswer(inv -> {
            java.util.function.Predicate<Object> precondicion = inv.getArgument(1);
            java.util.function.Consumer<Object> cambios = inv.getArgument(2);
            if (!precondicion.test(entidad)) {
                throw new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException("El ETag no coincide.");
            }
            cambios.accept(entidad);
            return entidad;
        });

        Response respuesta = tipoExamenResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), dto);

        assertEquals(esperada, respuesta.getEntity());
        assertEquals(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), respuesta.getHeaderString("ETag"));
        verify(tipoExamenMapper).copiar(dto, entidad);
        verify(tipoExamenService).actualizarSi(any(), any(), any());
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        TipoExamen entidad = new TipoExamen(id);
        when(tipoExamenService.findById(id)).thenReturn(entidad);

        Response respuesta = tipoExamenResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        assertFalse(entidad.getActivo());
        verify(tipoExamenService).actualizar(entidad);
    }
}
