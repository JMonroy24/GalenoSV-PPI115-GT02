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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenAsignableDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ExamenAsignableMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ProcedimientoPasoExamenMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoExamenResourceTest {

    @Mock
    ProcedimientoPasoExamenService procedimientoPasoExamenService;

    @Mock
    ProcedimientoPasoExamenMapper procedimientoPasoExamenMapper;

    @Mock
    ExamenAsignableMapper examenAsignableMapper;

    @InjectMocks
    ProcedimientoPasoExamenResource procedimientoPasoExamenResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        ProcedimientoPasoExamen entidad = new ProcedimientoPasoExamen(UUID.randomUUID());
        ProcedimientoPasoExamenDTO dto = new ProcedimientoPasoExamenDTO(entidad.getIdProcedimientoPasoExamen(),
                null, Boolean.TRUE, null, UUID.randomUUID(), UUID.randomUUID());
        when(procedimientoPasoExamenService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(procedimientoPasoExamenService.count(null)).thenReturn(1L);
        when(procedimientoPasoExamenMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<ProcedimientoPasoExamenDTO> respuesta = procedimientoPasoExamenResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(procedimientoPasoExamenService).findRange(0, 100, null);
        verify(procedimientoPasoExamenService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(procedimientoPasoExamenService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> procedimientoPasoExamenResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        ProcedimientoPasoExamenDTO dto = new ProcedimientoPasoExamenDTO(null, null, Boolean.TRUE, null,
                UUID.randomUUID(), UUID.randomUUID());
        ProcedimientoPasoExamen entidad = new ProcedimientoPasoExamen();
        ProcedimientoPasoExamenDTO creada = new ProcedimientoPasoExamenDTO(UUID.randomUUID(),
                dto.fechaCreacion(), dto.activo(), dto.observaciones(), dto.idExamen(), dto.idProcedimientoPaso());
        when(procedimientoPasoExamenMapper.toEntity(dto)).thenReturn(entidad);
        when(procedimientoPasoExamenMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = procedimientoPasoExamenResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(procedimientoPasoExamenService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        ProcedimientoPasoExamen entidad = new ProcedimientoPasoExamen(id);
        ProcedimientoPasoExamenDTO dto = new ProcedimientoPasoExamenDTO(null, null, Boolean.TRUE, null,
                UUID.randomUUID(), UUID.randomUUID());
        ProcedimientoPasoExamenDTO esperada = new ProcedimientoPasoExamenDTO(id,
                dto.fechaCreacion(), dto.activo(), dto.observaciones(), dto.idExamen(), dto.idProcedimientoPaso());
        when(procedimientoPasoExamenMapper.toDTO(entidad)).thenReturn(esperada);
        when(procedimientoPasoExamenService.actualizarSi(any(), any(), any())).thenAnswer(inv -> {
            java.util.function.Predicate<Object> precondicion = inv.getArgument(1);
            java.util.function.Consumer<Object> cambios = inv.getArgument(2);
            if (!precondicion.test(entidad)) {
                throw new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException("El ETag no coincide.");
            }
            cambios.accept(entidad);
            return entidad;
        });

        Response respuesta = procedimientoPasoExamenResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), dto);

        assertEquals(esperada, respuesta.getEntity());
        assertEquals(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), respuesta.getHeaderString("ETag"));
        verify(procedimientoPasoExamenMapper).copiar(dto, entidad);
        verify(procedimientoPasoExamenService).actualizarSi(any(), any(), any());
    }

    @Test
    void asignablesPorPasoFiltraYPagina() {
        UUID idPaso = UUID.randomUUID();
        ProcedimientoPasoExamen relacion1 = new ProcedimientoPasoExamen(UUID.randomUUID());
        ProcedimientoPasoExamen relacion2 = new ProcedimientoPasoExamen(UUID.randomUUID());
        ExamenAsignableDTO dto1 = new ExamenAsignableDTO(UUID.randomUUID(), UUID.randomUUID(), "Hemograma",
                Boolean.TRUE, idPaso, UUID.randomUUID(), "Laboratorio");
        ExamenAsignableDTO dto2 = new ExamenAsignableDTO(UUID.randomUUID(), UUID.randomUUID(), "Rayos X",
                Boolean.TRUE, idPaso, UUID.randomUUID(), "Imagenología");
        when(procedimientoPasoExamenService.findByPaso(idPaso)).thenReturn(List.of(relacion1, relacion2));
        when(examenAsignableMapper.toDTO(relacion1)).thenReturn(dto1);
        when(examenAsignableMapper.toDTO(relacion2)).thenReturn(dto2);

        PageResponse<ExamenAsignableDTO> respuesta =
                procedimientoPasoExamenResource.asignablesPorPaso(idPaso, 0, 1, "hemo");

        assertEquals(1L, respuesta.total());
        assertEquals(1, respuesta.items().size());
        assertEquals("Hemograma", respuesta.items().get(0).nombre());
        verify(procedimientoPasoExamenService).findByPaso(idPaso);
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        ProcedimientoPasoExamen entidad = new ProcedimientoPasoExamen(id);
        when(procedimientoPasoExamenService.findById(id)).thenReturn(entidad);

        Response respuesta = procedimientoPasoExamenResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        assertFalse(entidad.getActivo());
        verify(procedimientoPasoExamenService).actualizar(entidad);
    }
}
