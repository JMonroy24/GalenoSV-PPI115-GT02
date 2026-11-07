package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.DocumentoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoDocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentoMapperTest {

    @Mock
    PersonaService personaService;

    @Mock
    TipoDocumentoService tipoDocumentoService;

    @InjectMocks
    DocumentoMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelaciones() {
        UUID id = UUID.randomUUID();
        Persona persona = new Persona(UUID.randomUUID());
        TipoDocumento tipo = new TipoDocumento(UUID.randomUUID());
        Documento entidad = new Documento(id);
        entidad.setValor("01234567-8");
        entidad.setIdPersona(persona);
        entidad.setIdTipoDocumento(tipo);

        DocumentoDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals("01234567-8", dto.valor());
        assertEquals(persona.getIdPersona(), dto.idPersona());
        assertEquals(tipo.getIdTipoDocumento(), dto.idTipoDocumento());

        when(personaService.findById(persona.getIdPersona())).thenReturn(persona);
        when(tipoDocumentoService.findById(tipo.getIdTipoDocumento())).thenReturn(tipo);
        Documento vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdDocumento());
        assertEquals(dto.valor(), vuelta.getValor());
        assertSame(persona, vuelta.getIdPersona());
        assertSame(tipo, vuelta.getIdTipoDocumento());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        Documento destino = new Documento(idDestino);
        Persona persona = new Persona(UUID.randomUUID());
        TipoDocumento tipo = new TipoDocumento(UUID.randomUUID());
        DocumentoDTO dto = new DocumentoDTO(UUID.randomUUID(), "ABC", persona.getIdPersona(), tipo.getIdTipoDocumento());
        when(personaService.findById(persona.getIdPersona())).thenReturn(persona);
        when(tipoDocumentoService.findById(tipo.getIdTipoDocumento())).thenReturn(tipo);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdDocumento());
        assertEquals("ABC", destino.getValor());
        assertSame(persona, destino.getIdPersona());
        assertSame(tipo, destino.getIdTipoDocumento());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        UUID idPersona = UUID.randomUUID();
        DocumentoDTO dto = new DocumentoDTO(null, "ABC", idPersona, UUID.randomUUID());
        when(personaService.findById(idPersona)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}
