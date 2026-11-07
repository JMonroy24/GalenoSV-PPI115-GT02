package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;

import static org.junit.jupiter.api.Assertions.*;

class ExamenMapperTest {

    private final ExamenMapper mapper = new ExamenMapper();

    @Test
    void idaYVueltaConservaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        Examen entidad = new Examen(id);
        entidad.setNombre("Hemograma completo");
        entidad.setActivo(Boolean.TRUE);
        entidad.setObservaciones("Examen de sangre");

        ExamenDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals("Hemograma completo", dto.nombre());
        assertEquals(Boolean.TRUE, dto.activo());
        assertEquals("Examen de sangre", dto.observaciones());

        Examen vuelta = mapper.toEntity(dto);
        assertEquals(id, vuelta.getIdExamen());
        assertEquals(dto.nombre(), vuelta.getNombre());
        assertEquals(dto.activo(), vuelta.getActivo());
        assertEquals(dto.observaciones(), vuelta.getObservaciones());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        Examen destino = new Examen(idDestino);
        ExamenDTO dto = new ExamenDTO(UUID.randomUUID(), "Perfil lipídico", Boolean.FALSE, "Nuevas observaciones");

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdExamen());
        assertEquals("Perfil lipídico", destino.getNombre());
        assertEquals(Boolean.FALSE, destino.getActivo());
        assertEquals("Nuevas observaciones", destino.getObservaciones());
    }
}
