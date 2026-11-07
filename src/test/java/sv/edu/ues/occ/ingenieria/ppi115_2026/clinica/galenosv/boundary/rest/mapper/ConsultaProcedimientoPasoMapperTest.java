package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaProcedimientoPasoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoPasoMapperTest {

    @Mock
    ConsultaProcedimientoService consultaProcedimientoService;

    @Mock
    PersonaRolService personaRolService;

    @InjectMocks
    ConsultaProcedimientoPasoMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelaciones() {
        UUID id = UUID.randomUUID();
        Date inicio = new Date(1700000000000L);
        Date fin = new Date(1700003600000L);
        ConsultaProcedimiento consultaProcedimiento = new ConsultaProcedimiento(UUID.randomUUID());
        PersonaRol personaRol = new PersonaRol(UUID.randomUUID());
        ConsultaProcedimientoPaso entidad = new ConsultaProcedimientoPaso(id);
        entidad.setFechaInicio(inicio);
        entidad.setFechaFin(fin);
        entidad.setEstado("COMPLETADO");
        entidad.setIdConsultaProcedimiento(consultaProcedimiento);
        entidad.setIdPersonaRol(personaRol);

        ConsultaProcedimientoPasoDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals(inicio, dto.fechaInicio());
        assertEquals(fin, dto.fechaFin());
        assertEquals("COMPLETADO", dto.estado());
        assertEquals(consultaProcedimiento.getIdConsultaProcedimiento(), dto.idConsultaProcedimiento());
        assertEquals(personaRol.getIdPersonaRol(), dto.idPersonaRol());

        when(consultaProcedimientoService.findById(consultaProcedimiento.getIdConsultaProcedimiento()))
                .thenReturn(consultaProcedimiento);
        when(personaRolService.findById(personaRol.getIdPersonaRol())).thenReturn(personaRol);
        ConsultaProcedimientoPaso vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdConsultaProcedimientoPaso());
        assertEquals(dto.fechaInicio(), vuelta.getFechaInicio());
        assertEquals(dto.fechaFin(), vuelta.getFechaFin());
        assertEquals(dto.estado(), vuelta.getEstado());
        assertSame(consultaProcedimiento, vuelta.getIdConsultaProcedimiento());
        assertSame(personaRol, vuelta.getIdPersonaRol());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        ConsultaProcedimientoPaso destino = new ConsultaProcedimientoPaso(idDestino);
        ConsultaProcedimiento consultaProcedimiento = new ConsultaProcedimiento(UUID.randomUUID());
        PersonaRol personaRol = new PersonaRol(UUID.randomUUID());
        ConsultaProcedimientoPasoDTO dto = new ConsultaProcedimientoPasoDTO(UUID.randomUUID(), null, null,
                "EN_PROCESO", consultaProcedimiento.getIdConsultaProcedimiento(), personaRol.getIdPersonaRol());
        when(consultaProcedimientoService.findById(consultaProcedimiento.getIdConsultaProcedimiento()))
                .thenReturn(consultaProcedimiento);
        when(personaRolService.findById(personaRol.getIdPersonaRol())).thenReturn(personaRol);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdConsultaProcedimientoPaso());
        assertEquals("EN_PROCESO", destino.getEstado());
        assertSame(consultaProcedimiento, destino.getIdConsultaProcedimiento());
        assertSame(personaRol, destino.getIdPersonaRol());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        ConsultaProcedimiento consultaProcedimiento = new ConsultaProcedimiento(UUID.randomUUID());
        UUID idPersonaRol = UUID.randomUUID();
        ConsultaProcedimientoPasoDTO dto = new ConsultaProcedimientoPasoDTO(null, null, null, "COMPLETADO",
                consultaProcedimiento.getIdConsultaProcedimiento(), idPersonaRol);
        when(consultaProcedimientoService.findById(consultaProcedimiento.getIdConsultaProcedimiento()))
                .thenReturn(consultaProcedimiento);
        when(personaRolService.findById(idPersonaRol)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}
