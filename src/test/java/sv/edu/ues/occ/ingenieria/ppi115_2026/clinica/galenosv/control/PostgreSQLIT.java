package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import jakarta.persistence.Persistence;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;
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
        try (var c = DriverManager.getConnection(url, usuario, password); var s = c.createStatement()) {
            s.execute(java.nio.file.Files.readString(java.nio.file.Path.of(
                    "src/main/db/migration/V4__consulta_procedimiento_paso_valor.sql")));
            s.execute(java.nio.file.Files.readString(java.nio.file.Path.of(
                    "src/main/db/migration/V5__hu4_orden_examen_y_pasos.sql")));
            s.execute(java.nio.file.Files.readString(java.nio.file.Path.of(
                    "src/main/db/migration/V6__hu3_hu4_regularizar_pasos.sql")));
        }
        factory = Persistence.createEntityManagerFactory("GalenoSV_TEST", Map.of(
                "jakarta.persistence.jdbc.url", url,
                "jakarta.persistence.jdbc.user", usuario,
                "jakarta.persistence.jdbc.password", password,
                "jakarta.persistence.jdbc.driver", "org.postgresql.Driver"));
    }

    @org.junit.jupiter.api.BeforeEach
    void aislarDatosDeCadaPrueba() throws SQLException {
        try (var c = DriverManager.getConnection(url, usuario, password); var sentencia = c.createStatement()) {
            sentencia.execute("TRUNCATE TABLE public.persona, public.rol, public.clinica, public.procedimiento, "
                    + "public.examen, public.tipo_examen, public.tipo_documento CASCADE");
        }
    }

    @AfterAll
    static void cerrar() {
        if (factory != null) factory.close();
        if (usaContenedor) DB.stop();
    }

    private Connection conexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }

    private static void persistirFixture(EntityManager em, ConsultaProcedimientoPaso paso) {
        var pr = paso.getIdPersonaRol(); var cp = paso.getIdConsultaProcedimiento();
        for (Object entidad : List.of(pr.getIdClinica(), pr.getIdRol(), pr.getIdPersona(), pr,
                cp.getIdConsulta(), cp.getIdProcedimiento(), paso.getIdProcedimientoPaso(), cp, paso)) em.persist(entidad);
    }

    @Test
    void detalleConsultaLeePasosConRelacionOpcionalDespuesDeV5() {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            var consulta = new Consulta(UUID.randomUUID());
            var procedimiento = new Procedimiento(UUID.randomUUID());
            procedimiento.setNombre("Atención");
            var definicion = new ProcedimientoPaso(UUID.randomUUID());
            definicion.setNombre("Triaje");
            definicion.setIdProcedimiento(procedimiento);
            var asignado = new ConsultaProcedimiento(UUID.randomUUID());
            asignado.setIdConsulta(consulta);
            asignado.setIdProcedimiento(procedimiento);
            var registrado = new ConsultaProcedimientoPaso(UUID.randomUUID());
            registrado.setIdConsultaProcedimiento(asignado);
            registrado.setIdProcedimientoPaso(definicion);
            registrado.setEstado("PENDIENTE");
            registrado.setValor("{\"temperatura\":36.5}");
            for (Object entidad : List.of(consulta, procedimiento, definicion, asignado, registrado)) {
                em.persist(entidad);
            }
            em.flush();
            em.clear();

            var encontrada = new ConsultaService(new ConsultaDAO(em)).findById(consulta.getIdConsulta());
            assertNotNull(encontrada);
            var procedimientos = new ConsultaProcedimientoService(new ConsultaProcedimientoDAO(em))
                    .findByConsulta(encontrada.getIdConsulta());
            assertEquals(List.of(asignado), procedimientos);
            var pasos = new ConsultaProcedimientoPasoService(new ConsultaProcedimientoPasoDAO(em))
                    .findByConsultaProcedimiento(procedimientos.getFirst().getIdConsultaProcedimiento());
            assertEquals(1, pasos.size());
            var paso = pasos.stream().filter(registrado::equals).findFirst().orElseThrow();
            assertEquals("Triaje", paso.getIdProcedimientoPaso().getNombre());
            assertEquals(registrado.getValor(), paso.getValor());
            assertNotNull(paso.getIdProcedimientoPaso());
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    @Test
    void formularioJsonAmplioSePersisteYActualizaSinTruncar() {
        EntityManager em = factory.createEntityManager();
        try {
            var paso = FixturesClinicos.pasoValido();
            paso.setEstado("PENDIENTE");
            String valor = "{\"observaciones\":\"Presión arterial\",\"detalle\":\"" + "a".repeat(5000) + "\"}";
            paso.setValor(valor);
            em.getTransaction().begin();
            persistirFixture(em, paso);
            em.getTransaction().commit();
            em.clear();
            assertEquals(valor, em.find(ConsultaProcedimientoPaso.class, paso.getIdConsultaProcedimientoPaso()).getValor());

            String actualizado = "{\"observaciones\":\"  Sin cambios  \",\"detalle\":\"" + "b".repeat(6000) + "\"}";
            em.getTransaction().begin();
            new ConsultaProcedimientoPasoService(new ConsultaProcedimientoPasoDAO(em))
                    .actualizarFormulario(paso.getIdConsultaProcedimientoPaso(), p -> true, "COMPLETADO", actualizado);
            em.getTransaction().commit();
            em.clear();
            var guardado = em.find(ConsultaProcedimientoPaso.class, paso.getIdConsultaProcedimientoPaso());
            assertEquals("COMPLETADO", guardado.getEstado());
            assertEquals(actualizado, guardado.getValor());
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    @Test
    void personasFiltraDocumentoExactoDelMismoTipoSinDuplicar() {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            var tipo = new TipoDocumento(UUID.randomUUID());
            var otroTipo = new TipoDocumento(UUID.randomUUID());
            var persona = new Persona(UUID.randomUUID());
            persona.setNombres("Ana");
            var otraPersona = new Persona(UUID.randomUUID());
            for (Object entidad : List.of(tipo, otroTipo, persona, otraPersona)) em.persist(entidad);
            // La otra persona tiene el valor buscado y el tipo buscado en documentos distintos.
            for (var datos : List.of(
                    List.of(persona, tipo, "12345678-9"),
                    List.of(persona, otroTipo, "12345678-9"),
                    List.of(otraPersona, tipo, "otro"),
                    List.of(otraPersona, otroTipo, "12345678-9"))) {
                var documento = new Documento(UUID.randomUUID());
                documento.setIdPersona((Persona) datos.get(0));
                documento.setIdTipoDocumento((TipoDocumento) datos.get(1));
                documento.setValor((String) datos.get(2));
                em.persist(documento);
            }
            em.flush();
            var servicio = new PersonaService(new PersonaDAO(em));
            assertEquals(List.of(persona), servicio.findRange(0, 20, null, tipo.getIdTipoDocumento(), "12345678-9"));
            assertEquals(1L, servicio.count(null, tipo.getIdTipoDocumento(), "12345678-9"));
            assertEquals(List.of(persona), servicio.findRange(0, 20, "Ana", null, "12345678-9"));
            assertEquals(1L, servicio.count("Ana", null, "12345678-9"));
            for (String valor : List.of("inexistente", "12345678", "", " 12345678-9 ", "%")) {
                assertTrue(servicio.findRange(0, 20, null, tipo.getIdTipoDocumento(), valor).isEmpty());
                assertEquals(0L, servicio.count(null, tipo.getIdTipoDocumento(), valor));
            }
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
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
            var rolBreve = new Rol(UUID.randomUUID());
            rolBreve.setNombre("Q");
            rolBreve.setActivo(true);
            em.persist(rolBreve);
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
            var cp = new ConsultaProcedimiento(UUID.randomUUID());
            cp.setIdConsulta(consulta);
            cp.setIdProcedimiento(procedimiento);
            cp.setFechaInicio(fecha);
            em.persist(cp);
            var cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
            cpp.setIdConsultaProcedimiento(cp);
            cpp.setIdProcedimientoPaso(inicio);
            cpp.setIdPersonaRol(responsable);
            cpp.setFechaInicio(fecha);
            cpp.setEstado("PENDIENTE");
            em.persist(cpp);
            var orden = new OrdenExamen(UUID.randomUUID());
            orden.setIdConsultaProcedimientoPaso(cpp);
            orden.setFechaCreacion(fecha);
            orden.setIndicaciones("Preparación 50%_");
            em.persist(orden);
            var examenActivo = new Examen(UUID.randomUUID());
            examenActivo.setNombre("Hemograma activo");
            examenActivo.setActivo(true);
            em.persist(examenActivo);
            var examenInactivo = new Examen(UUID.randomUUID());
            examenInactivo.setNombre("Hemograma inactivo");
            examenInactivo.setActivo(false);
            em.persist(examenInactivo);
            var tipo = new TipoExamen(UUID.randomUUID());
            tipo.setNombre("Hematología");
            tipo.setActivo(true);
            em.persist(tipo);

            em.getTransaction().commit(); em.clear();
            PersonaRolDAO personas = new PersonaRolDAO(em); ConsultaDAO consultas = new ConsultaDAO(em);
            assertEquals(List.of(pr), personas.buscarPacientes("50%_", clinica.getIdClinica(), 20));
            assertEquals(List.of(consulta), consultas.buscarParaAutocompletar("ana50%_ prueba", 20));
            assertTrue(consultas.buscarParaAutocompletar("51%_", 20).isEmpty());
            assertEquals(List.of(consulta), consultas.buscarParaAutocompletar("50%_", 20, clinica.getIdClinica()));
            assertTrue(consultas.buscarParaAutocompletar("50%_", 20, otra.getIdClinica()).isEmpty());
            assertTrue(consultas.buscarParaAutocompletar(null, 20, clinica.getIdClinica()).isEmpty());
            assertTrue(consultas.buscarParaAutocompletar("  ", 20, clinica.getIdClinica()).isEmpty());
            assertTrue(personas.existeAsignacion(persona.getIdPersona(), paciente.getIdRol(), clinica.getIdClinica(), null));
            assertFalse(personas.existeAsignacion(persona.getIdPersona(), paciente.getIdRol(), clinica.getIdClinica(), pr.getIdPersonaRol()));
            assertFalse(personas.existeAsignacion(persona.getIdPersona(), paciente.getIdRol(), null, null));
            assertFalse(personas.existeAsignacion(persona.getIdPersona(), paciente.getIdRol(), null, pr.getIdPersonaRol()));
            assertEquals(List.of(persona), new PersonaDAO(em).buscarParaAutocompletar("ANA50%_ Prueba", 20));
            assertEquals(List.of(responsable), personas.buscarParaAutocompletar("Prueba Doctor", 20));
            assertEquals(List.of(cp), new ConsultaProcedimientoDAO(em).buscarParaAutocompletar("Ana50%_ Prueba", 20));
            assertEquals(List.of(cpp), new ConsultaProcedimientoPasoDAO(em).buscarParaAutocompletar("pendiente", 20));
            assertEquals(List.of(orden), new OrdenExamenDAO(em).buscarParaAutocompletar("50%_", 20));
            assertEquals(List.of(orden), new OrdenExamenDAO(em).buscarParaAutocompletar("prueba", 20));
            assertEquals(List.of(doctor), new RolDAO(em).buscarParaAutocompletar("doctor", 20));
            assertEquals(List.of(rolBreve), new RolDAO(em).buscarParaAutocompletar("q", 20));
            assertEquals(List.of(procedimiento), new ProcedimientoDAO(em).buscarParaAutocompletar("prueba", 20));
            assertEquals(List.of(examenActivo), new ExamenDAO(em).buscarParaAutocompletar("hemograma", 20));
            assertEquals(List.of(tipo), new TipoExamenDAO(em).buscarParaAutocompletar("hematología", 20));
            assertEquals(responsable, personas.findResponsable(clinica.getIdClinica(), doctor.getIdRol()));
            var pasos = new ProcedimientoPasoDAO(em);
            assertEquals(inicio, pasos.findPasoInicial(procedimiento.getIdProcedimiento()));
            assertEquals(List.of(inicio), pasos.findRange(0, 20, "Z Inicio"));
            assertEquals(List.of(secuencia), new ProcedimientoPasoSecuenciaDAO(em).findRange(0, 20, "Z Inicio"));
            var idProcedimiento = procedimiento.getIdProcedimiento();
            var idPaso = inicio.getIdProcedimientoPaso();
            var pasoSinExamenes = pasos.findByProcedimiento(idProcedimiento).stream()
                    .filter(inicio::equals).findFirst().orElseThrow();
            assertTrue(pasoSinExamenes.getProcedimientoPasoExamenList().isEmpty());

            em.getTransaction().begin();
            var relacionExamen = new ProcedimientoPasoExamen(UUID.randomUUID());
            relacionExamen.setIdProcedimientoPaso(em.getReference(ProcedimientoPaso.class, idPaso));
            relacionExamen.setIdExamen(em.getReference(Examen.class, examenActivo.getIdExamen()));
            relacionExamen.setActivo(true);
            em.persist(relacionExamen);
            em.getTransaction().commit();
            em.clear();
            var pasoRefrescado = pasos.findByProcedimiento(idProcedimiento).stream()
                    .filter(inicio::equals).findFirst().orElseThrow();
            assertEquals(List.of(relacionExamen), pasoRefrescado.getProcedimientoPasoExamenList());
            var asociaciones = new ProcedimientoPasoExamenDAO(em);
            assertTrue(asociaciones.existePasoExamen(idPaso, examenActivo.getIdExamen()));
            assertFalse(asociaciones.existePasoExamen(idPaso, examenActivo.getIdExamen(), relacionExamen.getIdProcedimientoPasoExamen()));
            assertEquals(List.of(relacionExamen), asociaciones.findByPaso(idPaso));
            assertTrue(asociaciones.findByPaso(siguiente.getIdProcedimientoPaso()).isEmpty());

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
        PersonaDAO dao = new PersonaDAO(em);
        PersonaService personas = new PersonaService(dao);
        try {
            em.getTransaction().begin();
            for (int i = 0; i < 6; i++) {
                Persona p = new Persona(UUID.randomUUID());
                p.setNombres(i == 0 ? "50%_ descuento" : "50XX descuento");
                p.setApellidos(grupo);
                personas.create(p);
            }
            em.getTransaction().commit();
            em.clear();
            List<Persona> todos = dao.findRange(0, 6, grupo);
            List<Persona> primera = dao.findRange(0, 3, grupo);
            List<Persona> segunda = dao.findRange(3, 3, grupo);
            assertEquals(todos.subList(0, 3), primera);
            assertEquals(todos.subList(3, 6), segunda);
            assertEquals(1, dao.count("50%_ descuento"));
            assertEquals("50%_ descuento", dao.findRange(0, 10, "50%_ descuento").get(0).getNombres());
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
    private record Flujo(UUID consulta, UUID inicial, UUID segundo, UUID ultimo, UUID clinica,
            UUID rolInicial, UUID rolSegundo, UUID rolUltimo, String referencia) { }

    private Flujo crearFlujo(EntityManager em) {
        var primero = FixturesClinicos.pasoValido();
        var cp = primero.getIdConsultaProcedimiento();
        String referencia = "HU34-" + UUID.randomUUID();
        cp.getIdConsulta().setReferenciaExterna(referencia);
        var inicio = primero.getIdProcedimientoPaso(); inicio.setIndicaFin(false); inicio.setNombre("Recepción");
        persistirFixture(em, primero);
        var definiciones = new java.util.ArrayList<ProcedimientoPaso>(); definiciones.add(inicio);
        for (String nombre : List.of("Enfermería", "Doctor")) {
            var rol = new Rol(UUID.randomUUID()); rol.setNombre(nombre); rol.setActivo(true); em.persist(rol);
            var pr = new PersonaRol(UUID.randomUUID()); pr.setIdRol(rol); pr.setIdClinica(primero.getIdPersonaRol().getIdClinica());
            pr.setIdPersona(primero.getIdPersonaRol().getIdPersona()); em.persist(pr);
            var definicion = new ProcedimientoPaso(UUID.randomUUID()); definicion.setNombre(nombre);
            definicion.setIdRol(rol); definicion.setIdProcedimiento(cp.getIdProcedimiento());
            definicion.setIndicaFin("Doctor".equals(nombre)); em.persist(definicion);
            var secuencia = new ProcedimientoPasoSecuencia(UUID.randomUUID());
            secuencia.setIdProcedimientoPaso(definiciones.getLast()); secuencia.setIdProcedimientoPasoReferencia(definicion.getIdProcedimientoPaso());
            secuencia.setTipoSecuencia("SIGUIENTE"); em.persist(secuencia); definiciones.add(definicion);
        }
        em.flush();
        return new Flujo(cp.getIdConsulta().getIdConsulta(), primero.getIdConsultaProcedimientoPaso(),
                definiciones.get(1).getIdProcedimientoPaso(), definiciones.get(2).getIdProcedimientoPaso(),
                primero.getIdPersonaRol().getIdClinica().getIdClinica(), inicio.getIdRol().getIdRol(),
                definiciones.get(1).getIdRol().getIdRol(), definiciones.get(2).getIdRol().getIdRol(), referencia);
    }

    private ConsultaProcedimientoPaso registro(EntityManager em, UUID consulta, UUID definicion) {
        return em.createQuery("SELECT p FROM ConsultaProcedimientoPaso p WHERE p.idConsultaProcedimiento.idConsulta.idConsulta = :consulta"
                + " AND p.idProcedimientoPaso.idProcedimientoPaso = :definicion", ConsultaProcedimientoPaso.class)
                .setParameter("consulta", consulta).setParameter("definicion", definicion).getSingleResult();
    }

    @Test void flujoCompletoRetrocedeReactivaSinDuplicarYCierraConsulta() {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin(); var flujo = crearFlujo(em); em.getTransaction().commit(); em.clear();
            var servicio = new ConsultaProcedimientoPasoService(new ConsultaProcedimientoPasoDAO(em));
            em.getTransaction().begin();
            var inicio = servicio.actualizarFormulario(flujo.inicial(), p -> true, "COMPLETADO", "recepción");
            Date finInicial = inicio.getFechaFin(); assertNotNull(finInicial);
            var enfermeria = registro(em, flujo.consulta(), flujo.segundo());
            assertEquals("EN_CURSO", enfermeria.getEstado()); assertEquals(flujo.rolSegundo(), enfermeria.getIdPersonaRol().getIdRol().getIdRol());
            servicio.actualizarFormulario(flujo.inicial(), p -> true, "COMPLETADO", "corregido");
            assertEquals(finInicial, inicio.getFechaFin());
            servicio.retroceder(enfermeria.getIdConsultaProcedimientoPaso(), p -> true, "formulario conservado");
            assertEquals("EN_CURSO", inicio.getEstado()); assertEquals("PENDIENTE", enfermeria.getEstado());
            servicio.actualizarFormulario(flujo.inicial(), p -> true, "COMPLETADO", "recepción revisada");
            assertEquals(finInicial, inicio.getFechaFin());
            assertEquals(enfermeria.getIdConsultaProcedimientoPaso(), registro(em, flujo.consulta(), flujo.segundo()).getIdConsultaProcedimientoPaso());
            servicio.actualizarFormulario(enfermeria.getIdConsultaProcedimientoPaso(), p -> true, "COMPLETADO", "triaje");
            var doctor = registro(em, flujo.consulta(), flujo.ultimo());
            servicio.actualizarFormulario(doctor.getIdConsultaProcedimientoPaso(), p -> true, "COMPLETADO", "diagnóstico");
            em.getTransaction().commit(); em.clear();
            var consulta = em.find(Consulta.class, flujo.consulta()); assertNotNull(consulta.getFechaFin());
            assertNotNull(em.find(ConsultaProcedimientoPaso.class, doctor.getIdConsultaProcedimientoPaso()).getFechaFin());
            assertEquals(3L, em.createQuery("SELECT COUNT(p) FROM ConsultaProcedimientoPaso p WHERE p.idConsultaProcedimiento.idConsulta = :consulta", Long.class)
                    .setParameter("consulta", consulta).getSingleResult());
            em.getTransaction().begin();
            servicio.retroceder(doctor.getIdConsultaProcedimientoPaso(), p -> true, "volver");
            em.getTransaction().commit(); em.clear();
            assertNull(em.find(Consulta.class, flujo.consulta()).getFechaFin());
            assertEquals("EN_CURSO", registro(em, flujo.consulta(), flujo.segundo()).getEstado());
        } finally { if (em.getTransaction().isActive()) em.getTransaction().rollback(); em.close(); }
    }

    @Test void filtrosCombinadosPaginanYConteoNoDuplicaPorProcedimientos() {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin(); var flujo = crearFlujo(em);
            var original = em.find(ConsultaProcedimientoPaso.class, flujo.inicial());
            var segundoProcedimiento = new ConsultaProcedimiento(UUID.randomUUID());
            segundoProcedimiento.setIdConsulta(original.getIdConsultaProcedimiento().getIdConsulta());
            segundoProcedimiento.setIdProcedimiento(original.getIdConsultaProcedimiento().getIdProcedimiento()); em.persist(segundoProcedimiento);
            var repetido = new ConsultaProcedimientoPaso(UUID.randomUUID()); repetido.setIdConsultaProcedimiento(segundoProcedimiento);
            repetido.setIdProcedimientoPaso(original.getIdProcedimientoPaso()); repetido.setIdPersonaRol(original.getIdPersonaRol());
            repetido.setEstado("PENDIENTE"); repetido.setFechaInicio(new Date(200)); em.persist(repetido);
            em.flush();
            var dao = new ConsultaDAO(em);
            var filtro = new FiltroConsultasRest(original.getIdConsultaProcedimiento().getIdConsulta().getIdPersonaRol().getIdPersonaRol(),
                    flujo.clinica(), flujo.rolInicial(), new Date(100), new Date(100), flujo.referencia());
            assertEquals(1, dao.countRest(filtro)); assertEquals(1, dao.findRangeRest(0, 1, filtro).size());
            assertTrue(dao.findRangeRest(1, 1, filtro).isEmpty());
            assertEquals(0, dao.countRest(new FiltroConsultasRest(null, UUID.randomUUID(), null, null, null, flujo.referencia())));
            assertEquals(0, dao.countRest(new FiltroConsultasRest(null, flujo.clinica(), flujo.rolSegundo(), null, null, flujo.referencia())));
            assertEquals(0, dao.countRest(new FiltroConsultasRest(null, flujo.clinica(), null, new Date(101), null, flujo.referencia())));
            assertEquals(0, dao.countRest(new FiltroConsultasRest(null, flujo.clinica(), null, null, new Date(99), flujo.referencia())));
            assertEquals(0, dao.countRest(new FiltroConsultasRest(null, flujo.clinica(), null, null, null, "inexistente")));
            var servicio = new ConsultaProcedimientoPasoService(new ConsultaProcedimientoPasoDAO(em));
            servicio.actualizarFormulario(flujo.inicial(), p -> true, "COMPLETADO", "");
            assertEquals(1, dao.countRest(new FiltroConsultasRest(null, flujo.clinica(), flujo.rolSegundo(), null, null, flujo.referencia())));
            assertEquals(1, dao.findRangeRest(0, 1, new FiltroConsultasRest(null, flujo.clinica(), flujo.rolSegundo(), null, null, flujo.referencia())).size());
        } finally { if (em.getTransaction().isActive()) em.getTransaction().rollback(); em.close(); }
    }

    @Test void dosPatchesConMismoEtagSoloConfirmanUnaTransicion() throws Exception {
        Flujo flujo;
        String etag;
        try (var em = factory.createEntityManager()) {
            em.getTransaction().begin(); flujo = crearFlujo(em); em.getTransaction().commit();
            etag = sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(
                    new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaProcedimientoPasoMapper()
                            .toDTO(em.find(ConsultaProcedimientoPaso.class, flujo.inicial())));
        }
        var barrera = new java.util.concurrent.CyclicBarrier(2);
        try (var executor = java.util.concurrent.Executors.newFixedThreadPool(2)) {
            var tareas = new java.util.ArrayList<java.util.concurrent.Future<Boolean>>();
            for (int i = 0; i < 2; i++) tareas.add(executor.submit(() -> {
                try (var em = factory.createEntityManager()) {
                    em.getTransaction().begin(); barrera.await(10, java.util.concurrent.TimeUnit.SECONDS);
                    try {
                        new ConsultaProcedimientoPasoService(new ConsultaProcedimientoPasoDAO(em)).actualizarFormulario(flujo.inicial(),
                                p -> sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.coincide(etag,
                                        sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.ETagUtils.calcular(
                                                new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaProcedimientoPasoMapper().toDTO(p))),
                                "COMPLETADO", "concurrente");
                        em.getTransaction().commit(); return true;
                    } catch (sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException e) {
                        em.getTransaction().rollback(); return false;
                    }
                }
            }));
            int confirmadas = 0;
            for (var tarea : tareas) if (tarea.get(20, java.util.concurrent.TimeUnit.SECONDS)) confirmadas++;
            assertEquals(1, confirmadas);
        }
        try (var em = factory.createEntityManager()) {
            assertNotNull(registro(em, flujo.consulta(), flujo.segundo()));
            assertEquals(2L, em.createQuery("SELECT COUNT(p) FROM ConsultaProcedimientoPaso p WHERE p.idConsultaProcedimiento.idConsulta.idConsulta = :id", Long.class)
                    .setParameter("id", flujo.consulta()).getSingleResult());
        }
    }

    @Test void faltaDeResponsableActivoRevierteCompletarYNoCreaSucesor() {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin(); var flujo = crearFlujo(em);
            em.find(Rol.class, flujo.rolSegundo()).setActivo(false);
            em.getTransaction().commit(); em.clear();
            em.getTransaction().begin();
            assertThrows(ValidacionNegocioException.class, () -> new ConsultaProcedimientoPasoService(new ConsultaProcedimientoPasoDAO(em))
                    .actualizarFormulario(flujo.inicial(), p -> true, "COMPLETADO", ""));
            em.getTransaction().rollback(); em.clear();
            assertEquals("PENDIENTE", em.find(ConsultaProcedimientoPaso.class, flujo.inicial()).getEstado());
            assertNull(em.find(ConsultaProcedimientoPaso.class, flujo.inicial()).getFechaFin());
            assertTrue(em.createQuery("SELECT p FROM ConsultaProcedimientoPaso p WHERE p.idProcedimientoPaso.idProcedimientoPaso = :id", ConsultaProcedimientoPaso.class)
                    .setParameter("id", flujo.segundo()).getResultList().isEmpty());
        } finally { if (em.getTransaction().isActive()) em.getTransaction().rollback(); em.close(); }
    }

    @Test void editarEjecucionHistoricaNoAdelantaLaEjecucionActiva() {
        try (var em = factory.createEntityManager()) {
            em.getTransaction().begin(); var flujo = crearFlujo(em);
            var activo = em.find(ConsultaProcedimientoPaso.class, flujo.inicial());
            var historico = new ConsultaProcedimientoPaso(UUID.randomUUID());
            historico.setIdConsultaProcedimiento(activo.getIdConsultaProcedimiento());
            historico.setIdProcedimientoPaso(activo.getIdProcedimientoPaso()); historico.setIdPersonaRol(activo.getIdPersonaRol());
            historico.setFechaInicio(new Date(100)); historico.setFechaFin(new Date(150)); historico.setEstado("COMPLETADO");
            em.persist(historico); em.getTransaction().commit(); em.clear();
            em.getTransaction().begin();
            new ConsultaProcedimientoPasoService(new ConsultaProcedimientoPasoDAO(em))
                    .actualizarFormulario(historico.getIdConsultaProcedimientoPaso(), p -> true, "COMPLETADO", "nota histórica");
            em.getTransaction().commit(); em.clear();
            assertEquals("PENDIENTE", em.find(ConsultaProcedimientoPaso.class, flujo.inicial()).getEstado());
            assertTrue(em.createQuery("SELECT p FROM ConsultaProcedimientoPaso p WHERE p.idProcedimientoPaso.idProcedimientoPaso = :id", ConsultaProcedimientoPaso.class)
                    .setParameter("id", flujo.segundo()).getResultList().isEmpty());
        }
    }

    @Test void altaAdjuntaRelacionesSeparadasAntesDeBloquearConsulta() {
        try (var em = factory.createEntityManager()) {
            var paso = FixturesClinicos.pasoValido();
            em.getTransaction().begin(); persistirFixture(em, paso); em.remove(paso); em.getTransaction().commit(); em.clear();
            var nuevo = new ConsultaProcedimientoPaso(UUID.randomUUID());
            nuevo.setIdConsultaProcedimiento(paso.getIdConsultaProcedimiento()); nuevo.setIdProcedimientoPaso(paso.getIdProcedimientoPaso());
            nuevo.setIdPersonaRol(paso.getIdPersonaRol()); nuevo.setEstado("PENDIENTE");
            em.getTransaction().begin();
            new ConsultaProcedimientoPasoService(new ConsultaProcedimientoPasoDAO(em)).crear(nuevo);
            em.getTransaction().commit(); em.clear();
            assertEquals(paso.getIdProcedimientoPaso(), em.find(ConsultaProcedimientoPaso.class, nuevo.getIdConsultaProcedimientoPaso()).getIdProcedimientoPaso());
        }
    }

}
