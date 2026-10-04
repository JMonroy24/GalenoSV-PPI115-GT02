package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EsquemaProfesorTest {
    @Test
    void todosLosCamposJpaExistenEnElSqlOriginalSinVersion() throws Exception {
        String sql;
        try (var recurso = getClass().getResourceAsStream("/db/clinica_ppi115_2026_08_20.sql")) {
            sql = new String(java.util.Objects.requireNonNull(recurso).readAllBytes(), StandardCharsets.UTF_8);
        }
        Map<String, Set<String>> tablas = new HashMap<>();
        var matcher = Pattern.compile("CREATE TABLE public\\.(\\w+) \\((.*?)\\n\\);", Pattern.DOTALL).matcher(sql);
        while (matcher.find()) {
            Set<String> columnas = new HashSet<>(); var campos = Pattern.compile("^    (\\w+) ", Pattern.MULTILINE).matcher(matcher.group(2));
            while (campos.find()) columnas.add(campos.group(1)); tablas.put(matcher.group(1), columnas);
        }
        String persistence = Files.readString(Path.of("src/main/resources/META-INF/persistence.xml"));
        var clases = Pattern.compile("<class>(.*?)</class>").matcher(persistence); int revisadas = 0;
        while (clases.find()) {
            Class<?> tipo = Class.forName(clases.group(1)); String tabla = tipo.getAnnotation(Table.class).name();
            assertNotNull(tablas.get(tabla), tabla); revisadas++;
            for (Class<?> actual = tipo; actual != Object.class; actual = actual.getSuperclass()) {
                for (var campo : actual.getDeclaredFields()) {
                    assertNull(campo.getAnnotation(Version.class), tipo.getSimpleName() + "." + campo.getName());
                    var col = campo.getAnnotation(Column.class); var fk = campo.getAnnotation(JoinColumn.class);
                    if (col != null) assertTrue(tablas.get(tabla).contains(col.name()), tabla + "." + col.name());
                    if (fk != null) assertTrue(tablas.get(tabla).contains(fk.name()), tabla + "." + fk.name());
                }
            }
        }
        assertEquals(20, revisadas); assertTrue(persistence.contains("schema-generation.database.action\" value=\"none"));
        var nacimiento = Persona.class.getDeclaredField("fechaNacimiento");
        assertEquals(java.util.Date.class, nacimiento.getType()); assertEquals(TemporalType.TIMESTAMP, nacimiento.getAnnotation(Temporal.class).value());
    }

    @Test
    void migracionesNoSeEmpaquetanConLaAplicacion() {
        assertNull(getClass().getResource("/db/migration/V2__validaciones_integridad_y_version.sql"));
        assertNull(getClass().getResource("/db/migration/V3__fecha_nacimiento_sin_hora.sql"));
    }

    @Test
    void entidadesSiguenSiendoSerializablesSinLaSuperclaseVersionada() throws Exception {
        Persona persona = new Persona(java.util.UUID.randomUUID()); persona.setNombres("Ana");
        persona.setFechaNacimiento(new java.util.Date(1000));
        var bytes = new java.io.ByteArrayOutputStream();
        try (var salida = new java.io.ObjectOutputStream(bytes)) { salida.writeObject(persona); }
        try (var entrada = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))) {
            Persona copia = (Persona) entrada.readObject();
            assertEquals(persona.getIdPersona(), copia.getIdPersona()); assertEquals(persona.getFechaNacimiento(), copia.getFechaNacimiento());
        }
    }
}
