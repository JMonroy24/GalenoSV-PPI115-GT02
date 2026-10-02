package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.persistence;

import java.sql.Types;
import java.util.UUID;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PostgresUuidPlatformTest {
    private final PostgresUuidPlatform plataforma = new PostgresUuidPlatform();

    @Test
    void uuidUsaTipoNativoIncluyendoValoresNulos() {
        DatabaseField campo = new DatabaseField("id_persona");
        campo.setType(UUID.class);
        assertEquals(Types.OTHER, plataforma.getJDBCType(UUID.class));
        assertEquals(Types.OTHER, plataforma.getJDBCTypeForSetNull(campo));
    }

    @Test
    void reconoceUuidEnMetadataDeUnaFkSinTipoJava() {
        DatabaseField campo = new DatabaseField("id_rol");
        campo.setTypeName("java.util.UUID");
        assertEquals(Types.OTHER, plataforma.getJDBCTypeForSetNull(campo));
        campo.setTypeName(null);
        campo.setColumnDefinition("uuid");
        assertEquals(Types.OTHER, plataforma.getJDBCTypeForSetNull(campo));
    }

    @Test
    void camposDeTextoConservanTipoVarchar() {
        DatabaseField campo = new DatabaseField("nombre");
        campo.setType(String.class);
        assertEquals(Types.VARCHAR, plataforma.getJDBCType(String.class));
        assertEquals(Types.VARCHAR, plataforma.getJDBCTypeForSetNull(campo));
    }
}
