package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoMedioContactoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

import static org.junit.jupiter.api.Assertions.*;

class TipoMedioContactoMapperTest {

    private final TipoMedioContactoMapper mapper = new TipoMedioContactoMapper();

    @Test
    void idaYVueltaConservaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        TipoMedioContacto entidad = new TipoMedioContacto(id);
        entidad.setNombre("Correo electrónico");
        entidad.setIndicaciones("Correo institucional");
        entidad.setExpresionRegular("^[^@]+@[^@]+$");
        entidad.setActivo(Boolean.TRUE);

        TipoMedioContactoDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals("Correo electrónico", dto.nombre());
        assertEquals("Correo institucional", dto.indicaciones());
        assertEquals("^[^@]+@[^@]+$", dto.expresionRegular());
        assertEquals(Boolean.TRUE, dto.activo());

        TipoMedioContacto vuelta = mapper.toEntity(dto);
        assertEquals(id, vuelta.getIdTipoMedioContacto());
        assertEquals(dto.nombre(), vuelta.getNombre());
        assertEquals(dto.indicaciones(), vuelta.getIndicaciones());
        assertEquals(dto.expresionRegular(), vuelta.getExpresionRegular());
        assertEquals(dto.activo(), vuelta.getActivo());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        TipoMedioContacto destino = new TipoMedioContacto(idDestino);
        TipoMedioContactoDTO dto = new TipoMedioContactoDTO(UUID.randomUUID(), "Teléfono", "Teléfono fijo", "^[0-9]+$", Boolean.FALSE);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdTipoMedioContacto());
        assertEquals("Teléfono", destino.getNombre());
        assertEquals("Teléfono fijo", destino.getIndicaciones());
        assertEquals("^[0-9]+$", destino.getExpresionRegular());
        assertEquals(Boolean.FALSE, destino.getActivo());
    }
}
