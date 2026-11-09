package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ETagUtilsTest {

    private static final UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    record DTO(UUID id, String nombre, Boolean activo) {
    }

    @Test
    void mismoContenidoGeneraMismoEtag() {
        DTO primero = new DTO(ID, "Consulta", Boolean.TRUE);
        DTO segundo = new DTO(ID, "Consulta", Boolean.TRUE);

        assertEquals(ETagUtils.calcular(primero), ETagUtils.calcular(segundo));
    }

    @Test
    void contenidoDistintoGeneraEtagDistinto() {
        DTO original = new DTO(ID, "Consulta", Boolean.TRUE);
        DTO modificado = new DTO(ID, "Consulta modificada", Boolean.TRUE);

        assertNotEquals(ETagUtils.calcular(original), ETagUtils.calcular(modificado));
    }

    @Test
    void etagEsDebil() {
        String etag = ETagUtils.calcular(new DTO(ID, "Consulta", Boolean.TRUE));

        assertTrue(etag.startsWith("W/\""));
        assertTrue(etag.endsWith("\""));
    }

    @Test
    void etagFuerteNoTienePrefijoDebil() {
        String etag = ETagUtils.calcularFuerte(new DTO(ID, "Consulta", Boolean.TRUE));

        assertTrue(etag.startsWith("\""));
        assertTrue(etag.endsWith("\""));
        assertFalse(etag.startsWith("W/"));
    }

    @Test
    void coincideAceptaEtagFuerteListaYComodin() {
        String etag = ETagUtils.calcular(new DTO(ID, "Consulta", Boolean.TRUE));
        String fuerte = ETagUtils.calcularFuerte(new DTO(ID, "Consulta", Boolean.TRUE));

        assertTrue(ETagUtils.coincide(fuerte, etag));
        assertTrue(ETagUtils.coincide("W/\"otro\", " + fuerte, etag));
        assertTrue(ETagUtils.coincide("*", etag));
    }

    @Test
    void noCoincideConEtagDebilDiferenteONulo() {
        String etag = ETagUtils.calcular(new DTO(ID, "Consulta", Boolean.TRUE));

        assertFalse(ETagUtils.coincide(etag, etag));
        assertFalse(ETagUtils.coincide("W/\"distinto\"", etag));
        assertFalse(ETagUtils.coincide(null, etag));
        assertFalse(ETagUtils.coincide(etag, null));
    }
}
