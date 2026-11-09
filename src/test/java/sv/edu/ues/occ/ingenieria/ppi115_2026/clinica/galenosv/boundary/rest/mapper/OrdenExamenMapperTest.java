package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.OrdenExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenExamenMapperTest {

    @Mock
    ConsultaProcedimientoPasoService consultaProcedimientoPasoService;

    @InjectMocks
    OrdenExamenMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelacion() {
        UUID id = UUID.randomUUID();
        Date fecha = new Date(1700000000000L);
        ConsultaProcedimientoPaso paso = new ConsultaProcedimientoPaso(UUID.randomUUID());
        OrdenExamen entidad = new OrdenExamen(id);
        entidad.setFechaCreacion(fecha);
        entidad.setIndicaciones("Ayuno de 8 horas");
        entidad.setIdConsultaProcedimientoPaso(paso);

        OrdenExamenDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals(fecha, dto.fechaCreacion());
        assertEquals("Ayuno de 8 horas", dto.indicaciones());
        assertEquals(paso.getIdConsultaProcedimientoPaso(), dto.idConsultaProcedimientoPaso());

        when(consultaProcedimientoPasoService.findById(paso.getIdConsultaProcedimientoPaso())).thenReturn(paso);
        OrdenExamen vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdOrdenExamen());
        assertNull(vuelta.getFechaCreacion());
        assertEquals(dto.indicaciones(), vuelta.getIndicaciones());
        assertSame(paso, vuelta.getIdConsultaProcedimientoPaso());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        OrdenExamen destino = new OrdenExamen(idDestino);
        Date fechaOriginal = new Date(1600000000000L);
        destino.setFechaCreacion(fechaOriginal);
        ConsultaProcedimientoPaso paso = new ConsultaProcedimientoPaso(UUID.randomUUID());
        OrdenExamenDTO dto = new OrdenExamenDTO(UUID.randomUUID(), new Date(1800000000000L),
                "Nuevas indicaciones", paso.getIdConsultaProcedimientoPaso());
        when(consultaProcedimientoPasoService.findById(paso.getIdConsultaProcedimientoPaso())).thenReturn(paso);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdOrdenExamen());
        assertEquals(fechaOriginal, destino.getFechaCreacion());
        assertEquals("Nuevas indicaciones", destino.getIndicaciones());
        assertSame(paso, destino.getIdConsultaProcedimientoPaso());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        UUID idPaso = UUID.randomUUID();
        OrdenExamenDTO dto = new OrdenExamenDTO(null, null, "Indicaciones", idPaso);
        when(consultaProcedimientoPasoService.findById(idPaso)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}
