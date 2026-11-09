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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaProcedimientoPasoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaProcedimientoPasoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoPasoResourceTest {

    @Mock
    ConsultaProcedimientoPasoService consultaProcedimientoPasoService;

    @Mock
    ConsultaProcedimientoPasoMapper consultaProcedimientoPasoMapper;

    @InjectMocks
    ConsultaProcedimientoPasoResource consultaProcedimientoPasoResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        ConsultaProcedimientoPaso entidad = new ConsultaProcedimientoPaso(UUID.randomUUID());
        ConsultaProcedimientoPasoDTO dto = new ConsultaProcedimientoPasoDTO(entidad.getIdConsultaProcedimientoPaso(),
                null, null, "COMPLETADO", UUID.randomUUID(), UUID.randomUUID());
        when(consultaProcedimientoPasoService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(consultaProcedimientoPasoService.count(null)).thenReturn(1L);
        when(consultaProcedimientoPasoMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<ConsultaProcedimientoPasoDTO> respuesta = consultaProcedimientoPasoResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(consultaProcedimientoPasoService).findRange(0, 100, null);
        verify(consultaProcedimientoPasoService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(consultaProcedimientoPasoService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> consultaProcedimientoPasoResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        ConsultaProcedimientoPasoDTO dto = new ConsultaProcedimientoPasoDTO(null, null, null, "COMPLETADO",
                UUID.randomUUID(), UUID.randomUUID());
        ConsultaProcedimientoPaso entidad = new ConsultaProcedimientoPaso();
        ConsultaProcedimientoPasoDTO creada = new ConsultaProcedimientoPasoDTO(UUID.randomUUID(),
                dto.fechaInicio(), dto.fechaFin(), dto.estado(), dto.idConsultaProcedimiento(), dto.idPersonaRol());
        when(consultaProcedimientoPasoMapper.toEntity(dto)).thenReturn(entidad);
        when(consultaProcedimientoPasoMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = consultaProcedimientoPasoResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(consultaProcedimientoPasoService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        ConsultaProcedimientoPaso entidad = new ConsultaProcedimientoPaso(id);
        ConsultaProcedimientoPasoDTO dto = new ConsultaProcedimientoPasoDTO(null, null, null, "COMPLETADO",
                UUID.randomUUID(), UUID.randomUUID());
        ConsultaProcedimientoPasoDTO esperada = new ConsultaProcedimientoPasoDTO(id,
                dto.fechaInicio(), dto.fechaFin(), dto.estado(), dto.idConsultaProcedimiento(), dto.idPersonaRol());
        when(consultaProcedimientoPasoMapper.toDTO(entidad)).thenReturn(esperada);
        when(consultaProcedimientoPasoService.actualizarSi(any(), any(), any())).thenAnswer(inv -> {
            java.util.function.Predicate<Object> precondicion = inv.getArgument(1);
            java.util.function.Consumer<Object> cambios = inv.getArgument(2);
            if (!precondicion.test(entidad)) {
                throw new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException("El ETag no coincide.");
            }
            cambios.accept(entidad);
            return entidad;
        });

        Response respuesta = consultaProcedimientoPasoResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), dto);

        assertEquals(esperada, respuesta.getEntity());
        assertEquals(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcularFuerte(esperada), respuesta.getHeaderString("ETag"));
        verify(consultaProcedimientoPasoMapper).copiar(dto, entidad);
        verify(consultaProcedimientoPasoService).actualizarSi(any(), any(), any());
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        ConsultaProcedimientoPaso entidad = new ConsultaProcedimientoPaso(id);
        when(consultaProcedimientoPasoService.findById(id)).thenReturn(entidad);

        Response respuesta = consultaProcedimientoPasoResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(consultaProcedimientoPasoService).eliminar(entidad);
    }
}
