package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.RolDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

import static org.junit.jupiter.api.Assertions.*;

class RolMapperTest {

    private final RolMapper mapper = new RolMapper();

    @Test
    void idaYVueltaConservaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        Rol entidad = new Rol(id);
        entidad.setNombre("Administrador");
        entidad.setActivo(Boolean.TRUE);
        entidad.setObservaciones("Observación de prueba");

        RolDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals("Administrador", dto.nombre());
        assertEquals(Boolean.TRUE, dto.activo());
        assertEquals("Observación de prueba", dto.observaciones());

        Rol vuelta = mapper.toEntity(dto);
        assertEquals(id, vuelta.getIdRol());
        assertEquals(dto.nombre(), vuelta.getNombre());
        assertEquals(dto.activo(), vuelta.getActivo());
        assertEquals(dto.observaciones(), vuelta.getObservaciones());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        Rol destino = new Rol(idDestino);
        RolDTO dto = new RolDTO(UUID.randomUUID(), "Nuevo rol", Boolean.FALSE, "Nuevas observaciones");

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdRol());
        assertEquals("Nuevo rol", destino.getNombre());
        assertEquals(Boolean.FALSE, destino.getActivo());
        assertEquals("Nuevas observaciones", destino.getObservaciones());
    }
}
