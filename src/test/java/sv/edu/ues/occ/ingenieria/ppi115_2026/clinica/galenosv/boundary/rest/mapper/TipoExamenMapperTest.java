package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;

class TipoExamenMapperTest {

    private final TipoExamenMapper mapper = new TipoExamenMapper();

    @Test
    void idaYVueltaConservaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        TipoExamen entidad = new TipoExamen(id);
        entidad.setNombre("Hemograma");
        entidad.setActivo(Boolean.TRUE);
        entidad.setObservaciones("Observación de prueba");

        TipoExamenDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals("Hemograma", dto.nombre());
        assertEquals(Boolean.TRUE, dto.activo());
        assertEquals("Observación de prueba", dto.observaciones());

        TipoExamen vuelta = mapper.toEntity(dto);
        assertEquals(id, vuelta.getIdTipoExamen());
        assertEquals(dto.nombre(), vuelta.getNombre());
        assertEquals(dto.activo(), vuelta.getActivo());
        assertEquals(dto.observaciones(), vuelta.getObservaciones());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        TipoExamen destino = new TipoExamen(idDestino);
        TipoExamenDTO dto = new TipoExamenDTO(UUID.randomUUID(), "Química sanguínea", Boolean.FALSE, "Nuevas observaciones");

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdTipoExamen());
        assertEquals("Química sanguínea", destino.getNombre());
        assertEquals(Boolean.FALSE, destino.getActivo());
        assertEquals("Nuevas observaciones", destino.getObservaciones());
    }
}
