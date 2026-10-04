package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.FetchParent;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Operaciones CRUD, paginación estable y búsqueda reutilizable para entidades JPA. */
public abstract class DefaultDAO<T, ID extends Serializable> implements DAOInterface<T, ID> {

    public static final int MAX_FILTRO = 100;
    private final Class<T> entityClass;
    private final String campoId;

    public DefaultDAO(Class<T> entityClass) {
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass");
        this.campoId = encontrarId(entityClass);
    }

    public abstract EntityManager getEntityManager();

    @Override
    @Transactional
    public void create(T entity) {
        getEntityManager().persist(Objects.requireNonNull(entity, "entity"));
    }

    @Override
    @Transactional
    public void update(T entity) {
        getEntityManager().merge(Objects.requireNonNull(entity, "entity"));
    }

    @Override
    @Transactional
    public void delete(T entity) {
        Objects.requireNonNull(entity, "entity");
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    @Override
    public T findById(ID id) {
        return getEntityManager().find(entityClass, Objects.requireNonNull(id, "id"));
    }

    @Override
    public List<T> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cargarRelaciones(root);
        cq.select(root);
        cq.orderBy(cb.asc(root.get(campoId)));
        return getEntityManager().createQuery(cq).getResultList();
    }

    @Override
    public List<T> findRange(int first, int max) {
        return findRange(first, max, null, List.of(), List.of());
    }

    @Override
    public List<T> findRange(int first, int max, String filtroGlobal) {
        return findRange(first, max, filtroGlobal, List.of(), List.of());
    }

