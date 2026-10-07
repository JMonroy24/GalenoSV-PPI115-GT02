package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.persistence;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.sessions.Session;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import static org.junit.jupiter.api.Assertions.*;

class UuidFieldSessionCustomizerTest {
    @Test
    void normalizaPkYFkUuidSinCambiarCamposDeTexto() throws Exception {
        Project proyecto = new Project();
        ClassDescriptor descriptor = new ClassDescriptor();
        descriptor.setJavaClass(Persona.class);
        
        DatabaseField pk = new DatabaseField("id_persona");
        pk.setType(UUID.class);
        DirectToFieldMapping mPk = new DirectToFieldMapping();
        mPk.setField(pk);
        descriptor.addMapping(mPk);
        
        DatabaseField fk = new DatabaseField("id_rol");
        fk.setTypeName("java.util.UUID");
        DirectToFieldMapping mFk = new DirectToFieldMapping();
        mFk.setField(fk);
        descriptor.addMapping(mFk);
        
        DatabaseField texto = new DatabaseField("nombre");
        texto.setType(String.class); 
        texto.setSqlType(Types.VARCHAR);
        DirectToFieldMapping mTexto = new DirectToFieldMapping();
        mTexto.setField(texto);
        descriptor.addMapping(mTexto);
        
        descriptor.addPrimaryKeyField(pk);
        
        proyecto.getDescriptors().put(Persona.class, descriptor);
        proyecto.addDescriptor(descriptor);
        
        org.eclipse.persistence.sessions.DatabaseLogin login = new org.eclipse.persistence.sessions.DatabaseLogin();
        proyecto.setLogin(login);
        
        ServerSession s = new ServerSession(proyecto);

        new UuidFieldSessionCustomizer().customize(s);

        assertEquals(Types.OTHER, pk.getSqlType());
        assertEquals(Types.OTHER, fk.getSqlType());
        assertEquals(UUID.class, fk.getType());
        assertEquals("uuid", fk.getColumnDefinition());
        assertEquals(Types.VARCHAR, texto.getSqlType());
        assertEquals(String.class, texto.getType());
    }
}
