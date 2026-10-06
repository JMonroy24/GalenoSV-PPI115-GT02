package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoModelTest {

    @Mock
    private ProcedimientoDAO procedimientoDAO;

    @Mock private sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoDAO pasoDAO;
    @Mock private sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoSecuenciaDAO secuenciaDAO;
    @Mock private ProcedimientoPasoModel pasoModel;

    @InjectMocks
    private ProcedimientoModel procedimientoModel;

    @BeforeEach
    void setUp() {
        procedimientoModel.setProcedimientoDAO(procedimientoDAO);
    }

    @Test
    void testInitYCargarDatos() {
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        when(procedimientoDAO.findAll()).thenReturn(List.of(procedimiento));

        procedimientoModel.init();

        assertNotNull(procedimientoModel.getRegistros());
        assertEquals(1, procedimientoModel.getRegistros().size());
        verify(procedimientoDAO).findAll();
    }

    @Test
    void testPrepararNuevo() {
        procedimientoModel.prepararNuevo();

        assertNotNull(procedimientoModel.getRegistroActual());
        assertEquals(Estado.CREAR, procedimientoModel.getEstado());
        assertTrue(procedimientoModel.isEstadoCrear());
    }

    @Test
    void testSeleccionar() {
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());

        procedimientoModel.seleccionar(procedimiento);

        assertEquals(procedimiento, procedimientoModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, procedimientoModel.getEstado());
        assertTrue(procedimientoModel.isEstadoModificar());
    }

    @Test
    void testCancelar() {
        procedimientoModel.prepararNuevo();

        procedimientoModel.cancelar();

        assertNull(procedimientoModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, procedimientoModel.getEstado());
        assertTrue(procedimientoModel.isEstadoNinguno());
    }

    @Test
    void testGuardarCrear() {
        when(procedimientoDAO.findAll()).thenReturn(Collections.emptyList());

        procedimientoModel.prepararNuevo();
        procedimientoModel.getRegistroActual().setNombre("Toma de muestra sanguínea");

        procedimientoModel.guardar();

        verify(procedimientoDAO).create(any(Procedimiento.class));
        assertEquals(Estado.MODIFICAR, procedimientoModel.getEstado());
        assertNotNull(procedimientoModel.getRegistroActual());
    }

    @Test
    void testGuardarModificar() {
        when(procedimientoDAO.findAll()).thenReturn(Collections.emptyList());
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());

        procedimientoModel.seleccionar(procedimiento);
        procedimientoModel.guardar();

        verify(procedimientoDAO).update(procedimiento);
        assertEquals(Estado.MODIFICAR, procedimientoModel.getEstado());
    }

    @Test
    void testEliminar() {
        when(procedimientoDAO.findAll()).thenReturn(Collections.emptyList());
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());

        procedimientoModel.eliminar(procedimiento);

        verify(procedimientoDAO).delete(procedimiento);
    }

    @Test
    void testGettersYMetodosProtegidos() {
        assertEquals(procedimientoDAO, procedimientoModel.getDAO());
        assertEquals(procedimientoDAO, procedimientoModel.getProcedimientoDAO());
        assertNotNull(procedimientoModel.crearNuevoRegistro());
    }

    private sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso paso(String nombre) {
        var paso = new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso(UUID.randomUUID());
        paso.setNombre(nombre); return paso;
    }
    private sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia relacion(
            sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso padre,
            sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso hijo) {
        var s = new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia(UUID.randomUUID());
        s.setIdProcedimientoPaso(padre); s.setIdProcedimientoPasoReferencia(hijo.getIdProcedimientoPaso());
        s.setTipoSecuencia("SIGUIENTE"); return s;
    }
    @Test void arbolAnidadoOrdenadoExpandidoYConsultaUnica() {
        var procedimiento = new Procedimiento(UUID.randomUUID());
        var raiz = paso("Recepción"); var b = paso("B"); var a = paso("A"); var fin = paso("Fin");
        when(pasoDAO.findByProcedimiento(procedimiento.getIdProcedimiento())).thenReturn(List.of(b, fin, raiz, a));
        when(secuenciaDAO.findByProcedimiento(procedimiento.getIdProcedimiento()))
                .thenReturn(List.of(relacion(raiz, b), relacion(raiz, a), relacion(a, fin)));
        procedimientoModel.seleccionar(procedimiento);
        var arbol = procedimientoModel.getArbolPasos();
        assertEquals(1, arbol.getChildren().size());
        var nodo = arbol.getChildren().getFirst(); assertEquals(raiz, nodo.getData()); assertTrue(nodo.isExpanded());
        assertEquals(a, nodo.getChildren().getFirst().getData());
        assertEquals(fin, nodo.getChildren().getFirst().getChildren().getFirst().getData());
        assertEquals("Recepción", procedimientoModel.procedeDe(a));
        assertSame(arbol, procedimientoModel.getArbolPasos());
        verify(secuenciaDAO, times(1)).findByProcedimiento(procedimiento.getIdProcedimiento());
    }
    @Test void arbolToleraCiclosYHuerfanosSinPerderPasos() {
        var procedimiento = new Procedimiento(UUID.randomUUID());
        var a = paso("A"); var b = paso("B"); var huerfano = paso("Huérfano");
        when(pasoDAO.findByProcedimiento(procedimiento.getIdProcedimiento())).thenReturn(List.of(a, b, huerfano));
        when(secuenciaDAO.findByProcedimiento(procedimiento.getIdProcedimiento()))
                .thenReturn(List.of(relacion(a, b), relacion(b, a), relacion(paso("Ausente"), huerfano)));
        procedimientoModel.seleccionar(procedimiento);
        var arbol = procedimientoModel.getArbolPasos();
        assertEquals(2, arbol.getChildren().size());
        assertEquals(huerfano, arbol.getChildren().getFirst().getData());
        var ciclo = arbol.getChildren().get(1);
        assertEquals(a, ciclo.getData()); assertEquals(b, ciclo.getChildren().getFirst().getData());
        assertTrue(ciclo.getChildren().getFirst().getChildren().isEmpty());
    }
    @Test void guardarPasoRefrescaArbolYMantienePestana() {
        var procedimiento = new Procedimiento(UUID.randomUUID());
        procedimientoModel.seleccionar(procedimiento);
        var anterior = procedimientoModel.getArbolPasos();
        when(pasoModel.guardarPaso()).thenReturn(true);
        procedimientoModel.guardarPaso();
        assertNotSame(anterior, procedimientoModel.getArbolPasos());
        assertEquals(1, procedimientoModel.getPestanaActiva());
        verify(secuenciaDAO, times(2)).findByProcedimiento(procedimiento.getIdProcedimiento());
    }
}