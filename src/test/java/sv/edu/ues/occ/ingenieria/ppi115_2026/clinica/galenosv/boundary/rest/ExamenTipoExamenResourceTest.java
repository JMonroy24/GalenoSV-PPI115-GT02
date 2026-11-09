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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenTipoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ExamenTipoExamenMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenTipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenTipoExamenResourceTest {

    @Mock
    ExamenTipoExamenService examenTipoExamenService;

    @Mock
    ExamenTipoExamenMapper examenTipoExamenMapper;

    @InjectMocks
    ExamenTipoExamenResource examenTipoExamenResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        ExamenTipoExamen entidad = new ExamenTipoExamen(UUID.randomUUID());
        ExamenTipoExamenDTO dto = new ExamenTipoExamenDTO(entidad.getIdExamenTipoExamen(),
                null, null, UUID.randomUUID(), UUID.randomUUID());
        when(examenTipoExamenService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(examenTipoExamenService.count(null)).thenReturn(1L);
        when(examenTipoExamenMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<ExamenTipoExamenDTO> respuesta = examenTipoExamenResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(examenTipoExamenService).findRange(0, 100, null);
        verify(examenTipoExamenService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(examenTipoExamenService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> examenTipoExamenResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        ExamenTipoExamenDTO dto = new ExamenTipoExamenDTO(null, null, null,
                UUID.randomUUID(), UUID.randomUUID());
        ExamenTipoExamen entidad = new ExamenTipoExamen();
        ExamenTipoExamenDTO creada = new ExamenTipoExamenDTO(UUID.randomUUID(),
                dto.fechaCreacion(), dto.observaciones(), dto.idExamen(), dto.idTipoExamen());
        when(examenTipoExamenMapper.toEntity(dto)).thenReturn(entidad);
        when(examenTipoExamenMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = examenTipoExamenResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(examenTipoExamenService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        ExamenTipoExamen entidad = new ExamenTipoExamen(id);
        ExamenTipoExamenDTO dto = new ExamenTipoExamenDTO(null, null, null,
                UUID.randomUUID(), UUID.randomUUID());
        ExamenTipoExamenDTO esperada = new ExamenTipoExamenDTO(id,
                dto.fechaCreacion(), dto.observaciones(), dto.idExamen(), dto.idTipoExamen());
        when(examenTipoExamenMapper.toDTO(entidad)).thenReturn(esperada);
        when(examenTipoExamenService.actualizarSi(any(), any(), any())).thenAnswer(inv -> {
            java.util.function.Predicate<Object> precondicion = inv.getArgument(1);
            java.util.function.Consumer<Object> cambios = inv.getArgument(2);
            if (!precondicion.test(entidad)) {
                throw new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException("El ETag no coincide.");
            }
            cambios.accept(entidad);
            return entidad;
        });

        Response respuesta = examenTipoExamenResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), dto);

        assertEquals(esperada, respuesta.getEntity());
        assertEquals(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), respuesta.getHeaderString("ETag"));
        verify(examenTipoExamenMapper).copiar(dto, entidad);
        verify(examenTipoExamenService).actualizarSi(any(), any(), any());
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        ExamenTipoExamen entidad = new ExamenTipoExamen(id);
        when(examenTipoExamenService.findById(id)).thenReturn(entidad);

        Response respuesta = examenTipoExamenResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(examenTipoExamenService).eliminar(entidad);
    }
}
