package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.ws.rs.BadRequestException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/** Resuelve el destino del QR sin efectuar conexiones al frontend. */
public final class ConsultaPublicaUrl {
    private ConsultaPublicaUrl() { }

    public static String resolver(UUID id, String frontendUrl, String origin) {
        return resolver(id, frontendUrl, origin, System.getenv("GALENOSV_FRONTEND_URL"));
    }

    static String resolver(UUID id, String frontendUrl, String origin, String entorno) {
        String base;
        if (frontendUrl != null) {
            base = validar(frontendUrl);
            if (base == null) {
                throw new BadRequestException("frontendUrl debe ser una URL HTTP(S) absoluta sin credenciales, query ni fragmento.");
            }
        } else {
            base = validar(origin);
            if (base == null) base = validar(entorno);
            if (base == null) base = "http://localhost:3000";
        }
        return base + "/#consulta/" + id;
    }

    private static String validar(String valor) {
        if (valor == null || valor.isBlank() || valor.length() > 1024) return null;
        try {
            URI uri = new URI(valor);
            if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null || uri.getRawUserInfo() != null
                    || uri.getRawQuery() != null || uri.getRawFragment() != null
                    || uri.getPort() < -1 || uri.getPort() == 0 || uri.getPort() > 65535
                    || valor.matches("(?i).*%(?:0[0-9a-f]|1[0-9a-f]|7f).*")) return null;
            String base = uri.toASCIIString().replaceAll("/+$", "");
            // La codificación de una subruta Unicode puede multiplicar su tamaño en el QR.
            return base.length() <= 1024 ? base : null;
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
