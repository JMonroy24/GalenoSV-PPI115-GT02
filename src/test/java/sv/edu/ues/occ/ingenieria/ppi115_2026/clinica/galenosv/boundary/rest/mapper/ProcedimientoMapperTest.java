package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

import static org.junit.jupiter.api.Assertions.*;

class ProcedimientoMapperTest {

    private final ProcedimientoMapper mapper = new ProcedimientoMapper();

    @Test
    void idaYVueltaConservaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        Procedimiento entidad = new Procedimiento(id);
        entidad.setNombre("Consulta general");
        entidad.setActivo(Boolean.TRUE);
        entidad.setObservaciones("Procedimiento de prueba");

        ProcedimientoDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals("Consulta general", dto.nombre());
        assertEquals(Boolean.TRUE, dto.activo());
        assertEquals("Procedimiento de prueba", dto.observaciones());

        Procedimiento vuelta = mapper.toEntity(dto);
        assertEquals(id, vuelta.getIdProcedimiento());
        assertEquals(dto.nombre(), vuelta.getNombre());
        assertEquals(dto.activo(), vuelta.getActivo());
        assertEquals(dto.observaciones(), vuelta.getObservaciones());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        Procedimiento destino = new Procedimiento(idDestino);
        ProcedimientoDTO dto = new ProcedimientoDTO(UUID.randomUUID(), "Nuevo nombre", Boolean.FALSE, "Nuevas observaciones");

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdProcedimiento());
        assertEquals("Nuevo nombre", destino.getNombre());
        assertEquals(Boolean.FALSE, destino.getActivo());
        assertEquals("Nuevas observaciones", destino.getObservaciones());
    }
}
