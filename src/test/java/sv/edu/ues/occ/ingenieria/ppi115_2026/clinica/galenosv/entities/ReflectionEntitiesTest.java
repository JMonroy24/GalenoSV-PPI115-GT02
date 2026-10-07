package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ReflectionEntitiesTest {

    @Test
    void testAllEntities() throws Exception {
        Class<?>[] classes = {
            Clinica.class, Consulta.class, ConsultaProcedimiento.class, ConsultaProcedimientoPaso.class,
            Documento.class, Examen.class, ExamenResultado.class, ExamenTipoExamen.class, MedioContacto.class,
            OrdenExamen.class, Persona.class, PersonaRol.class, Procedimiento.class, ProcedimientoPaso.class,
            ProcedimientoPasoExamen.class, ProcedimientoPasoSecuencia.class, Rol.class, TipoDocumento.class,
            TipoExamen.class, TipoMedioContacto.class
        };

        for (Class<?> clazz : classes) {
            Object instance;
            try {
                instance = clazz.getDeclaredConstructor(UUID.class).newInstance(UUID.randomUUID());
            } catch (NoSuchMethodException e) {
                instance = clazz.getDeclaredConstructor().newInstance();
            }

            for (Method method : clazz.getMethods()) {
                if (method.getName().startsWith("get") && method.getParameterCount() == 0 && !method.getName().equals("getClass")) {
                    try {
                        String setterName = "set" + method.getName().substring(3);
                        Method setter = clazz.getMethod(setterName, method.getReturnType());
                        
                        Object testValue = null;
                        Class<?> type = method.getReturnType();
                        if (type == String.class) testValue = "test";
                        else if (type == Boolean.class || type == boolean.class) testValue = true;
                        else if (type == Integer.class || type == int.class) testValue = 1;
                        else if (type == Long.class || type == long.class) testValue = 1L;
                        else if (type == Date.class) testValue = new Date();
                        else if (type == UUID.class) testValue = UUID.randomUUID();
                        else if (type == List.class) testValue = List.of();
                        else if (type.getPackageName().startsWith("sv.edu.ues")) {
                            try {
                                testValue = type.getDeclaredConstructor().newInstance();
                            } catch (Exception ignored) {}
                        }
                        
                        if (testValue != null) {
                            setter.invoke(instance, testValue);
                            Object result = method.invoke(instance);
                            assertEquals(testValue, result);
                        }
                    } catch (NoSuchMethodException ignored) {
                        // Setter doesn't exist, which is fine
                        method.invoke(instance);
                    }
                }
            }
        }
    }
}
