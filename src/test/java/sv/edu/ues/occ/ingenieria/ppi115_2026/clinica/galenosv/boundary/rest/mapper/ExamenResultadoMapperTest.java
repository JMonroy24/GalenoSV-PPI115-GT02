package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenResultadoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenResultadoMapperTest {

    @Mock
    OrdenExamenService ordenExamenService;

    @InjectMocks
    ExamenResultadoMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelacion() {
        UUID id = UUID.randomUUID();
        Date fecha = new Date(1700000000000L);
        OrdenExamen ordenExamen = new OrdenExamen(UUID.randomUUID());
        ExamenResultado entidad = new ExamenResultado(id);
        entidad.setFechaCreacion(fecha);
        entidad.setResultado("5.2");
        entidad.setInterpretacion("Normal");
        entidad.setRutaAtestado("/resultados/1.pdf");
        entidad.setIdOrdenExamen(ordenExamen);

        ExamenResultadoDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals(fecha, dto.fechaCreacion());
        assertEquals("5.2", dto.resultado());
        assertEquals("Normal", dto.interpretacion());
        assertEquals("/resultados/1.pdf", dto.rutaAtestado());
        assertEquals(ordenExamen.getIdOrdenExamen(), dto.idOrdenExamen());

        when(ordenExamenService.findById(ordenExamen.getIdOrdenExamen())).thenReturn(ordenExamen);
        ExamenResultado vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdExamenResultado());
        assertEquals(dto.fechaCreacion(), vuelta.getFechaCreacion());
        assertEquals(dto.resultado(), vuelta.getResultado());
        assertEquals(dto.interpretacion(), vuelta.getInterpretacion());
        assertEquals(dto.rutaAtestado(), vuelta.getRutaAtestado());
        assertSame(ordenExamen, vuelta.getIdOrdenExamen());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        ExamenResultado destino = new ExamenResultado(idDestino);
        OrdenExamen ordenExamen = new OrdenExamen(UUID.randomUUID());
        ExamenResultadoDTO dto = new ExamenResultadoDTO(UUID.randomUUID(), null,
                "Nuevo resultado", "Nueva interpretación", null, ordenExamen.getIdOrdenExamen());
        when(ordenExamenService.findById(ordenExamen.getIdOrdenExamen())).thenReturn(ordenExamen);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdExamenResultado());
        assertEquals("Nuevo resultado", destino.getResultado());
        assertEquals("Nueva interpretación", destino.getInterpretacion());
        assertSame(ordenExamen, destino.getIdOrdenExamen());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        UUID idOrdenExamen = UUID.randomUUID();
        ExamenResultadoDTO dto = new ExamenResultadoDTO(null, null, "r", "i", null, idOrdenExamen);
        when(ordenExamenService.findById(idOrdenExamen)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}
