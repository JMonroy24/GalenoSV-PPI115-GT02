package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoDocumentoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

import static org.junit.jupiter.api.Assertions.*;

class TipoDocumentoMapperTest {

    private final TipoDocumentoMapper mapper = new TipoDocumentoMapper();

    @Test
    void idaYVueltaConservaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        TipoDocumento entidad = new TipoDocumento(id);
        entidad.setNombre("DUI");
        entidad.setIndicaciones("Documento de identidad");
        entidad.setExpresionRegular("^[0-9]{8}-[0-9]$");
        entidad.setActivo(Boolean.TRUE);

        TipoDocumentoDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals("DUI", dto.nombre());
        assertEquals("Documento de identidad", dto.indicaciones());
        assertEquals("^[0-9]{8}-[0-9]$", dto.expresionRegular());
        assertEquals(Boolean.TRUE, dto.activo());

        TipoDocumento vuelta = mapper.toEntity(dto);
        assertEquals(id, vuelta.getIdTipoDocumento());
        assertEquals(dto.nombre(), vuelta.getNombre());
        assertEquals(dto.indicaciones(), vuelta.getIndicaciones());
        assertEquals(dto.expresionRegular(), vuelta.getExpresionRegular());
        assertEquals(dto.activo(), vuelta.getActivo());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        TipoDocumento destino = new TipoDocumento(idDestino);
        TipoDocumentoDTO dto = new TipoDocumentoDTO(UUID.randomUUID(), "Pasaporte", "Indicación", "^[A-Z]+$", Boolean.FALSE);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdTipoDocumento());
        assertEquals("Pasaporte", destino.getNombre());
        assertEquals("Indicación", destino.getIndicaciones());
        assertEquals("^[A-Z]+$", destino.getExpresionRegular());
        assertEquals(Boolean.FALSE, destino.getActivo());
    }
}
