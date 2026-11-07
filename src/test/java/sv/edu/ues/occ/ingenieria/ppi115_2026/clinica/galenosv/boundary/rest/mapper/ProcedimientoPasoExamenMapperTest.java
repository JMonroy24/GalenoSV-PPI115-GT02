package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoExamenMapperTest {

    @Mock
    ExamenService examenService;

    @Mock
    ProcedimientoPasoService procedimientoPasoService;

    @InjectMocks
    ProcedimientoPasoExamenMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelaciones() {
        UUID id = UUID.randomUUID();
        Date fecha = new Date(1700000000000L);
        Examen examen = new Examen(UUID.randomUUID());
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        ProcedimientoPasoExamen entidad = new ProcedimientoPasoExamen(id);
        entidad.setFechaCreacion(fecha);
        entidad.setActivo(Boolean.TRUE);
        entidad.setObservaciones("Asignación");
        entidad.setIdExamen(examen);
        entidad.setIdProcedimientoPaso(paso);

        ProcedimientoPasoExamenDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals(fecha, dto.fechaCreacion());
        assertEquals(Boolean.TRUE, dto.activo());
        assertEquals("Asignación", dto.observaciones());
        assertEquals(examen.getIdExamen(), dto.idExamen());
        assertEquals(paso.getIdProcedimientoPaso(), dto.idProcedimientoPaso());

        when(examenService.findById(examen.getIdExamen())).thenReturn(examen);
        when(procedimientoPasoService.findById(paso.getIdProcedimientoPaso())).thenReturn(paso);
        ProcedimientoPasoExamen vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdProcedimientoPasoExamen());
        assertEquals(dto.fechaCreacion(), vuelta.getFechaCreacion());
        assertEquals(dto.activo(), vuelta.getActivo());
        assertEquals(dto.observaciones(), vuelta.getObservaciones());
        assertSame(examen, vuelta.getIdExamen());
        assertSame(paso, vuelta.getIdProcedimientoPaso());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        ProcedimientoPasoExamen destino = new ProcedimientoPasoExamen(idDestino);
        Examen examen = new Examen(UUID.randomUUID());
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        ProcedimientoPasoExamenDTO dto = new ProcedimientoPasoExamenDTO(UUID.randomUUID(), null,
                Boolean.FALSE, "Nuevas observaciones", examen.getIdExamen(), paso.getIdProcedimientoPaso());
        when(examenService.findById(examen.getIdExamen())).thenReturn(examen);
        when(procedimientoPasoService.findById(paso.getIdProcedimientoPaso())).thenReturn(paso);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdProcedimientoPasoExamen());
        assertEquals(Boolean.FALSE, destino.getActivo());
        assertEquals("Nuevas observaciones", destino.getObservaciones());
        assertSame(examen, destino.getIdExamen());
        assertSame(paso, destino.getIdProcedimientoPaso());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        UUID idPaso = UUID.randomUUID();
        ProcedimientoPasoExamenDTO dto = new ProcedimientoPasoExamenDTO(null, null, null, null, null, idPaso);
        when(procedimientoPasoService.findById(idPaso)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}
