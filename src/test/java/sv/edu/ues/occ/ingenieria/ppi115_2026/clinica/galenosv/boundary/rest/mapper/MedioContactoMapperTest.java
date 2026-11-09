package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.MedioContactoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoMedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedioContactoMapperTest {

    @Mock
    PersonaService personaService;

    @Mock
    TipoMedioContactoService tipoMedioContactoService;

    @InjectMocks
    MedioContactoMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelaciones() {
        UUID id = UUID.randomUUID();
        Date fecha = new Date(1700000000000L);
        Persona persona = new Persona(UUID.randomUUID());
        TipoMedioContacto tipo = new TipoMedioContacto(UUID.randomUUID());
        MedioContacto entidad = new MedioContacto(id);
        entidad.setValor("correo@dominio.com");
        entidad.setFechaCreacion(fecha);
        entidad.setIdPersona(persona);
        entidad.setIdTipoMedioContacto(tipo);

        MedioContactoDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals("correo@dominio.com", dto.valor());
        assertEquals(fecha, dto.fechaCreacion());
        assertEquals(persona.getIdPersona(), dto.idPersona());
        assertEquals(tipo.getIdTipoMedioContacto(), dto.idTipoMedioContacto());

        when(personaService.findById(persona.getIdPersona())).thenReturn(persona);
        when(tipoMedioContactoService.findById(tipo.getIdTipoMedioContacto())).thenReturn(tipo);
        MedioContacto vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdMedioContacto());
        assertEquals(dto.valor(), vuelta.getValor());
        assertNull(vuelta.getFechaCreacion());
        assertSame(persona, vuelta.getIdPersona());
        assertSame(tipo, vuelta.getIdTipoMedioContacto());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        MedioContacto destino = new MedioContacto(idDestino);
        Date fechaOriginal = new Date(1600000000000L);
        destino.setFechaCreacion(fechaOriginal);
        Persona persona = new Persona(UUID.randomUUID());
        TipoMedioContacto tipo = new TipoMedioContacto(UUID.randomUUID());
        MedioContactoDTO dto = new MedioContactoDTO(UUID.randomUUID(), "7777-7777", new Date(1800000000000L),
                persona.getIdPersona(), tipo.getIdTipoMedioContacto());
        when(personaService.findById(persona.getIdPersona())).thenReturn(persona);
        when(tipoMedioContactoService.findById(tipo.getIdTipoMedioContacto())).thenReturn(tipo);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdMedioContacto());
        assertEquals(fechaOriginal, destino.getFechaCreacion());
        assertEquals("7777-7777", destino.getValor());
        assertSame(persona, destino.getIdPersona());
        assertSame(tipo, destino.getIdTipoMedioContacto());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        UUID idPersona = UUID.randomUUID();
        MedioContactoDTO dto = new MedioContactoDTO(null, "7777-7777", null, idPersona, UUID.randomUUID());
        when(personaService.findById(idPersona)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}
