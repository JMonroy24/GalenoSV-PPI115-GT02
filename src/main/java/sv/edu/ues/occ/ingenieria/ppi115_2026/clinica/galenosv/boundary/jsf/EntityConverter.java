package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convertidor JSF genérico para entidades JPA basado en UUID.
 * Permite usar la entidad directamente como valor en componentes de selección.
 */
@Named("entityConverter")
@RequestScoped
public class EntityConverter implements Converter<Object> {

    private static final Logger LOGGER = Logger.getLogger(EntityConverter.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return null;
        }
        try {
            // El formato será Clase:UUID, ej. sv...Persona:1234-5678...
            int colonIndex = value.indexOf(':');
            if (colonIndex > 0) {
                String className = value.substring(0, colonIndex);
                String idStr = value.substring(colonIndex + 1);
                Class<?> entityClass = Class.forName(className);
                UUID id = UUID.fromString(idStr);
                return em.find(entityClass, id);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al convertir cadena a entidad: {0}", value);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        try {
            // Asume que todas las entidades tienen un método getId que retorna UUID
            // Usaremos la API de JPA para obtener el identificador primario de forma genérica
            Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(value);
            if (id != null) {
                return value.getClass().getName() + ":" + id.toString();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al convertir entidad a cadena: {0}", value);
        }
        return value.toString();
    }
}
