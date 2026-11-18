package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Cie10ServiceTest {

    private final Cie10Service servicio = new Cie10Service();

    @Test
    void catalogoTieneCodesUnicos() {
        assertEquals(200, servicio.findAll().size());
        assertEquals(200, servicio.findAll().stream().map(item -> item.codigo()).distinct().count());
    }

    @Test
    void buscarPorCodigoYDescripcionIgnoraTildesYMayusculas() {
        assertTrue(servicio.buscar("e11").stream().anyMatch(item -> "E11".equals(item.codigo())));
        assertTrue(servicio.buscar("HIPERTENSION").stream().anyMatch(item -> "I10".equals(item.codigo())));
        assertTrue(servicio.buscar("diabetes").stream().anyMatch(item -> "E11".equals(item.codigo())));
    }

    @Test
    void buscarSinFiltroDevuelveCatalogoCompleto() {
        assertEquals(servicio.findAll(), servicio.buscar(null));
        assertEquals(servicio.findAll(), servicio.buscar("  "));
    }
}
