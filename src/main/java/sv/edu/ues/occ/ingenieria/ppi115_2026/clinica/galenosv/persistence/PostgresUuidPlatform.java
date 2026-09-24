package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.persistence;

import java.sql.Types;
import java.util.UUID;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;

/**
 * PostgreSQL rejects NULL binds for uuid columns typed as VARCHAR
 * (EclipseLink issue #2717). Force Types.OTHER for UUID fields.
 */
public class PostgresUuidPlatform extends PostgreSQLPlatform {

    @Override
    public int getJDBCTypeForSetNull(DatabaseField field) {
        if (isUuidField(field)) {
            return Types.OTHER;
        }
        return super.getJDBCTypeForSetNull(field);
    }

    @Override
    public int getJDBCType(Class<?> javaType) {
        if (UUID.class.equals(javaType)) {
            return Types.OTHER;
        }
        return super.getJDBCType(javaType);
    }

    private boolean isUuidField(DatabaseField field) {
        if (field == null) {
            return false;
        }
        if (UUID.class.equals(field.getType())) {
            return true;
        }
        if (field.getSqlType() == Types.OTHER) {
            return true;
        }
        String typeName = field.getTypeName();
        if (typeName != null) {
            String t = typeName.toLowerCase();
            if (t.equals("uuid") || t.equals("java.util.uuid")) {
                return true;
            }
        }
        String colDef = field.getColumnDefinition();
        return colDef != null && colDef.toLowerCase().contains("uuid");
    }
}
