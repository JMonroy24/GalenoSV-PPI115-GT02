package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntidadVersionadaTest {

    @Test
    void testGetVersion() {
        EntidadVersionada ev = new EntidadVersionada() {};
        assertEquals(0L, ev.getVersion());
    }
}
