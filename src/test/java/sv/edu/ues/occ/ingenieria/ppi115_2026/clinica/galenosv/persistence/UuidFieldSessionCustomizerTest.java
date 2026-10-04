package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.persistence;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UuidFieldSessionCustomizerTest {
    @Test
    void normalizaPkYFkUuidSinCambiarCamposDeTexto() throws Exception {
        Session sesion = mock(Session.class);
        Project proyecto = mock(Project.class);
        ClassDescriptor descriptor = mock(ClassDescriptor.class);
        DatabaseField pk = new DatabaseField("id_persona"), fk = new DatabaseField("id_rol"), texto = new DatabaseField("nombre");
        pk.setType(UUID.class);
        fk.setTypeName("java.util.UUID");
        texto.setType(String.class); texto.setSqlType(Types.VARCHAR);
        when(sesion.getProject()).thenReturn(proyecto);
        when(proyecto.getDescriptors()).thenReturn(Map.of(Persona.class, descriptor));
        doReturn(Persona.class).when(descriptor).getJavaClass();
        when(descriptor.getPrimaryKeyFields()).thenReturn(List.of(pk));
        when(descriptor.getAllFields()).thenReturn(List.of(pk, fk, texto));

        new UuidFieldSessionCustomizer().customize(sesion);

        assertEquals(Types.OTHER, pk.getSqlType());
        assertEquals(Types.OTHER, fk.getSqlType());
        assertEquals(UUID.class, fk.getType());
        assertEquals("uuid", fk.getColumnDefinition());
        assertEquals(Types.VARCHAR, texto.getSqlType());
        assertEquals(String.class, texto.getType());
    }
}
