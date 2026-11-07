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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ClinicaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ClinicaMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ClinicaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicaResourceTest {

    @Mock
    ClinicaService clinicaService;

    @Mock
    ClinicaMapper clinicaMapper;

    @InjectMocks
    ClinicaResource clinicaResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        Clinica entidad = new Clinica(UUID.randomUUID());
        ClinicaDTO dto = new ClinicaDTO(entidad.getIdClinica(), "Clínica", Boolean.TRUE, "GENERAL", null);
        when(clinicaService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(clinicaService.count(null)).thenReturn(1L);
        when(clinicaMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<ClinicaDTO> respuesta = clinicaResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(0, respuesta.first());
        assertEquals(100, respuesta.max());
        verify(clinicaService).findRange(0, 100, null);
        verify(clinicaService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(clinicaService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> clinicaResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        ClinicaDTO dto = new ClinicaDTO(null, "Nueva", Boolean.TRUE, "GENERAL", null);
        Clinica entidad = new Clinica();
        ClinicaDTO creada = new ClinicaDTO(UUID.randomUUID(), "Nueva", Boolean.TRUE, "GENERAL", null);
        when(clinicaMapper.toEntity(dto)).thenReturn(entidad);
        when(clinicaMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = clinicaResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        String location = respuesta.getHeaderString("Location");
        assertNotNull(location);
        assertTrue(location.contains(creada.id().toString()));
        verify(clinicaService).crear(entidad);
    }

    @Test
    void putInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(clinicaService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> clinicaResource.actualizar(id.toString(), null, new ClinicaDTO(null, "x", null, "y", null)));
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        Clinica entidad = new Clinica(id);
        ClinicaDTO dto = new ClinicaDTO(null, "Actualizada", Boolean.TRUE, "GENERAL", null);
        ClinicaDTO esperada = new ClinicaDTO(id, "Actualizada", Boolean.TRUE, "GENERAL", null);
        when(clinicaService.findById(id)).thenReturn(entidad);
        when(clinicaMapper.toDTO(entidad)).thenReturn(esperada);

        ClinicaDTO respuesta = clinicaResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(esperada), dto);

        assertEquals(esperada, respuesta);
        verify(clinicaMapper).copiar(dto, entidad);
        verify(clinicaService).actualizar(entidad);
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        Clinica entidad = new Clinica(id);
        when(clinicaService.findById(id)).thenReturn(entidad);

        Response respuesta = clinicaResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        assertFalse(entidad.getActivo());
        verify(clinicaService).actualizar(entidad);
    }

    @Test
    void getListaConFirstNegativoLanzaBadRequest() {
        assertThrows(jakarta.ws.rs.BadRequestException.class,
                () -> clinicaResource.listar(-1, 20, null));
    }
}
