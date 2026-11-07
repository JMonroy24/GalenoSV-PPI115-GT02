package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaMapperTest {

    @Mock
    PersonaRolService personaRolService;

    @InjectMocks
    ConsultaMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelacion() {
        UUID id = UUID.randomUUID();
        Date inicio = new Date(1700000000000L);
        Date fin = new Date(1700003600000L);
        PersonaRol personaRol = new PersonaRol(UUID.randomUUID());
        Consulta entidad = new Consulta(id);
        entidad.setFechaInicio(inicio);
        entidad.setFechaFin(fin);
        entidad.setReferenciaExterna("REF-001");
        entidad.setObservaciones("Consulta de control");
        entidad.setIdPersonaRol(personaRol);

        ConsultaDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals(inicio, dto.fechaInicio());
        assertEquals(fin, dto.fechaFin());
        assertEquals("REF-001", dto.referenciaExterna());
        assertEquals("Consulta de control", dto.observaciones());
        assertEquals(personaRol.getIdPersonaRol(), dto.idPersonaRol());

        when(personaRolService.findById(personaRol.getIdPersonaRol())).thenReturn(personaRol);
        Consulta vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdConsulta());
        assertEquals(dto.fechaInicio(), vuelta.getFechaInicio());
        assertEquals(dto.fechaFin(), vuelta.getFechaFin());
        assertEquals(dto.referenciaExterna(), vuelta.getReferenciaExterna());
        assertEquals(dto.observaciones(), vuelta.getObservaciones());
        assertSame(personaRol, vuelta.getIdPersonaRol());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        Consulta destino = new Consulta(idDestino);
        PersonaRol personaRol = new PersonaRol(UUID.randomUUID());
        ConsultaDTO dto = new ConsultaDTO(UUID.randomUUID(), null, null,
                "REF-002", "Nuevas observaciones", personaRol.getIdPersonaRol());
        when(personaRolService.findById(personaRol.getIdPersonaRol())).thenReturn(personaRol);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdConsulta());
        assertEquals("REF-002", destino.getReferenciaExterna());
        assertEquals("Nuevas observaciones", destino.getObservaciones());
        assertSame(personaRol, destino.getIdPersonaRol());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        UUID idPersonaRol = UUID.randomUUID();
        ConsultaDTO dto = new ConsultaDTO(null, null, null, null, null, idPersonaRol);
        when(personaRolService.findById(idPersonaRol)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}
