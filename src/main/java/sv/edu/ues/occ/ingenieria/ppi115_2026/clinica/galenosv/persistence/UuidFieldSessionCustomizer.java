package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.persistence;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionCustomizer;

/**
 * Tag every UUID id / FK DatabaseField as SQL type OTHER so PostgreSQL
 * receives native uuid binds (including NULL foreign keys).
 */
public class UuidFieldSessionCustomizer implements SessionCustomizer {

    @Override
    public void customize(Session session) throws Exception {
        for (ClassDescriptor descriptor : session.getProject().getDescriptors().values()) {
            if (descriptor == null || descriptor.getJavaClass() == null) {
                continue;
            }
            try {
                List<DatabaseField> pks = descriptor.getPrimaryKeyFields();
                if (pks != null) {
                    for (DatabaseField f : pks) {
                        fixField(f);
                    }
                }
                List<DatabaseField> all = descriptor.getAllFields();
                if (all != null) {
                    for (DatabaseField f : all) {
                        fixField(f);
                    }
                }
                for (DatabaseMapping mapping : descriptor.getMappings()) {
                    if (mapping == null) {
                        continue;
                    }
                    try {
                        fixField(mapping.getField());
                        List<DatabaseField> fields = mapping.getFields();
                        if (fields != null) {
                            for (DatabaseField f : fields) {
                                fixField(f);
                            }
                        }
                        if (mapping instanceof ObjectReferenceMapping orm) {
                            List<DatabaseField> fks = orm.getForeignKeyFields();
                            if (fks != null) {
                                for (DatabaseField f : fks) {
                                    fixField(f);
                                }
                            }
                        }
                        if (mapping instanceof OneToOneMapping o2o) {
                            Map<DatabaseField, DatabaseField> src = o2o.getSourceToTargetKeyFields();
                            if (src != null) {
                                for (Map.Entry<DatabaseField, DatabaseField> e : src.entrySet()) {
                                    fixField(e.getKey());
                                    fixField(e.getValue());
                                }
                            }
                        }
                        if (mapping instanceof OneToManyMapping otom) {
                            List<DatabaseField> fks = otom.getTargetForeignKeyFields();
                            if (fks != null) {
                                for (DatabaseField f : fks) {
                                    fixField(f);
                                }
                            }
                            Map<DatabaseField, DatabaseField> src = otom.getSourceKeysToTargetForeignKeys();
                            if (src != null) {
                                for (Map.Entry<DatabaseField, DatabaseField> e : src.entrySet()) {
                                    fixField(e.getKey());
                                    fixField(e.getValue());
                                }
                            }
                        }
                    } catch (RuntimeException ignored) {
                        // mapping not fully initialized during customizer phase
                    }
                }
            } catch (RuntimeException ignored) {
                // descriptor not fully initialized during customizer phase
            }
        }
    }

    private void fixField(DatabaseField field) {
        if (field == null) {
            return;
        }
        Class<?> type = field.getType();
        String typeName = field.getTypeName();
        boolean isUuid = UUID.class.equals(type)
                || (typeName != null && typeName.toLowerCase().contains("uuid"))
                || (field.getColumnDefinition() != null
                        && field.getColumnDefinition().toLowerCase().contains("uuid"));
        if (isUuid) {
            field.setSqlType(Types.OTHER);
            field.setTypeName("uuid");
            field.setColumnDefinition("uuid");
            if (type == null) {
                field.setType(UUID.class);
            }
        }
    }
}
