package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.ws.rs.BadRequestException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ConsultaPublicaUrlTest {
    private final UUID id = UUID.randomUUID();

    @Test
    void respetaPrioridadQueryOriginEntornoYLocalhost() {
        assertEquals("https://pares.example/app/#consulta/" + id,
                ConsultaPublicaUrl.resolver(id, "https://pares.example/app///", "http://localhost:4000", "https://env.example"));
        assertEquals("http://localhost:4000/#consulta/" + id,
                ConsultaPublicaUrl.resolver(id, null, "http://localhost:4000", "https://env.example"));
        assertEquals("https://env.example/#consulta/" + id,
                ConsultaPublicaUrl.resolver(id, null, null, "https://env.example/"));
        assertEquals("http://localhost:3000/#consulta/" + id,
                ConsultaPublicaUrl.resolver(id, null, null, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "null", "file:///tmp/app", "https://usuario:secreto@example.com", "https://example.com/#ruta"})
    void originOEntornoInutilizableUsanElSiguienteRespaldo(String valor) {
        assertEquals("https://env.example/#consulta/" + id,
                ConsultaPublicaUrl.resolver(id, null, valor, "https://env.example"));
        assertEquals("http://localhost:3000/#consulta/" + id,
                ConsultaPublicaUrl.resolver(id, null, valor, valor));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "javascript:alert(1)", "data:text/html,test", "ftp://example.com", "//example.com",
            "https:///sin-host", "http://", "https://user:pass@example.com", "https://example.com?redirect=otro",
            "https://example.com/#consulta/otra", "https://example.com:0", "https://example.com:65536",
            "https://example.com\\@evil.example", "https://example.com/%0d%0aLocation:evil", "https://example.com/\n"})
    void rechazaUrlExplicitaInseguraOAmbigua(String url) {
        assertThrows(BadRequestException.class, () -> ConsultaPublicaUrl.resolver(id, url, "https://valida.example", null));
    }

    @Test
    void permiteLoopbackHttpsYSubrutaYLimitaLaLongitudDelQr() {
        assertEquals("http://127.0.0.1:5173/app/#consulta/" + id,
                ConsultaPublicaUrl.resolver(id, "http://127.0.0.1:5173/app", null, null));
        assertEquals("https://[::1]:3000/#consulta/" + id,
                ConsultaPublicaUrl.resolver(id, "https://[::1]:3000/", null, null));
        assertThrows(BadRequestException.class,
                () -> ConsultaPublicaUrl.resolver(id, "https://example.com/" + "a".repeat(1024), null, null));
        assertThrows(BadRequestException.class,
                () -> ConsultaPublicaUrl.resolver(id, "https://example.com/" + "á".repeat(300), null, null));
        assertEquals("https://pares.example/cl%C3%ADnica/#consulta/" + id,
                ConsultaPublicaUrl.resolver(id, "https://pares.example/clínica/", null, null));
    }
}
