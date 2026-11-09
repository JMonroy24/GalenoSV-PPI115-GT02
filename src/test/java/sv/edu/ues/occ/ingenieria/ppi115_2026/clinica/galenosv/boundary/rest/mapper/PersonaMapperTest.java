package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.PersonaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

import static org.junit.jupiter.api.Assertions.*;

class PersonaMapperTest {

    private final PersonaMapper mapper = new PersonaMapper();

    @Test
    void idaYVueltaConservaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        Date nacimiento = new Date(946684800000L);
        Date creacion = new Date(1700000000000L);
        Persona entidad = new Persona(id);
        entidad.setNombres("Ana");
        entidad.setApellidos("Pérez");
        entidad.setFechaNacimiento(nacimiento);
        entidad.setFechaCreacion(creacion);

        PersonaDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals("Ana", dto.nombres());
        assertEquals("Pérez", dto.apellidos());
        assertEquals(nacimiento, dto.fechaNacimiento());
        assertEquals(creacion, dto.fechaCreacion());

        Persona vuelta = mapper.toEntity(dto);
        assertEquals(id, vuelta.getIdPersona());
        assertEquals(dto.nombres(), vuelta.getNombres());
        assertEquals(dto.apellidos(), vuelta.getApellidos());
        assertEquals(dto.fechaNacimiento(), vuelta.getFechaNacimiento());
        assertNull(vuelta.getFechaCreacion());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        Persona destino = new Persona(idDestino);
        Date fechaOriginal = new Date(1600000000000L);
        destino.setFechaCreacion(fechaOriginal);
        PersonaDTO dto = new PersonaDTO(UUID.randomUUID(), "Luis", "Gómez", null, new Date(1800000000000L));

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdPersona());
        assertEquals(fechaOriginal, destino.getFechaCreacion());
        assertEquals("Luis", destino.getNombres());
        assertEquals("Gómez", destino.getApellidos());
    }
}
