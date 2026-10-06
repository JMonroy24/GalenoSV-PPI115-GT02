package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;

/** Guarda el paso y sus asociaciones como una unidad: un fallo revierte todo. */
@ApplicationScoped
public class AsignacionService {
    @Inject ProcedimientoPasoDAO pasoDAO;
    @Inject ProcedimientoPasoExamenDAO examenDAO;
    @Inject ProcedimientoPasoSecuenciaDAO secuenciaDAO;
    @PersistenceContext(unitName = "GalenoSV_PU") EntityManager em;

    @Transactional
    public void guardarPaso(ProcedimientoPaso paso, boolean nuevo, ProcedimientoPaso padre,
                            List<ProcedimientoPasoExamen> examenes) {
        if (nuevo) pasoDAO.create(paso);
        else pasoDAO.update(paso);
        if (nuevo && padre != null) {
            ProcedimientoPasoSecuencia relacion = new ProcedimientoPasoSecuencia(UUID.randomUUID());
            relacion.setIdProcedimientoPaso(padre);
            relacion.setIdProcedimientoPasoReferencia(paso.getIdProcedimientoPaso());
            relacion.setTipoSecuencia("SIGUIENTE");
            secuenciaDAO.create(relacion);
        }
        Set<UUID> retenidos = examenes.stream().map(ProcedimientoPasoExamen::getIdProcedimientoPasoExamen)
                .collect(Collectors.toSet());
        if (!nuevo) {
            for (ProcedimientoPasoExamen anterior : examenDAO.findByPaso(paso.getIdProcedimientoPaso())) {
                if (!retenidos.contains(anterior.getIdProcedimientoPasoExamen())) examenDAO.delete(anterior);
            }
        }
        for (ProcedimientoPasoExamen examen : examenes) guardarExamen(examen);
        em.flush();
    }

    @Transactional
    public void guardarExamen(ProcedimientoPasoExamen examen) {
        examenDAO.update(examen);
    }

    @Transactional
    public void eliminarPaso(ProcedimientoPaso paso) {
        List<ProcedimientoPasoSecuencia> relaciones = secuenciaDAO.findByProcedimiento(
                paso.getIdProcedimiento().getIdProcedimiento());
        if (relaciones.stream().anyMatch(s -> "SIGUIENTE".equals(s.getTipoSecuencia())
                && paso.equals(s.getIdProcedimientoPaso()))) {
            throw new IllegalArgumentException("No se puede eliminar un paso con hijos.");
        }
        for (ProcedimientoPasoSecuencia relacion : relaciones) {
            if (paso.getIdProcedimientoPaso().equals(relacion.getIdProcedimientoPasoReferencia())
                    || paso.equals(relacion.getIdProcedimientoPaso())) secuenciaDAO.delete(relacion);
        }
        for (ProcedimientoPasoExamen examen : examenDAO.findByPaso(paso.getIdProcedimientoPaso()))
            examenDAO.delete(examen);
        pasoDAO.delete(paso);
        em.flush();
    }
}
