package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenTipoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenTipoExamenMapperTest {

    @Mock
    ExamenService examenService;

    @Mock
    TipoExamenService tipoExamenService;

    @InjectMocks
    ExamenTipoExamenMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelaciones() {
        UUID id = UUID.randomUUID();
        Date fecha = new Date(1700000000000L);
        Examen examen = new Examen(UUID.randomUUID());
        TipoExamen tipoExamen = new TipoExamen(UUID.randomUUID());
        ExamenTipoExamen entidad = new ExamenTipoExamen(id);
        entidad.setFechaCreacion(fecha);
        entidad.setObservaciones("Asignación");
        entidad.setIdExamen(examen);
        entidad.setIdTipoExamen(tipoExamen);

        ExamenTipoExamenDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals(fecha, dto.fechaCreacion());
        assertEquals("Asignación", dto.observaciones());
        assertEquals(examen.getIdExamen(), dto.idExamen());
        assertEquals(tipoExamen.getIdTipoExamen(), dto.idTipoExamen());

        when(examenService.findById(examen.getIdExamen())).thenReturn(examen);
        when(tipoExamenService.findById(tipoExamen.getIdTipoExamen())).thenReturn(tipoExamen);
        ExamenTipoExamen vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdExamenTipoExamen());
        assertNull(vuelta.getFechaCreacion());
        assertEquals(dto.observaciones(), vuelta.getObservaciones());
        assertSame(examen, vuelta.getIdExamen());
        assertSame(tipoExamen, vuelta.getIdTipoExamen());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        ExamenTipoExamen destino = new ExamenTipoExamen(idDestino);
        Date fechaOriginal = new Date(1600000000000L);
        destino.setFechaCreacion(fechaOriginal);
        Examen examen = new Examen(UUID.randomUUID());
        TipoExamen tipoExamen = new TipoExamen(UUID.randomUUID());
        ExamenTipoExamenDTO dto = new ExamenTipoExamenDTO(UUID.randomUUID(), new Date(1800000000000L),
                "Nuevas observaciones", examen.getIdExamen(), tipoExamen.getIdTipoExamen());
        when(examenService.findById(examen.getIdExamen())).thenReturn(examen);
        when(tipoExamenService.findById(tipoExamen.getIdTipoExamen())).thenReturn(tipoExamen);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdExamenTipoExamen());
        assertEquals(fechaOriginal, destino.getFechaCreacion());
        assertSame(examen, destino.getIdExamen());
        assertSame(tipoExamen, destino.getIdTipoExamen());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        Examen examen = new Examen(UUID.randomUUID());
        UUID idTipoExamen = UUID.randomUUID();
        ExamenTipoExamenDTO dto = new ExamenTipoExamenDTO(null, null, null,
                examen.getIdExamen(), idTipoExamen);
        when(examenService.findById(examen.getIdExamen())).thenReturn(examen);
        when(tipoExamenService.findById(idTipoExamen)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}
