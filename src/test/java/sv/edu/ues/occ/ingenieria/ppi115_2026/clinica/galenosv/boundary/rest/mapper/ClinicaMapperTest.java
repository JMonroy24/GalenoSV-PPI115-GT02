package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ClinicaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;

import static org.junit.jupiter.api.Assertions.*;

class ClinicaMapperTest {

    private final ClinicaMapper mapper = new ClinicaMapper();

    @Test
    void idaYVueltaConservaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        Clinica entidad = new Clinica(id);
        entidad.setNombre("Clínica Central");
        entidad.setActivo(Boolean.TRUE);
        entidad.setTipo("GENERAL");
        entidad.setComentarios("Comentario de prueba");

        ClinicaDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals("Clínica Central", dto.nombre());
        assertEquals(Boolean.TRUE, dto.activo());
        assertEquals("GENERAL", dto.tipo());
        assertEquals("Comentario de prueba", dto.comentarios());

        Clinica vuelta = mapper.toEntity(dto);
        assertEquals(id, vuelta.getIdClinica());
        assertEquals(dto.nombre(), vuelta.getNombre());
        assertEquals(dto.activo(), vuelta.getActivo());
        assertEquals(dto.tipo(), vuelta.getTipo());
        assertEquals(dto.comentarios(), vuelta.getComentarios());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        UUID idDto = UUID.randomUUID();
        Clinica destino = new Clinica(idDestino);
        ClinicaDTO dto = new ClinicaDTO(idDto, "Nuevo nombre", Boolean.FALSE, "ESPECIALIZADA", "Nuevos comentarios");

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdClinica());
        assertEquals("Nuevo nombre", destino.getNombre());
        assertEquals(Boolean.FALSE, destino.getActivo());
        assertEquals("ESPECIALIZADA", destino.getTipo());
        assertEquals("Nuevos comentarios", destino.getComentarios());
    }
}
