package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;

/** Guarda asociaciones y sus reglas en una sola transacción. */
@ApplicationScoped
public class AsignacionService {
    @Inject ProcedimientoPasoDAO pasoDAO;
    @Inject ProcedimientoPasoExamenDAO examenDAO;
    @Inject ProcedimientoPasoSecuenciaDAO secuenciaDAO;
    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;
    @Inject protected ExamenTipoExamenDAO examenTipoExamenDAO;
    @Inject protected ProcedimientoPasoExamenDAO procedimientoPasoExamenDAO;
    @Inject protected ProcedimientoPasoSecuenciaDAO procedimientoPasoSecuenciaDAO;

    @Transactional
    public void guardarTipo(ExamenTipoExamen registro, boolean nuevo) {
        ValidadorComun.requerido(registro, "Seleccione una asignación.");
        var examen = ValidadorComun.requerido(registro.getIdExamen(), "Seleccione un examen.");
        var tipo = ValidadorComun.requerido(registro.getIdTipoExamen(), "Seleccione un tipo de examen.");
        // Todas las altas de tipos del mismo examen se serializan con el mismo padre.
        examen = bloquear(Examen.class, examen.getIdExamen(), "El examen ya no existe.");
        tipo = existente(TipoExamen.class, tipo.getIdTipoExamen(), "El tipo de examen ya no existe.");
        ValidadorComun.activo(examen.getActivo(), "El examen");
        ValidadorComun.activo(tipo.getActivo(), "El tipo de examen");
        if (examenTipoExamenDAO.existeExamenTipo(examen.getIdExamen(), tipo.getIdTipoExamen(),
                nuevo ? null : registro.getIdExamenTipoExamen())) {
            throw new ValidacionNegocioException("Este tipo ya está asignado al examen.");
        }
        registro.setIdExamen(examen);
        registro.setIdTipoExamen(tipo);
        if (nuevo) examenTipoExamenDAO.create(registro);
        else examenTipoExamenDAO.update(registro);
        em.flush();
    }

    @Transactional
    public void guardarExamen(ProcedimientoPasoExamen registro, boolean nuevo) {
        ValidadorComun.requerido(registro, "Seleccione una asignación.");
        var paso = ValidadorComun.requerido(registro.getIdProcedimientoPaso(), "Seleccione un paso.");
        var examen = ValidadorComun.requerido(registro.getIdExamen(), "Seleccione un examen.");
        paso = bloquear(ProcedimientoPaso.class, paso.getIdProcedimientoPaso(), "El paso ya no existe.");
        examen = existente(Examen.class, examen.getIdExamen(), "El examen ya no existe.");
        ValidadorComun.activo(examen.getActivo(), "El examen");
        if (procedimientoPasoExamenDAO.existePasoExamen(paso.getIdProcedimientoPaso(), examen.getIdExamen(),
                nuevo ? null : registro.getIdProcedimientoPasoExamen())) {
            throw new ValidacionNegocioException("Este examen ya está asignado al paso.");
        }
        registro.setIdProcedimientoPaso(paso);
        registro.setIdExamen(examen);
        if (nuevo) procedimientoPasoExamenDAO.create(registro);
        else procedimientoPasoExamenDAO.update(registro);
        em.flush();
    }

