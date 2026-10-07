package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstadoPasoTest {

    @Test
    void testTransicionValida() {
        assertTrue(EstadoPaso.transicionValida(null, EstadoPaso.PENDIENTE));
        assertFalse(EstadoPaso.transicionValida(null, EstadoPaso.EN_CURSO));

        assertTrue(EstadoPaso.transicionValida(EstadoPaso.PENDIENTE, EstadoPaso.EN_CURSO));
        assertFalse(EstadoPaso.transicionValida(EstadoPaso.PENDIENTE, EstadoPaso.COMPLETADO));
        assertFalse(EstadoPaso.transicionValida(EstadoPaso.PENDIENTE, EstadoPaso.PENDIENTE));

        assertTrue(EstadoPaso.transicionValida(EstadoPaso.EN_CURSO, EstadoPaso.COMPLETADO));
        assertFalse(EstadoPaso.transicionValida(EstadoPaso.EN_CURSO, EstadoPaso.PENDIENTE));

        assertFalse(EstadoPaso.transicionValida(EstadoPaso.COMPLETADO, EstadoPaso.EN_CURSO));
    }

    @Test
    void testFromString() {
        assertNull(EstadoPaso.fromString(null));
        assertNull(EstadoPaso.fromString("   "));

        assertEquals(EstadoPaso.PENDIENTE, EstadoPaso.fromString("PENDIENTE"));
        assertEquals(EstadoPaso.EN_CURSO, EstadoPaso.fromString("en_curso"));
        
        assertThrows(IllegalArgumentException.class, () -> EstadoPaso.fromString("INVALIDO"));
    }
}
