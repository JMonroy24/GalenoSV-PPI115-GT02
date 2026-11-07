package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import java.lang.reflect.Array;
import java.lang.reflect.RecordComponent;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Calcula ETags débiles a partir de una serialización JSON canónica del DTO.
 */
public final class ETagUtils {

    private ETagUtils() {
    }

    public static String calcular(Object dto) {
        String json = serializar(dto);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));
            return "W/\"" + HexFormat.of().formatHex(hash) + "\"";
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo calcular el ETag.", e);
        }
    }

    public static boolean coincide(String ifMatch, String etagActual) {
        if (ifMatch == null || etagActual == null) {
            return false;
        }
        String actual = sinDebilidad(etagActual);
        for (String token : ifMatch.split(",")) {
            String valor = token.trim();
            if (valor.isEmpty()) {
                continue;
            }
            if ("*".equals(valor)) {
                return true;
            }
            if (actual.equals(sinDebilidad(valor))) {
                return true;
            }
        }
        return false;
    }

    private static String sinDebilidad(String etag) {
        String valor = etag.trim();
        return valor.startsWith("W/") ? valor.substring(2).trim() : valor;
    }

    private static String serializar(Object valor) {
        if (valor == null) {
            return "null";
        }
        if (valor instanceof CharSequence texto) {
            return comillas(texto.toString());
        }
        if (valor instanceof Boolean || valor instanceof Number) {
            return valor.toString();
        }
        if (valor instanceof UUID || valor instanceof Enum<?>) {
            return comillas(valor.toString());
        }
        if (valor instanceof Date fecha) {
            return comillas(fecha.toInstant().toString());
        }
        if (valor.getClass().isRecord()) {
            StringBuilder json = new StringBuilder("{");
            RecordComponent[] componentes = valor.getClass().getRecordComponents();
            for (int i = 0; i < componentes.length; i++) {
                if (i > 0) {
                    json.append(',');
                }
                RecordComponent componente = componentes[i];
                json.append(comillas(componente.getName())).append(':');
                try {
                    json.append(serializar(componente.getAccessor().invoke(valor)));
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("No se pudo serializar el DTO.", e);
                }
            }
            return json.append('}').toString();
        }
        if (valor.getClass().isArray()) {
            StringBuilder json = new StringBuilder("[");
            int longitud = Array.getLength(valor);
            for (int i = 0; i < longitud; i++) {
                if (i > 0) {
                    json.append(',');
                }
                json.append(serializar(Array.get(valor, i)));
            }
            return json.append(']').toString();
        }
        if (valor instanceof Collection<?> coleccion) {
            StringBuilder json = new StringBuilder("[");
            boolean primero = true;
            for (Object elemento : coleccion) {
                if (!primero) {
                    json.append(',');
                }
                primero = false;
                json.append(serializar(elemento));
            }
            return json.append(']').toString();
        }
        return comillas(valor.toString());
    }

    private static String comillas(String texto) {
        StringBuilder salida = new StringBuilder(texto.length() + 2);
        salida.append('"');
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '"' -> salida.append("\\\"");
                case '\\' -> salida.append("\\\\");
                case '\b' -> salida.append("\\b");
                case '\f' -> salida.append("\\f");
                case '\n' -> salida.append("\\n");
                case '\r' -> salida.append("\\r");
                case '\t' -> salida.append("\\t");
                default -> {
                    if (c < 0x20) {
                        salida.append(String.format("\\u%04x", (int) c));
                    } else {
                        salida.append(c);
                    }
                }
            }
        }
        return salida.append('"').toString();
    }
}
