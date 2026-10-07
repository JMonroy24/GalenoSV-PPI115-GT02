package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import jakarta.persistence.PrePersist;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

class FechaCreacionTest {

    @ParameterizedTest
    @MethodSource("entidadesAuditadas")
    void callbackAsignaFechaDeCreacionUnaVez(Class<?> tipo) throws Exception {
        Object entidad = tipo.getConstructor().newInstance();
        Method getter = tipo.getMethod("getFechaCreacion");
        Method callback = Arrays.stream(tipo.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(PrePersist.class)).findFirst().orElseThrow();
        assertNull(getter.invoke(entidad));
        long antes = System.currentTimeMillis();
        callback.invoke(entidad);
        Date creada = (Date) getter.invoke(entidad);
        assertNotNull(creada);
        assertTrue(creada.getTime() >= antes && creada.getTime() <= System.currentTimeMillis());
        callback.invoke(entidad);
        assertEquals(creada, getter.invoke(entidad));

        Date importada = new Date(1000);
        tipo.getMethod("setFechaCreacion", Date.class).invoke(entidad, importada);
        callback.invoke(entidad);
        assertEquals(importada, getter.invoke(entidad), "Una fecha importada debe conservarse");
    }

    static Stream<Class<?>> entidadesAuditadas() {
        return Stream.of(Persona.class, MedioContacto.class, PersonaRol.class, OrdenExamen.class,
                ExamenResultado.class, ExamenTipoExamen.class, ProcedimientoPasoExamen.class);
    }
}
