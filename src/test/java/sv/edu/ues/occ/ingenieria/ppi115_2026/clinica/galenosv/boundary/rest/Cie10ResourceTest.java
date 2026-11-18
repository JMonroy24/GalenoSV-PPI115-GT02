package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.ws.rs.BadRequestException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.Cie10DTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.Cie10Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Cie10ResourceTest {

    @Mock
    Cie10Service cie10Service;

    @InjectMocks
    Cie10Resource cie10Resource;

    @Test
    void listarAplicaFiltroYPaginacion() {
        List<Cie10DTO> catalogo = List.of(
                new Cie10DTO("A00", "Cólera"),
                new Cie10DTO("E11", "Diabetes"),
                new Cie10DTO("I10", "Hipertensión"));
        when(cie10Service.buscar("diabetes")).thenReturn(catalogo);

        PageResponse<Cie10DTO> respuesta = cie10Resource.listar("diabetes", 1, 1);

        assertEquals(3L, respuesta.total());
        assertEquals(1, respuesta.first());
        assertEquals(1, respuesta.max());
        assertEquals(1, respuesta.items().size());
        assertEquals("E11", respuesta.items().get(0).codigo());
    }

    @Test
    void listarValidaParametros() {
        assertThrows(BadRequestException.class, () -> cie10Resource.listar(null, -1, 20));
        assertThrows(BadRequestException.class, () -> cie10Resource.listar(null, 0, 0));
        verifyNoInteractions(cie10Service);
    }
}