    @Transactional
    public void guardarSecuencia(ProcedimientoPasoSecuencia registro, boolean nuevo) {
        ValidadorComun.requerido(registro, "Seleccione una secuencia.");
        var origen = ValidadorComun.requerido(registro.getIdProcedimientoPaso(), "Seleccione el paso de origen.");
        origen = existente(ProcedimientoPaso.class, origen.getIdProcedimientoPaso(), "El paso de origen ya no existe.");
        var procedimiento = ValidadorComun.requerido(origen.getIdProcedimiento(), "El paso no pertenece a un procedimiento.");
        // El bloqueo del procedimiento evita que A→B y B→A se aprueben simultáneamente.
        bloquear(Procedimiento.class, procedimiento.getIdProcedimiento(), "El procedimiento ya no existe.");
        var destino = existente(ProcedimientoPaso.class, registro.getIdProcedimientoPasoReferencia(),
                "Seleccione un paso de referencia existente.");
        registro.setTipoSecuencia(ValidadorComun.textoObligatorio(registro.getTipoSecuencia(), "El tipo de secuencia"));
        if (registro.getTipoSecuencia().length() > 20) {
            throw new ValidacionNegocioException("El tipo de secuencia no puede superar 20 caracteres.");
        }
        if (destino.getIdProcedimiento() == null || !Objects.equals(
                procedimiento.getIdProcedimiento(), destino.getIdProcedimiento().getIdProcedimiento())) {
            throw new ValidacionNegocioException("Los pasos de la secuencia deben pertenecer al mismo procedimiento.");
        }
        UUID idOrigen = origen.getIdProcedimientoPaso();
        UUID idDestino = destino.getIdProcedimientoPaso();
        UUID excluir = nuevo ? null : registro.getIdProcedimientoPasoSecuencia();
        if (procedimientoPasoSecuenciaDAO.existeSecuencia(idOrigen, idDestino, registro.getTipoSecuencia(), excluir)) {
            throw new ValidacionNegocioException("Esta relación de secuencia ya existe.");
        }
        validarSinCiclo(idOrigen, idDestino, excluir,
                procedimientoPasoSecuenciaDAO.findByProcedimiento(procedimiento.getIdProcedimiento()));
        registro.setIdProcedimientoPaso(origen);
        if (nuevo) procedimientoPasoSecuenciaDAO.create(registro);
        else procedimientoPasoSecuenciaDAO.update(registro);
        em.flush();
    }

    private <T> T bloquear(Class<T> clase, UUID id, String mensaje) {
        ValidadorComun.requerido(id, mensaje);
        return ValidadorComun.requerido(em.find(clase, id, LockModeType.PESSIMISTIC_WRITE), mensaje);
    }

    private <T> T existente(Class<T> clase, UUID id, String mensaje) {
        ValidadorComun.requerido(id, mensaje);
        return ValidadorComun.requerido(em.find(clase, id), mensaje);
    }

    /** DFS iterativo; ignora la arista original al editar y tolera grafos históricos cíclicos. */
    static void validarSinCiclo(UUID origen, UUID destino, UUID excluir,
            List<ProcedimientoPasoSecuencia> secuencias) {
        Map<UUID, List<UUID>> adyacentes = new HashMap<>();
        for (var secuencia : secuencias) {
            if (excluir != null && excluir.equals(secuencia.getIdProcedimientoPasoSecuencia())) continue;
            if (secuencia.getIdProcedimientoPaso() == null || secuencia.getIdProcedimientoPasoReferencia() == null) continue;
            UUID desde = secuencia.getIdProcedimientoPaso().getIdProcedimientoPaso();
            adyacentes.computeIfAbsent(desde, id -> new ArrayList<>()).add(secuencia.getIdProcedimientoPasoReferencia());
        }
        Set<UUID> visitados = new HashSet<>();
        ArrayDeque<UUID> pendientes = new ArrayDeque<>();
        pendientes.push(destino);
        while (!pendientes.isEmpty()) {
            UUID actual = pendientes.pop();
            if (origen.equals(actual)) {
                throw new ValidacionNegocioException("La secuencia produciría un ciclo entre los pasos.");
            }
            if (visitados.add(actual)) {
                pendientes.addAll(adyacentes.getOrDefault(actual, List.of()));
            }
        }
    }

    /** Padre e inicio se confirman juntos: un fallo del paso revierte ambos. */
    @Transactional
    public void crearProcedimientoConPaso(ConsultaProcedimiento procedimiento, ConsultaProcedimientoPaso paso) {
        paso.setIdConsultaProcedimiento(procedimiento);
        em.persist(procedimiento);
        em.persist(paso);
        em.flush();
    }

    /** Crea el examen y su tipo inicial en una única transacción. */
    @Transactional
    public void crearExamenConTipo(Examen examen, ExamenTipoExamen tipo) {
        tipo.setIdExamen(examen);
        em.persist(examen);
        em.persist(tipo);
        em.flush();
    }

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