    @Override
    public List<T> findRange(int first, int max, String filtroGlobal,
            List<OrdenConsulta> orden, List<FiltroConsulta> filtros) {
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("El inicio debe ser mayor o igual a cero y el tamaño debe ser positivo.");
        }
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cargarRelaciones(root);
        cq.select(root);
        aplicarFiltros(cb, cq, root, filtroGlobal, filtros);
        List<Order> ordenes = new ArrayList<>();
        boolean incluyeId = false;
        for (OrdenConsulta item : orden == null ? List.<OrdenConsulta>of() : orden) {
            Path<?> path = resolverRuta(root, item.campo());
            ordenes.add(item.ascendente() ? cb.asc(path) : cb.desc(path));
            incluyeId |= campoId.equals(item.campo());
        }
        if (!incluyeId) {
            ordenes.add(cb.asc(root.get(campoId)));
        }
        cq.orderBy(ordenes);
        return getEntityManager().createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
    }

    @Override
    public long count() {
        return count(null, List.of());
    }

    @Override
    public long count(String filtroGlobal) {
        return count(filtroGlobal, List.of());
    }

    @Override
    public long count(String filtroGlobal, List<FiltroConsulta> filtros) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);
        cq.select(cb.count(root));
        aplicarFiltros(cb, cq, root, filtroGlobal, filtros);
        return getEntityManager().createQuery(cq).getSingleResult();
    }

    /** Atributos de texto habilitados para la búsqueda global. */
    protected List<String> getCamposBusqueda() {
        return List.of();
    }

    /** Solo relaciones a uno: cargar colecciones aquí rompería la paginación SQL. */
    protected List<String> getRelacionesCarga() {
        return List.of();
    }

    private void cargarRelaciones(Root<T> root) {
        Map<String, FetchParent<?, ?>> cargas = new HashMap<>();
        for (String ruta : getRelacionesCarga()) {
            FetchParent<?, ?> parent = root;
            Class<?> tipo = entityClass;
            String prefijo = "";
            for (String nombre : ruta.split("\\.")) {
                Field atributo = atributo(tipo, nombre);
                if (!esRelacionUno(atributo)) {
                    throw new IllegalArgumentException("Solo se permite cargar relaciones a uno: " + ruta);
                }
                prefijo = prefijo.isEmpty() ? nombre : prefijo + "." + nombre;
                if (!cargas.containsKey(prefijo)) {
                    cargas.put(prefijo, parent.fetch(nombre, JoinType.LEFT));
                }
                parent = cargas.get(prefijo);
                tipo = atributo.getType();
            }
        }
    }

    public List<T> buscarParaAutocompletar(String filtro, int max) {
        String texto = normalizarFiltro(filtro);
        if (texto == null || texto.length() < 2 || getCamposBusqueda().isEmpty()) {
            return List.of();
        }
        return findRange(0, limitarAutocompletado(max), texto);
    }

    protected static int limitarAutocompletado(int max) {
        return Math.max(1, Math.min(50, max));
    }

    public static String normalizarFiltro(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String texto = valor.trim();
        return texto.substring(0, Math.min(texto.length(), MAX_FILTRO));
    }

    /** Escapa metacaracteres LIKE para que la búsqueda siempre sea literal. */
    public static String escaparLike(String valor) {
        return valor.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    protected static String patronBusqueda(String valor) {
        String texto = normalizarFiltro(valor);
        return "%" + escaparLike(texto == null ? "" : texto.toLowerCase(Locale.ROOT)) + "%";
    }

    private void aplicarFiltros(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<T> root,
            String global, List<FiltroConsulta> filtros) {
        List<Predicate> predicados = new ArrayList<>();
        if (normalizarFiltro(global) != null && !getCamposBusqueda().isEmpty()) {
            Predicate[] campos = getCamposBusqueda().stream()
                    .map(campo -> cb.like(cb.lower(resolverRuta(root, campo).as(String.class)), patronBusqueda(global), '\\'))
                    .toArray(Predicate[]::new);
            predicados.add(cb.or(campos));
        }
        for (FiltroConsulta filtro : filtros == null ? List.<FiltroConsulta>of() : filtros) {
            if (filtro.valor() == null || filtro.valor() instanceof String s && normalizarFiltro(s) == null) {
                continue;
            }
            predicados.add(predicadoColumna(cb, resolverRuta(root, filtro.campo()), filtro));
        }
        if (!predicados.isEmpty()) {
            cq.where(predicados.toArray(Predicate[]::new));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Predicate predicadoColumna(CriteriaBuilder cb, Path<?> path, FiltroConsulta filtro) {
        Object valor = filtro.valor();
        String modo = filtro.modo() == null ? "CONTAINS" : filtro.modo();
        if (List.of("CONTAINS", "NOT_CONTAINS", "STARTS_WITH", "ENDS_WITH", "EXACT").contains(modo)) {
            String texto = escaparLike(normalizarFiltro(valor.toString()).toLowerCase(Locale.ROOT));
            String patron = switch (modo) {
                case "STARTS_WITH" -> texto + "%";
                case "ENDS_WITH" -> "%" + texto;
                case "EXACT" -> texto;
                default -> "%" + texto + "%";
            };
            Predicate like = cb.like(cb.lower(path.as(String.class)), patron, '\\');
            return "NOT_CONTAINS".equals(modo) ? cb.not(like) : like;
        }
        return switch (modo) {
            case "EQUALS" -> valor instanceof String s
                    ? cb.equal(cb.lower(path.as(String.class)), s.trim().toLowerCase(Locale.ROOT)) : cb.equal(path, valor);
            case "NOT_EQUALS" -> valor instanceof String s
                    ? cb.notEqual(cb.lower(path.as(String.class)), s.trim().toLowerCase(Locale.ROOT)) : cb.notEqual(path, valor);
            case "LESS_THAN" -> cb.lessThan((Path) path, (Comparable) valor);
            case "LESS_THAN_EQUALS" -> cb.lessThanOrEqualTo((Path) path, (Comparable) valor);
            case "GREATER_THAN" -> cb.greaterThan((Path) path, (Comparable) valor);
            case "GREATER_THAN_EQUALS" -> cb.greaterThanOrEqualTo((Path) path, (Comparable) valor);
            case "IN" -> path.in(valor instanceof Collection<?> c ? c : List.of(valor));
            case "BETWEEN" -> {
                if (!(valor instanceof List<?> limites) || limites.size() != 2) {
                    throw new IllegalArgumentException("Un rango necesita dos límites.");
                }
                yield cb.between((Path) path, (Comparable) limites.get(0), (Comparable) limites.get(1));
            }
            default -> throw new IllegalArgumentException("Modo de filtro no soportado: " + modo);
        };
    }

    /** Solo admite atributos persistentes escalares y rutas de relaciones a uno. */
    private Path<?> resolverRuta(Root<T> root, String ruta) {
        if (ruta == null || !ruta.matches("[A-Za-z][A-Za-z0-9]*(\\.[A-Za-z][A-Za-z0-9]*)*")) {
            throw new IllegalArgumentException("Atributo de consulta no válido.");
        }
        Path<?> path = root;
        Class<?> tipo = entityClass;
        String[] partes = ruta.split("\\.");
        for (int i = 0; i < partes.length; i++) {
            Field field = atributo(tipo, partes[i]);
            if (i < partes.length - 1) {
                if (!esRelacionUno(field)) {
                    throw new IllegalArgumentException("La ruta debe atravesar relaciones a uno: " + ruta);
                }
                path = ((From<?, ?>) path).join(partes[i], JoinType.LEFT);
            } else {
                if (esRelacionUno(field) || Collection.class.isAssignableFrom(field.getType())) {
                    throw new IllegalArgumentException("El atributo debe ser escalar: " + ruta);
                }
                path = path.get(partes[i]);
            }
            tipo = field.getType();
        }
        return path;
    }

    private static boolean esRelacionUno(Field field) {
        return field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class);
    }

    private static Field atributo(Class<?> tipo, String nombre) {
        for (Class<?> actual = tipo; actual != null; actual = actual.getSuperclass()) {
            try {
                Field field = actual.getDeclaredField(nombre);
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())
                        || field.isAnnotationPresent(Transient.class)) {
                    break;
                }
                return field;
            } catch (NoSuchFieldException ignored) {
                // Los campos persistentes pueden provenir de una superclase mapeada.
            }
        }
        throw new IllegalArgumentException("Atributo persistente no válido: " + nombre);
    }

    private static String encontrarId(Class<?> tipo) {
        for (Class<?> actual = tipo; actual != null; actual = actual.getSuperclass()) {
            for (Field field : actual.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    return field.getName();
                }
            }
        }
        throw new IllegalArgumentException("La entidad debe declarar su identificador JPA.");
    }

    public boolean existePorCampo(String campo, String valor, ID excluirId) {
        if (valor == null || valor.isBlank()) {
            return false;
        }
        if (atributo(entityClass, campo).getType() != String.class) {
            throw new IllegalArgumentException("El atributo de unicidad debe ser de texto.");
        }
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);
        Predicate condicion = cb.equal(cb.lower(root.get(campo)), valor.trim().toLowerCase(Locale.ROOT));
        if (excluirId != null) {
            condicion = cb.and(condicion, cb.notEqual(root.get(campoId), excluirId));
        }
        cq.select(cb.count(root));
        cq.where(condicion);
        return getEntityManager().createQuery(cq).getSingleResult() > 0;
    }
}
