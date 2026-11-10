package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoModelTest {

    @Mock
    private ProcedimientoService procedimientoService;

    @Mock private ProcedimientoPasoModel pasoModel;

    @InjectMocks
    private ProcedimientoModel procedimientoModel;

    @BeforeEach
    void setUp() {
        procedimientoModel.setProcedimientoService(procedimientoService);
    }

    @Test
    void inicializaLaPaginacionSinCargarElCatalogoCompleto() {
        procedimientoModel.init();

        assertNotNull(procedimientoModel.getLazyModel());
        verifyNoInteractions(procedimientoService);

        procedimientoModel.getLazyModel().load(0, 15, Map.of(), Map.of());
        verify(procedimientoService).findRange(0, 15, null, List.of(), List.of());
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

        procedimientoModel.prepararNuevo();
        procedimientoModel.getRegistroActual().setNombre("Toma de muestra sanguínea");

        procedimientoModel.guardar();

        verify(procedimientoService).create(any(Procedimiento.class));
        assertEquals(Estado.MODIFICAR, procedimientoModel.getEstado());
        assertNotNull(procedimientoModel.getRegistroActual());
    }

    @Test
    void testGuardarModificar() {
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());

        procedimientoModel.seleccionar(procedimiento);
        procedimientoModel.guardar();

        verify(procedimientoService).update(procedimiento);
        assertEquals(Estado.MODIFICAR, procedimientoModel.getEstado());
    }

    @Test
    void testEliminar() {
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());

        procedimientoModel.eliminar(procedimiento);

        verify(procedimientoService).delete(procedimiento);
    }

    @Test
    void testGettersYMetodosProtegidos() {
        assertEquals(procedimientoService, procedimientoModel.getDAO());
        assertEquals(procedimientoService, procedimientoModel.getProcedimientoService());
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
        when(procedimientoService.findPasosByProcedimiento(procedimiento.getIdProcedimiento())).thenReturn(List.of(b, fin, raiz, a));
        when(procedimientoService.findSecuenciasByProcedimiento(procedimiento.getIdProcedimiento()))
                .thenReturn(List.of(relacion(raiz, b), relacion(raiz, a), relacion(a, fin)));
        procedimientoModel.seleccionar(procedimiento);
        var arbol = procedimientoModel.getArbolPasos();
        assertEquals(1, arbol.getChildren().size());
        var nodo = arbol.getChildren().getFirst(); assertEquals(raiz, nodo.getData()); assertTrue(nodo.isExpanded());
        assertEquals(a, nodo.getChildren().getFirst().getData());
        assertEquals(fin, nodo.getChildren().getFirst().getChildren().getFirst().getData());
        assertEquals("Recepción", procedimientoModel.procedeDe(a));
        assertSame(arbol, procedimientoModel.getArbolPasos());
        verify(procedimientoService, times(1)).findSecuenciasByProcedimiento(procedimiento.getIdProcedimiento());
    }
    @Test void arbolToleraCiclosYHuerfanosSinPerderPasos() {
        var procedimiento = new Procedimiento(UUID.randomUUID());
        var a = paso("A"); var b = paso("B"); var huerfano = paso("Huérfano");
        when(procedimientoService.findPasosByProcedimiento(procedimiento.getIdProcedimiento())).thenReturn(List.of(a, b, huerfano));
        when(procedimientoService.findSecuenciasByProcedimiento(procedimiento.getIdProcedimiento()))
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
        verify(procedimientoService, times(2)).findSecuenciasByProcedimiento(procedimiento.getIdProcedimiento());
    }
}