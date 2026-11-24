package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf.EstadoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;

@ApplicationScoped
public class ConsultaProcedimientoPasoService extends DefaultService<ConsultaProcedimientoPaso, UUID>
        implements Serializable {
    private static final long serialVersionUID = 1L;
    @Inject protected ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO;

    public ConsultaProcedimientoPasoService() { }
    public ConsultaProcedimientoPasoService(ConsultaProcedimientoPasoDAO dao) { this.consultaProcedimientoPasoDAO = dao; }
    @Override protected DefaultDAO<ConsultaProcedimientoPaso, UUID> getDAO() { return consultaProcedimientoPasoDAO; }

    /** Invariantes compartidas por las altas REST, JSF y las transiciones automáticas. */
    public static void validarRelaciones(ConsultaProcedimientoPaso paso) {
        var asignado = ValidadorComun.requerido(paso.getIdConsultaProcedimiento(), "Seleccione un procedimiento de consulta.");
        var procedimiento = ValidadorComun.requerido(asignado.getIdProcedimiento(), "Seleccione un procedimiento.");
        var consulta = ValidadorComun.requerido(asignado.getIdConsulta(), "Seleccione una consulta.");
        var paciente = ValidadorComun.requerido(consulta.getIdPersonaRol(), "La consulta debe tener un paciente.");
        var clinica = ValidadorComun.requerido(paciente.getIdClinica(), "La consulta debe tener una clínica.");
        var definicion = ValidadorComun.requerido(paso.getIdProcedimientoPaso(), "El paso de procedimiento es obligatorio.");
        if (!Objects.equals(procedimiento, definicion.getIdProcedimiento())) {
            throw new ValidacionNegocioException("El paso no pertenece al procedimiento de la consulta.");
        }
        var rol = ValidadorComun.requerido(definicion.getIdRol(), "El paso debe tener un rol responsable.");
        var responsable = ValidadorComun.requerido(paso.getIdPersonaRol(), "Seleccione un responsable.");
        ValidadorComun.activo(procedimiento.getActivo(), "El procedimiento");
        ValidadorComun.activo(rol.getActivo(), "El rol del paso");
        ValidadorComun.activo(clinica.getActivo(), "La clínica");
        ValidadorComun.requerido(responsable.getIdPersona(), "El responsable debe tener una persona.");
        if (!Objects.equals(rol, responsable.getIdRol()) || !Objects.equals(clinica, responsable.getIdClinica())) {
            throw new ValidacionNegocioException("El responsable debe tener el rol del paso en la clínica de la consulta.");
        }
        ValidadorComun.activo(responsable.getIdRol().getActivo(), "El rol del responsable");
        ValidadorComun.activo(responsable.getIdClinica().getActivo(), "La clínica del responsable");
    }

    private static EstadoPaso estado(String valor) {
        try {
            return ValidadorComun.requerido(EstadoPaso.fromString(valor), "El estado es obligatorio.");
        } catch (IllegalArgumentException e) {
            throw new ValidacionNegocioException("Estado no válido: use PENDIENTE, EN_CURSO o COMPLETADO.");
        }
    }

    @Override protected void validar(ConsultaProcedimientoPaso paso, boolean nuevo) {
        paso.setEstado(estado(paso.getEstado()).name());
        validarRelaciones(paso);
        if (paso.getFechaInicio() == null) paso.setFechaInicio(new Date());
        if (estado(paso.getEstado()) == EstadoPaso.COMPLETADO && (nuevo || paso.getFechaFin() == null)) paso.setFechaFin(new Date());
        ValidadorComun.rangoFechas(paso.getFechaInicio(), paso.getFechaFin());
        // Las órdenes históricas conservan sus datos, pero no se puede operar con un examen inactivo.
        if (paso.getOrdenExamenList() != null) {
            for (var orden : paso.getOrdenExamenList()) {
                if (orden.getIdExamen() != null) ValidadorComun.activo(orden.getIdExamen().getActivo(), "El examen");
            }
        }
    }

    @Override @Transactional public void crear(ConsultaProcedimientoPaso paso) {
        validar(paso, true);
        consultaProcedimientoPasoDAO.bloquearConsulta(paso);
        validarPredecesor(paso);
        if (consultaProcedimientoPasoDAO.findRegistro(paso.getIdConsultaProcedimiento().getIdConsultaProcedimiento(),
                paso.getIdProcedimientoPaso().getIdProcedimientoPaso()) != null) {
            throw new ValidacionNegocioException("El paso ya existe en este procedimiento de consulta.");
        }
        getDAO().create(paso);
        if (estado(paso.getEstado()) == EstadoPaso.COMPLETADO) avanzar(paso);
    }

    @Override @Transactional
    public ConsultaProcedimientoPaso actualizarSi(UUID id, Predicate<ConsultaProcedimientoPaso> precondicion,
            Consumer<ConsultaProcedimientoPaso> cambios) {
        var paso = obtenerBloqueado(id, precondicion);
        var anterior = estado(paso.getEstado());
        Date fin = paso.getFechaFin();
        var asignado = paso.getIdConsultaProcedimiento();
        var definicion = paso.getIdProcedimientoPaso();
        cambios.accept(paso);
        if (!Objects.equals(asignado, paso.getIdConsultaProcedimiento())
                || !Objects.equals(definicion, paso.getIdProcedimientoPaso())) {
            throw new ValidacionNegocioException("No puede cambiar el procedimiento ni la definición de un paso registrado.");
        }
        paso.setFechaFin(fin);
        validar(paso, false);
        if (!EstadoPaso.transicionValida(anterior, estado(paso.getEstado()))) {
            throw new ValidacionNegocioException("Transición no permitida. Use accion=RETROCEDER para volver al paso anterior.");
        }
        if (anterior != estado(paso.getEstado())) validarPredecesor(paso);
        getDAO().update(paso);
        if (estado(paso.getEstado()) == EstadoPaso.COMPLETADO) avanzar(paso);
        consultaProcedimientoPasoDAO.getEntityManager().flush();
        return paso;
    }

    @Override @Transactional public void actualizar(ConsultaProcedimientoPaso entidad) {
        // Capturar antes de refrescar: JSF puede entregar la misma instancia administrada.
        String nuevoEstado = entidad.getEstado(), valor = entidad.getValor();
        var responsable = entidad.getIdPersonaRol();
        var definicion = entidad.getIdProcedimientoPaso();
        var asignado = entidad.getIdConsultaProcedimiento();
        Date inicio = entidad.getFechaInicio();
        actualizarSi(entidad.getIdConsultaProcedimientoPaso(), p -> true, p -> {
            p.setEstado(nuevoEstado); p.setValor(valor); p.setIdPersonaRol(responsable);
            p.setIdProcedimientoPaso(definicion); p.setIdConsultaProcedimiento(asignado); p.setFechaInicio(inicio);
        });
    }

    private ConsultaProcedimientoPaso obtenerBloqueado(UUID id, Predicate<ConsultaProcedimientoPaso> precondicion) {
        var paso = consultaProcedimientoPasoDAO.findByIdParaFlujo(id);
        if (paso == null) throw new NotFoundException("No existe el paso solicitado.");
        if (!precondicion.test(paso)) throw new PreconditionFailedException("El ETag enviado no coincide con el estado actual del recurso.");
        return paso;
    }

    @Transactional public ConsultaProcedimientoPaso actualizarFormulario(UUID id,
            Predicate<ConsultaProcedimientoPaso> precondicion, String estadoPaso, String valor) {
        return actualizarSi(id, precondicion, paso -> { paso.setEstado(estadoPaso); paso.setValor(valor); });
    }

    private List<ProcedimientoPasoSecuencia> secuencias(ConsultaProcedimientoPaso paso) {
        return new ProcedimientoPasoSecuenciaDAO(consultaProcedimientoPasoDAO.getEntityManager())
                .findByProcedimiento(paso.getIdConsultaProcedimiento().getIdProcedimiento().getIdProcedimiento())
                .stream().filter(s -> "SIGUIENTE".equalsIgnoreCase(s.getTipoSecuencia())).toList();
    }

    private ProcedimientoPaso relacionado(ConsultaProcedimientoPaso paso, boolean anterior) {
        UUID id = paso.getIdProcedimientoPaso().getIdProcedimientoPaso();
        var ids = secuencias(paso).stream()
                .filter(s -> anterior ? id.equals(s.getIdProcedimientoPasoReferencia())
                        : id.equals(s.getIdProcedimientoPaso().getIdProcedimientoPaso()))
                .map(s -> anterior ? s.getIdProcedimientoPaso().getIdProcedimientoPaso() : s.getIdProcedimientoPasoReferencia())
                .distinct().toList();
        if (ids.size() > 1) throw new ValidacionNegocioException("La secuencia debe tener un único " + (anterior ? "antecesor." : "sucesor."));
        if (ids.isEmpty()) return null;
        return ValidadorComun.requerido(consultaProcedimientoPasoDAO.getEntityManager().find(ProcedimientoPaso.class, ids.getFirst()),
                "La secuencia referencia un paso inexistente.");
    }

    private void validarPredecesor(ConsultaProcedimientoPaso paso) {
        var anterior = relacionado(paso, true);
        if (anterior == null) return;
        var registro = consultaProcedimientoPasoDAO.findRegistro(paso.getIdConsultaProcedimiento().getIdConsultaProcedimiento(),
                anterior.getIdProcedimientoPaso());
        if (registro == null || estado(registro.getEstado()) != EstadoPaso.COMPLETADO) {
            throw new ValidacionNegocioException("Debe completar el paso anterior antes de activar este paso.");
        }
    }

    private PersonaRol responsable(ConsultaProcedimientoPaso paso, ProcedimientoPaso definicion) {
        var clinica = paso.getIdConsultaProcedimiento().getIdConsulta().getIdPersonaRol().getIdClinica();
        var rol = ValidadorComun.requerido(definicion.getIdRol(), "El paso debe tener un rol responsable.");
        ValidadorComun.activo(rol.getActivo(), "El rol del siguiente paso");
        ValidadorComun.activo(clinica.getActivo(), "La clínica");
        return ValidadorComun.requerido(new PersonaRolDAO(consultaProcedimientoPasoDAO.getEntityManager())
                .findResponsable(clinica.getIdClinica(), rol.getIdRol()), "No hay responsable activo del rol en esta clínica.");
    }

    private void avanzar(ConsultaProcedimientoPaso paso) {
        var asignado = paso.getIdConsultaProcedimiento();
        var vigente = consultaProcedimientoPasoDAO.findRegistro(asignado.getIdConsultaProcedimiento(),
                paso.getIdProcedimientoPaso().getIdProcedimientoPaso());
        // Editar una ejecución histórica no adelanta otra ejecución todavía activa de la misma definición.
        if (vigente != null && !vigente.equals(paso) && estado(vigente.getEstado()) != EstadoPaso.COMPLETADO) return;
        if (Boolean.TRUE.equals(paso.getIdProcedimientoPaso().getIndicaFin())) {
            if (asignado.getFechaFin() == null) asignado.setFechaFin(paso.getFechaFin());
            if (asignado.getIdConsulta().getFechaFin() == null) asignado.getIdConsulta().setFechaFin(paso.getFechaFin());
            return;
        }
        var siguiente = ValidadorComun.requerido(relacionado(paso, false), "El paso no es final y no tiene sucesor definido.");
        var existente = consultaProcedimientoPasoDAO.findRegistro(asignado.getIdConsultaProcedimiento(), siguiente.getIdProcedimientoPaso());
        if (existente != null && estado(existente.getEstado()) == EstadoPaso.COMPLETADO) return;
        var persona = responsable(paso, siguiente);
        if (existente == null) {
            existente = new ConsultaProcedimientoPaso(UUID.randomUUID());
            existente.setIdConsultaProcedimiento(asignado);
            existente.setIdProcedimientoPaso(siguiente);
            existente.setFechaInicio(new Date());
            existente.setValor("");
            existente.setIdPersonaRol(persona);
            existente.setEstado(EstadoPaso.EN_CURSO.name());
            validar(existente, true);
            getDAO().create(existente);
        } else {
            existente.setIdPersonaRol(persona);
            existente.setEstado(EstadoPaso.EN_CURSO.name());
            validar(existente, false);
        }
    }

    /** Reactiva el antecesor existente y devuelve el paso solicitado, ahora pendiente. */
    @Transactional public ConsultaProcedimientoPaso retroceder(UUID id, Predicate<ConsultaProcedimientoPaso> precondicion, String valor) {
        var paso = obtenerBloqueado(id, precondicion);
        validar(paso, false);
        var anterior = relacionado(paso, true);
        if (!EstadoPaso.puedeRetroceder(estado(paso.getEstado()), anterior != null)) {
            throw new ValidacionNegocioException("No puede retroceder desde el primer paso.");
        }
        var registro = ValidadorComun.requerido(consultaProcedimientoPasoDAO.findRegistro(
                paso.getIdConsultaProcedimiento().getIdConsultaProcedimiento(), anterior.getIdProcedimientoPaso()),
                "No existe el paso anterior registrado.");
        if (estado(registro.getEstado()) != EstadoPaso.COMPLETADO) {
            throw new ValidacionNegocioException("El paso anterior ya está activo o pendiente.");
        }
        // No se permite retroceder desde una fila histórica dejando sucesores en ejecución.
        var siguiente = relacionado(paso, false);
        if (siguiente != null) {
            var posterior = consultaProcedimientoPasoDAO.findRegistro(paso.getIdConsultaProcedimiento().getIdConsultaProcedimiento(), siguiente.getIdProcedimientoPaso());
            if (posterior != null && estado(posterior.getEstado()) != EstadoPaso.PENDIENTE) {
                throw new ValidacionNegocioException("Debe retroceder primero desde el paso vigente.");
            }
        }
        registro.setIdPersonaRol(responsable(paso, anterior));
        registro.setEstado(EstadoPaso.EN_CURSO.name());
        validar(registro, false);
        paso.setEstado(EstadoPaso.PENDIENTE.name());
        paso.setValor(valor);
        paso.getIdConsultaProcedimiento().setFechaFin(null);
        paso.getIdConsultaProcedimiento().getIdConsulta().setFechaFin(null);
        consultaProcedimientoPasoDAO.getEntityManager().flush();
        return paso;
    }

    public List<ConsultaProcedimientoPaso> findByConsultaProcedimiento(UUID id) {
        return id == null ? List.of() : consultaProcedimientoPasoDAO.findByConsultaProcedimiento(id);
    }
    public ConsultaProcedimientoPasoDAO getConsultaProcedimientoPasoDAO() { return consultaProcedimientoPasoDAO; }
    public void setConsultaProcedimientoPasoDAO(ConsultaProcedimientoPasoDAO dao) { consultaProcedimientoPasoDAO = dao; }
}
