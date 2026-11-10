package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

/**
 * Acceso a datos para la entidad OrdenExamen.
 */
@ApplicationScoped
public class OrdenExamenDAO extends DefaultDAO<OrdenExamen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public OrdenExamenDAO() {
        super(OrdenExamen.class);
    }

    public OrdenExamenDAO(EntityManager em) {
        super(OrdenExamen.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected List<String> getCamposBusqueda() {
        return List.of("indicaciones");
    }

    /**
     * Busca registros para autocompletado (p:autoComplete), sin distinguir mayúsculas.
     *
     * @param filtro texto digitado por el usuario
     * @param max    cantidad máxima de resultados
     * @return lista de coincidencias
     */
    @Override
    public List<OrdenExamen> buscarParaAutocompletar(String filtro, int max) {
        return autocompletarConConsulta("OrdenExamen.buscarParaAutocompletar", filtro, max, 2);
    }

    @Override
    protected java.util.List<String> getRelacionesCarga() {
        return java.util.List.of("idConsultaProcedimientoPaso.idConsultaProcedimiento.idProcedimiento");
    }
}
