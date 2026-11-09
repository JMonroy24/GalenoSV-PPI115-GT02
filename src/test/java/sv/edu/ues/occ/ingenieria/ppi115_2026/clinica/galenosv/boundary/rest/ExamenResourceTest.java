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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ExamenMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenResourceTest {

    @Mock
    ExamenService examenService;

    @Mock
    ExamenMapper examenMapper;

    @InjectMocks
    ExamenResource examenResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        Examen entidad = new Examen(UUID.randomUUID());
        ExamenDTO dto = new ExamenDTO(entidad.getIdExamen(), "Hemograma", Boolean.TRUE, null);
        when(examenService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(examenService.count(null)).thenReturn(1L);
        when(examenMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<ExamenDTO> respuesta = examenResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(examenService).findRange(0, 100, null);
        verify(examenService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(examenService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> examenResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        ExamenDTO dto = new ExamenDTO(null, "Nuevo", Boolean.TRUE, null);
        Examen entidad = new Examen();
        ExamenDTO creada = new ExamenDTO(UUID.randomUUID(), "Nuevo", Boolean.TRUE, null);
        when(examenMapper.toEntity(dto)).thenReturn(entidad);
        when(examenMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = examenResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(examenService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        Examen entidad = new Examen(id);
        ExamenDTO dto = new ExamenDTO(null, "Actualizado", Boolean.TRUE, null);
        ExamenDTO esperada = new ExamenDTO(id, "Actualizado", Boolean.TRUE, null);
        when(examenMapper.toDTO(entidad)).thenReturn(esperada);
        when(examenService.actualizarSi(any(), any(), any())).thenAnswer(inv -> {
            java.util.function.Predicate<Object> precondicion = inv.getArgument(1);
            java.util.function.Consumer<Object> cambios = inv.getArgument(2);
            if (!precondicion.test(entidad)) {
                throw new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException("El ETag no coincide.");
            }
            cambios.accept(entidad);
            return entidad;
        });

        Response respuesta = examenResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), dto);

        assertEquals(esperada, respuesta.getEntity());
        assertEquals(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), respuesta.getHeaderString("ETag"));
        verify(examenMapper).copiar(dto, entidad);
        verify(examenService).actualizarSi(any(), any(), any());
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        Examen entidad = new Examen(id);
        when(examenService.findById(id)).thenReturn(entidad);

        Response respuesta = examenResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        assertFalse(entidad.getActivo());
        verify(examenService).actualizar(entidad);
    }
}
