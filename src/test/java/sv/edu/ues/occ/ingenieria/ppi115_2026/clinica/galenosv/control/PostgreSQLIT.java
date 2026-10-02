package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Persistence;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas reales: se ejecutan con mvn -Pintegration verify y requieren Docker. */
class PostgreSQLIT {
    private static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>("postgres:15");
    private static EntityManagerFactory factory;

    @BeforeAll
    static void iniciar() {
        DB.start();
        Flyway.configure().dataSource(DB.getJdbcUrl(), DB.getUsername(), DB.getPassword())
                .locations("classpath:db/migration").load().migrate();
        factory = Persistence.createEntityManagerFactory("GalenoSV_TEST", Map.of(
                "jakarta.persistence.jdbc.url", DB.getJdbcUrl(),
                "jakarta.persistence.jdbc.user", DB.getUsername(),
                "jakarta.persistence.jdbc.password", DB.getPassword(),
                "jakarta.persistence.jdbc.driver", "org.postgresql.Driver"));
    }

    @AfterAll
    static void cerrar() {
        if (factory != null) factory.close();
        DB.stop();
    }

    private Connection conexion() throws SQLException {
        return DriverManager.getConnection(DB.getJdbcUrl(), DB.getUsername(), DB.getPassword());
    }

    @Test
    void indiceUnicoIgnoraMayusculasYEspacios() throws SQLException {
        String nombre = "Rol " + UUID.randomUUID();
        try (var c = conexion(); var q = c.prepareStatement("INSERT INTO rol (id_rol,nombre,activo) VALUES (?, ?, true)")) {
            q.setObject(1, UUID.randomUUID()); q.setString(2, nombre); q.executeUpdate();
            q.setObject(1, UUID.randomUUID()); q.setString(2, " " + nombre.toUpperCase(java.util.Locale.ROOT) + " ");
            assertEquals("23505", assertThrows(SQLException.class, q::executeUpdate).getSQLState());
        }
    }

    @Test
    void baseRechazaTextosVaciosYNulos() throws SQLException {
        try (var c = conexion(); var q = c.prepareStatement("INSERT INTO rol (id_rol,nombre,activo) VALUES (?, ?, true)")) {
            q.setObject(1, UUID.randomUUID()); q.setString(2, "   ");
            assertEquals("23514", assertThrows(SQLException.class, q::executeUpdate).getSQLState());
            q.setString(2, null);
            assertEquals("23502", assertThrows(SQLException.class, q::executeUpdate).getSQLState());
        }
    }

    @Test
    void referenciaDeSecuenciaDebeExistir() throws SQLException {
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
            assertEquals("23503", assertThrows(SQLException.class, q::executeUpdate).getSQLState());
            q.setObject(3, paso);
            assertEquals("23514", assertThrows(SQLException.class, q::executeUpdate).getSQLState());
        }
    }

    @Test
    void rangoDeFechasSeProtegeEnPostgresql() throws SQLException {
        try (var c = conexion(); var s = c.createStatement()) {
            UUID persona = UUID.randomUUID(), rol = UUID.randomUUID(), asignacion = UUID.randomUUID();
            s.executeUpdate("INSERT INTO persona (id_persona,nombres,apellidos) VALUES ('" + persona + "','Paciente','Prueba')");
            s.executeUpdate("INSERT INTO rol (id_rol,nombre,activo) VALUES ('" + rol + "','" + rol + "',true)");
            s.executeUpdate("INSERT INTO persona_rol (id_persona_rol,id_persona,id_rol) VALUES ('" + asignacion + "','" + persona + "','" + rol + "')");
            SQLException error = assertThrows(SQLException.class, () -> s.executeUpdate(
                    "INSERT INTO consulta (id_consulta,id_persona_rol,fecha_inicio,fecha_fin) VALUES ('"
                            + UUID.randomUUID() + "','" + asignacion + "','2026-01-02','2026-01-01')"));
            assertEquals("23514", error.getSQLState());
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
    void dosEdicionesConcurrentesProducenConflicto() {
        EntityManager primero = factory.createEntityManager(), segundo = factory.createEntityManager();
        try {
            Persona p = new Persona(UUID.randomUUID());
            p.setNombres("Inicial"); p.setApellidos("Concurrencia");
            primero.getTransaction().begin(); primero.persist(p); primero.getTransaction().commit();
            Persona copia = segundo.find(Persona.class, p.getIdPersona());
            primero.getTransaction().begin(); p.setNombres("Primero"); primero.getTransaction().commit();
            segundo.getTransaction().begin(); copia.setNombres("Segundo");
            assertThrows(OptimisticLockException.class, segundo::flush);
        } finally {
            if (primero.getTransaction().isActive()) primero.getTransaction().rollback();
            if (segundo.getTransaction().isActive()) segundo.getTransaction().rollback();
            primero.close(); segundo.close();
        }
    }
}
