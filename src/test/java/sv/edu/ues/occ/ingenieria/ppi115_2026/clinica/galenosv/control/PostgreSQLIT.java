package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import jakarta.persistence.Persistence;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas reales: se ejecutan con mvn -Pintegration verify y requieren Docker. */
class PostgreSQLIT {
    private static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>("postgres:15")
            .withInitScript("db/clinica_ppi115_2026_08_20.sql");
    private static EntityManagerFactory factory;
    private static String url;
    private static String usuario;
    private static String password;
    private static boolean usaContenedor;

    @BeforeAll
    static void iniciar() throws Exception {
        url = System.getProperty("galenosv.test.jdbc.url");
        usaContenedor = url == null;
        if (usaContenedor) {
            DB.start();
            url = DB.getJdbcUrl(); usuario = DB.getUsername(); password = DB.getPassword();
        } else {
            if (!url.matches("jdbc:postgresql://127\\.0\\.0\\.1:[0-9]+/galenosv_test_[0-9]+")) {
                throw new IllegalArgumentException("La integración local requiere una base temporal galenosv_test_<numero> en loopback.");
            }
            usuario = System.getProperty("galenosv.test.jdbc.user", "galenosv_test");
            password = System.getProperty("galenosv.test.jdbc.password", "");
            try (var recurso = PostgreSQLIT.class.getResourceAsStream("/db/clinica_ppi115_2026_08_20.sql");
                 var c = DriverManager.getConnection(url, usuario, password); var s = c.createStatement()) {
                s.execute(new String(java.util.Objects.requireNonNull(recurso).readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
            }
        }
        factory = Persistence.createEntityManagerFactory("GalenoSV_TEST", Map.of(
                "jakarta.persistence.jdbc.url", url,
                "jakarta.persistence.jdbc.user", usuario,
                "jakarta.persistence.jdbc.password", password,
                "jakarta.persistence.jdbc.driver", "org.postgresql.Driver"));
    }

    @AfterAll
    static void cerrar() {
        if (factory != null) factory.close();
        if (usaContenedor) DB.stop();
    }

    private Connection conexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }

    @Test
    void todasLasEntidadesSeLeenSinColumnasAdicionales() throws SQLException {
        EntityManager em = factory.createEntityManager();
        try {
            assertEquals(20, em.getMetamodel().getEntities().size());
            for (var entidad : em.getMetamodel().getEntities()) {
                assertDoesNotThrow(() -> em.createQuery("SELECT e FROM " + entidad.getName() + " e", entidad.getJavaType())
                        .setMaxResults(1).getResultList(), entidad.getName());
            }
            try (var c = conexion(); var s = c.createStatement(); var r = s.executeQuery(
                    "SELECT count(*) FROM information_schema.columns WHERE table_schema='public' AND column_name='version'")) {
                assertTrue(r.next()); assertEquals(0, r.getInt(1));
            }
        } finally { em.close(); }
    }

    @Test
    void consultasPacientesResponsablesYPasoInicialUsanElEsquemaOriginal() {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            var clinica = new Clinica(UUID.randomUUID());
            clinica.setNombre("Central"); clinica.setActivo(true);
            var otra = new Clinica(UUID.randomUUID());
            otra.setNombre("Otra"); otra.setActivo(true);
            var paciente = new Rol(UUID.randomUUID());
            paciente.setNombre("Paciente"); paciente.setActivo(true);
            var doctor = new Rol(UUID.randomUUID());
            doctor.setNombre("Doctor"); doctor.setActivo(true);
            var inactivo = new Rol(UUID.randomUUID());
            inactivo.setNombre("Paciente inactivo"); inactivo.setActivo(false);
            Persona persona = new Persona(UUID.randomUUID()); persona.setNombres("Ana50%_"); persona.setApellidos("Prueba");
            for (Object o : List.of(clinica, otra, paciente, doctor, inactivo, persona)) em.persist(o);
            var pr = asignar(em, persona, paciente, clinica);
            asignar(em, persona, paciente, otra); asignar(em, persona, inactivo, clinica);
            var responsable = asignar(em, persona, doctor, clinica);
            var fecha = java.util.Date.from(java.time.Instant.parse("2026-01-03T06:00:00Z"));
            var consulta = new Consulta(UUID.randomUUID());
            consulta.setIdPersonaRol(pr); consulta.setFechaInicio(fecha); consulta.setReferenciaExterna("50%_"); em.persist(consulta);
            var procedimiento = new Procedimiento(UUID.randomUUID());
            procedimiento.setNombre("Prueba"); procedimiento.setActivo(true); em.persist(procedimiento);
            var inicio = paso(em, procedimiento, doctor, "Z Inicio"); var siguiente = paso(em, procedimiento, paciente, "A Siguiente");
            var secuencia = new ProcedimientoPasoSecuencia(UUID.randomUUID());
            secuencia.setIdProcedimientoPaso(inicio); secuencia.setIdProcedimientoPasoReferencia(siguiente.getIdProcedimientoPaso());
            secuencia.setTipoSecuencia("SIGUIENTE"); em.persist(secuencia);
            em.getTransaction().commit(); em.clear();
            PersonaRolDAO personas = new PersonaRolDAO(em); ConsultaDAO consultas = new ConsultaDAO(em);
            assertEquals(List.of(pr), personas.buscarPacientes("50%_", clinica.getIdClinica(), 20));
            assertEquals(responsable, personas.findResponsable(clinica.getIdClinica(), doctor.getIdRol()));
            assertEquals(inicio, new ProcedimientoPasoDAO(em).findPasoInicial(procedimiento.getIdProcedimiento()));
            assertEquals(List.of(consulta), consultas.findRangeFiltrado(0, 10, clinica.getIdClinica(), fecha, fecha, "50%_"));
            assertEquals(1, consultas.countFiltrado(clinica.getIdClinica(), null, null));
            assertEquals(0, consultas.countFiltrado(otra.getIdClinica(), null, null));
            assertTrue(consultas.findRangeFiltrado(0, 10, clinica.getIdClinica(), new java.util.Date(fecha.getTime()+1), null).isEmpty());
            assertTrue(consultas.findRangeFiltrado(0, 10, clinica.getIdClinica(), null, new java.util.Date(fecha.getTime()-1)).isEmpty());
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    private PersonaRol asignar(EntityManager em,
            Persona persona, Rol rol,
            Clinica clinica) {
        var pr = new PersonaRol(UUID.randomUUID());
        pr.setIdPersona(persona); pr.setIdRol(rol); pr.setIdClinica(clinica); em.persist(pr); return pr;
    }

    private ProcedimientoPaso paso(EntityManager em,
            Procedimiento procedimiento,
            Rol rol, String nombre) {
        var p = new ProcedimientoPaso(UUID.randomUUID());
        p.setNombre(nombre); p.setIdProcedimiento(procedimiento); p.setIdRol(rol); p.setIndicaFin(false); em.persist(p); return p;
    }

    @Test
    void esquemaOriginalPermiteNombresRepetidos() throws SQLException {
        String nombre = "Rol " + UUID.randomUUID();
        try (var c = conexion(); var q = c.prepareStatement("INSERT INTO rol (id_rol,nombre,activo) VALUES (?, ?, true)")) {
            q.setObject(1, UUID.randomUUID()); q.setString(2, nombre); q.executeUpdate();
            q.setObject(1, UUID.randomUUID()); q.setString(2, " " + nombre.toUpperCase(java.util.Locale.ROOT) + " ");
            assertEquals(1, q.executeUpdate());
        }
    }

    @Test
    void obligatoriosSeValidanEnLaAplicacionSinAgregarConstraints() throws SQLException {
        try (var c = conexion(); var q = c.prepareStatement("INSERT INTO rol (id_rol,nombre,activo) VALUES (?, ?, true)")) {
            q.setObject(1, UUID.randomUUID()); q.setString(2, "   ");
            assertEquals(1, q.executeUpdate());
            q.setString(2, null);
            q.setObject(1, UUID.randomUUID());
            assertEquals(1, q.executeUpdate());
        }
    }

    @Test
    void origenTieneFkYReferenciaConservaElEsquemaOriginal() throws SQLException {
        try (var c = conexion(); var q = c.prepareStatement("""
                INSERT INTO procedimiento_paso_secuencia
                (id_procedimiento_paso_secuencia,id_procedimiento_paso,id_procedimiento_paso_referencia,tipo_secuencia)
                VALUES (?, ?, ?, 'SIGUIENTE')
                """)) {
            UUID procedimiento = UUID.randomUUID(), paso = UUID.randomUUID();
            try (var p = c.prepareStatement("INSERT INTO procedimiento (id_procedimiento,nombre,activo) VALUES (?, ?, true)")) {
                p.setObject(1, procedimiento); p.setString(2, procedimiento.toString()); p.executeUpdate();
            }
            try (var p = c.prepareStatement("INSERT INTO procedimiento_paso (id_procedimiento_paso,nombre,indica_fin,id_procedimiento) VALUES (?, 'Inicio', false, ?)")) {
                p.setObject(1, paso); p.setObject(2, procedimiento); p.executeUpdate();
            }
            q.setObject(1, UUID.randomUUID()); q.setObject(2, paso); q.setObject(3, UUID.randomUUID());
            assertEquals(1, q.executeUpdate());
            q.setObject(1, UUID.randomUUID()); q.setObject(3, paso);
            assertEquals(1, q.executeUpdate());
        }
    }

    @Test
    void rangoDeFechasSeValidaSinModificarPostgresql() throws SQLException {
        try (var c = conexion(); var s = c.createStatement()) {
            UUID persona = UUID.randomUUID(), rol = UUID.randomUUID(), asignacion = UUID.randomUUID();
            s.executeUpdate("INSERT INTO persona (id_persona,nombres,apellidos) VALUES ('" + persona + "','Paciente','Prueba')");
            s.executeUpdate("INSERT INTO rol (id_rol,nombre,activo) VALUES ('" + rol + "','" + rol + "',true)");
            s.executeUpdate("INSERT INTO persona_rol (id_persona_rol,id_persona,id_rol) VALUES ('" + asignacion + "','" + persona + "','" + rol + "')");
            int creados = s.executeUpdate(
                    "INSERT INTO consulta (id_consulta,id_persona_rol,fecha_inicio,fecha_fin) VALUES ('"
                            + UUID.randomUUID() + "','" + asignacion + "','2026-01-02','2026-01-01')");
            assertEquals(1, creados);
            assertThrows(ValidacionNegocioException.class, () -> ValidadorComun.rangoFechas(
                    new java.util.Date(2000), new java.util.Date(1000)));
        }
    }

    @Test
    void paginacionYBusquedaLiteralFuncionanConJpa() {
        EntityManager em = factory.createEntityManager();
        String grupo = "Prueba " + UUID.randomUUID();
        try {
            em.getTransaction().begin();
            for (int i = 0; i < 6; i++) {
                Persona p = new Persona(UUID.randomUUID());
                p.setNombres(i == 0 ? "50%_ descuento" : "50XX descuento");
                p.setApellidos(grupo);
                em.persist(p);
            }
            em.getTransaction().commit();
            em.clear();
            PersonaDAO dao = new PersonaDAO(em);
            List<Persona> todos = dao.findRange(0, 6, grupo);
            List<Persona> primera = dao.findRange(0, 3, grupo);
            List<Persona> segunda = dao.findRange(3, 3, grupo);
            assertEquals(todos.subList(0, 3), primera);
            assertEquals(todos.subList(3, 6), segunda);
            assertEquals(1, dao.count("50%_"));
            assertEquals("50%_ descuento", dao.findRange(0, 10, "50%_").get(0).getNombres());
            assertNotNull(todos.get(0).getFechaCreacion());
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    @Test
    void dosEdicionesPersistenSinColumnaVersion() {
        EntityManager primero = factory.createEntityManager(), segundo = factory.createEntityManager();
        try {
            Persona p = new Persona(UUID.randomUUID());
            p.setNombres("Inicial"); p.setApellidos("Concurrencia");
            p.setFechaNacimiento(java.util.Date.from(java.time.Instant.parse("1990-01-01T06:00:00Z")));
            primero.getTransaction().begin(); primero.persist(p); primero.getTransaction().commit();
            Persona copia = segundo.find(Persona.class, p.getIdPersona());
            assertEquals(p.getFechaNacimiento(), copia.getFechaNacimiento());
            primero.getTransaction().begin(); p.setNombres("Primero"); primero.getTransaction().commit();
            segundo.getTransaction().begin(); copia.setNombres("Segundo");
            assertDoesNotThrow(segundo.getTransaction()::commit);
            primero.clear();
            assertEquals("Segundo", primero.find(Persona.class, p.getIdPersona()).getNombres());
        } finally {
            if (primero.getTransaction().isActive()) primero.getTransaction().rollback();
            if (segundo.getTransaction().isActive()) segundo.getTransaction().rollback();
            primero.close(); segundo.close();
        }
    }
}
