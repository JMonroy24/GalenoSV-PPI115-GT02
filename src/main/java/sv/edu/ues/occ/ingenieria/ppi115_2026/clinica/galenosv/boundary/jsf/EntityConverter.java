package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.IdentificableEntity;

/**
 * Convertidor JSF genérico para entidades JPA basado en UUID.
 * Permite usar la entidad directamente como valor en componentes de selección.
 */
@Named("entityConverter")
@RequestScoped
public class EntityConverter implements Converter<Object> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            // El formato será Clase:UUID, ej. sv...Persona:1234-5678...
            int colonIndex = value.indexOf(':');
            if (colonIndex > 0) {
                String className = value.substring(0, colonIndex);
                String idStr = value.substring(colonIndex + 1);
                UUID id = UUID.fromString(idStr);
                if (!id.toString().equalsIgnoreCase(idStr)) throw seleccionInvalida();
                Class<?> entityClass = em.getMetamodel().getEntities().stream()
                        .map(e -> e.getJavaType())
                        .filter(c -> c.getName().equals(className) && IdentificableEntity.class.isAssignableFrom(c))
                        .findFirst().orElseThrow(this::seleccionInvalida);
                Object entidad = em.find(entityClass, id);
                if (entidad == null) throw seleccionInvalida();
                return entidad;
            }
        } catch (IllegalArgumentException e) {
            throw seleccionInvalida();
        }
        throw seleccionInvalida();
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof IdentificableEntity entidad && entidad.getIdKey() != null
                && !entidad.getIdKey().isBlank()) {
            Class<?> tipo = em.getMetamodel().getEntities().stream().map(e -> e.getJavaType())
                    .filter(c -> c.isInstance(value)).findFirst().orElseThrow(this::seleccionInvalida);
            return tipo.getName() + ":" + entidad.getIdKey();
        }
        throw seleccionInvalida();
    }

    private ConverterException seleccionInvalida() {
        return new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Selección no válida", "Seleccione nuevamente un registro de la lista."));
    }
}
