package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter(autoApply = true)
public class UUIDConverter implements AttributeConverter<UUID, String> {

    @Override
    public String convertToDatabaseColumn(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return UUID.fromString(s);
    }
}
