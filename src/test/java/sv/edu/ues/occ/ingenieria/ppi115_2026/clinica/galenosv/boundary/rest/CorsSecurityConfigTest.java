package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CorsSecurityConfigTest {

    @Test
    void permiteOptionsSinAutenticacionYConservaRolesYHttpsParaElResto() throws Exception {
        var document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(Path.of("src/main/webapp/WEB-INF/web.xml").toFile());
        var xpath = XPathFactory.newInstance().newXPath();
        String secured = "/web-app/security-constraint[web-resource-collection/web-resource-name='API REST']";
        String preflight = "/web-app/security-constraint[web-resource-collection/web-resource-name='CORS preflight']";

        assertEquals("/api/*", xpath.evaluate(secured + "/web-resource-collection/url-pattern", document));
        assertEquals("OPTIONS", xpath.evaluate(secured + "/web-resource-collection/http-method-omission", document));
        assertEquals("2", xpath.evaluate("count(" + secured + "/auth-constraint/role-name)", document));
        assertEquals("ADMINISTRADOR", xpath.evaluate(secured + "/auth-constraint/role-name[1]", document));
        assertEquals("PERSONAL_CLINICO", xpath.evaluate(secured + "/auth-constraint/role-name[2]", document));
        assertEquals("CONFIDENTIAL", xpath.evaluate(secured + "/user-data-constraint/transport-guarantee", document));
        assertEquals("/api/*", xpath.evaluate(preflight + "/web-resource-collection/url-pattern", document));
        assertEquals("OPTIONS", xpath.evaluate(preflight + "/web-resource-collection/http-method", document));
        assertEquals("0", xpath.evaluate("count(" + preflight + "/auth-constraint)", document));
        assertEquals("0", xpath.evaluate("count(" + preflight + "/user-data-constraint)", document));
        assertEquals("BASIC", xpath.evaluate("/web-app/login-config/auth-method", document));
    }
}
