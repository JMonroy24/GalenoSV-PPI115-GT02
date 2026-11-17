package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.HttpMethod;
import java.nio.file.Path;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CorsSecurityConfigTest {

    @Test
    void permiteApiSinBasicNiRedireccionAHttps() throws Exception {
        var document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(Path.of("src/main/webapp/WEB-INF/web.xml").toFile());
        var xpath = XPathFactory.newInstance().newXPath();
        assertEquals("0", xpath.evaluate("count(/web-app/security-constraint)", document));
        assertEquals("0", xpath.evaluate("count(/web-app/login-config)", document));
        assertEquals("0", xpath.evaluate("count(/web-app/error-page[error-code='401'])", document));
    }

    @Test
    void libertyEscuchaEnHttp9080() throws Exception {
        var document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(Path.of("src/main/liberty/config/server.xml").toFile());
        var xpath = XPathFactory.newInstance().newXPath();

        assertEquals("9080", xpath.evaluate("/server/httpEndpoint[@id='defaultHttpEndpoint']/@httpPort", document));
    }

    @Test
    void permiteAccesoAnonimoAOperacionesHeredadasYFiltrosDelLogin() {
        for (Class<?> resource : new Class<?>[] {GenericResource.class, PersonaResource.class,
                PersonaRolResource.class, ConsultaResource.class, TipoDocumentoResource.class,
                TipoExamenResource.class, TipoMedioContactoResource.class, RolResource.class}) {
            assertFalse(resource.isAnnotationPresent(RolesAllowed.class), resource.getName());
            assertFalse(resource.isAnnotationPresent(DenyAll.class), resource.getName());
            var endpoints = Arrays.stream(resource.getMethods())
                    .filter(method -> Arrays.stream(method.getAnnotations())
                            .anyMatch(annotation -> annotation.annotationType().isAnnotationPresent(HttpMethod.class)))
                    .toList();
            assertFalse(endpoints.isEmpty(), resource.getName());
            for (var endpoint : endpoints) {
                assertTrue(endpoint.isAnnotationPresent(PermitAll.class), endpoint.toString());
                assertFalse(endpoint.isAnnotationPresent(RolesAllowed.class), endpoint.toString());
                assertFalse(endpoint.isAnnotationPresent(DenyAll.class), endpoint.toString());
            }
        }
    }
}
