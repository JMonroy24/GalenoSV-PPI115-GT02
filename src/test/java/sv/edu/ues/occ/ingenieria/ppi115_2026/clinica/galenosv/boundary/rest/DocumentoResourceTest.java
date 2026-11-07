package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.DocumentoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.DocumentoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentoResourceTest {

    @Mock
    DocumentoService documentoService;

    @Mock
    DocumentoMapper documentoMapper;

    @InjectMocks
    DocumentoResource documentoResource;

    @Test
    void getListaDevuelveTotalYLimitaMaxA100() {
        Documento entidad = new Documento(UUID.randomUUID());
        DocumentoDTO dto = new DocumentoDTO(entidad.getIdDocumento(), "ABC", null, null);
        when(documentoService.findRange(0, 100, null)).thenReturn(List.of(entidad));
        when(documentoService.count(null)).thenReturn(1L);
        when(documentoMapper.toDTO(entidad)).thenReturn(dto);

        PageResponse<DocumentoDTO> respuesta = documentoResource.listar(0, 1000, null);

        assertEquals(1, respuesta.items().size());
        assertEquals(1L, respuesta.total());
        assertEquals(100, respuesta.max());
        verify(documentoService).findRange(0, 100, null);
        verify(documentoService).count(null);
    }

    @Test
    void getPorIdInexistenteLanzaNotFound() {
        UUID id = UUID.randomUUID();
        when(documentoService.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> documentoResource.obtener(id.toString()));
    }

    @Test
    void postDevuelve201YHeaderLocation() {
        DocumentoDTO dto = new DocumentoDTO(null, "ABC", UUID.randomUUID(), UUID.randomUUID());
        Documento entidad = new Documento();
        DocumentoDTO creada = new DocumentoDTO(UUID.randomUUID(), "ABC", dto.idPersona(), dto.idTipoDocumento());
        when(documentoMapper.toEntity(dto)).thenReturn(entidad);
        when(documentoMapper.toDTO(entidad)).thenReturn(creada);

        Response respuesta = documentoResource.crear(dto);

        assertEquals(201, respuesta.getStatus());
        assertNotNull(respuesta.getHeaderString("Location"));
        assertTrue(respuesta.getHeaderString("Location").contains(creada.id().toString()));
        verify(documentoService).crear(entidad);
    }

    @Test
    void putExistenteCopiaYActualiza() {
        UUID id = UUID.randomUUID();
        Documento entidad = new Documento(id);
        DocumentoDTO dto = new DocumentoDTO(null, "XYZ", UUID.randomUUID(), UUID.randomUUID());
        DocumentoDTO esperada = new DocumentoDTO(id, "XYZ", dto.idPersona(), dto.idTipoDocumento());
        when(documentoService.findById(id)).thenReturn(entidad);
        when(documentoMapper.toDTO(entidad)).thenReturn(esperada);

        DocumentoDTO respuesta = documentoResource.actualizar(id.toString(), sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(esperada), dto);

        assertEquals(esperada, respuesta);
        verify(documentoMapper).copiar(dto, entidad);
        verify(documentoService).actualizar(entidad);
    }

    @Test
    void deleteDevuelve204() {
        UUID id = UUID.randomUUID();
        Documento entidad = new Documento(id);
        when(documentoService.findById(id)).thenReturn(entidad);

        Response respuesta = documentoResource.eliminar(id.toString());

        assertEquals(204, respuesta.getStatus());
        verify(documentoService).eliminar(entidad);
    }
}
