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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del modelo JSF que administra las asociaciones entre los
 * pasos de los procedimientos y los exámenes clínicos.
 *
 * Las pruebas aíslan el comportamiento del Model mediante un DAO simulado.
 * Se verifican la carga inicial de registros, los cambios de estado del
 * formulario y la delegación de las operaciones CRUD.
 */
@ExtendWith(MockitoExtension.class)
public class ProcedimientoPasoExamenModelTest {

    @Mock
    private ProcedimientoPasoExamenDAO procedimientoPasoExamenDAO;

    @InjectMocks
    private ProcedimientoPasoExamenModel procedimientoPasoExamenModel;

    /**
     * Inyecta el DAO simulado en el Model antes de ejecutar cada prueba.
     */
    @BeforeEach
    public void setUp() {
        procedimientoPasoExamenModel.setProcedimientoPasoExamenDAO(
                procedimientoPasoExamenDAO);
    }

    /**
     * Verifica que la inicialización consulte el DAO y cargue las asociaciones
     * existentes en la colección utilizada por la vista.
     */
    @Test
    public void testInitYCargarDatos() {
        ProcedimientoPasoExamen asociacion =
                new ProcedimientoPasoExamen(UUID.randomUUID());

        when(procedimientoPasoExamenDAO.findAll())
                .thenReturn(List.of(asociacion));

        procedimientoPasoExamenModel.init();

        assertNotNull(procedimientoPasoExamenModel.getRegistros());
        assertEquals(
                1,
                procedimientoPasoExamenModel.getRegistros().size()
        );
        verify(procedimientoPasoExamenDAO).findAll();
    }

    /**
     * Verifica que la preparación de un registro nuevo cree una asociación
     * vacía y coloque el formulario en estado de creación.
     */
    @Test
    public void testPrepararNuevo() {
        procedimientoPasoExamenModel.prepararNuevo();

        assertNotNull(
                procedimientoPasoExamenModel.getRegistroActual()
        );
        assertEquals(
                Estado.CREAR,
                procedimientoPasoExamenModel.getEstado()
        );
        assertTrue(procedimientoPasoExamenModel.isEstadoCrear());
    }

    /**
     * Verifica que seleccionar una asociación la convierta en el registro
     * actual y habilite el estado de modificación.
     */
    @Test
    public void testSeleccionar() {
        ProcedimientoPasoExamen asociacion =
                new ProcedimientoPasoExamen(UUID.randomUUID());

        procedimientoPasoExamenModel.seleccionar(asociacion);

        assertEquals(
                asociacion,
                procedimientoPasoExamenModel.getRegistroActual()
        );
        assertEquals(
                Estado.MODIFICAR,
                procedimientoPasoExamenModel.getEstado()
        );
        assertTrue(procedimientoPasoExamenModel.isEstadoModificar());
    }

    /**
     * Verifica que cancelar una operación descarte el registro actual y
     * devuelva el formulario a su estado inicial.
     */
    @Test
    public void testCancelar() {
        procedimientoPasoExamenModel.prepararNuevo();

        procedimientoPasoExamenModel.cancelar();

        assertNull(procedimientoPasoExamenModel.getRegistroActual());
        assertEquals(
                Estado.NINGUNO,
                procedimientoPasoExamenModel.getEstado()
        );
        assertTrue(procedimientoPasoExamenModel.isEstadoNinguno());
    }

    /**
     * Verifica que guardar durante una creación delegue la persistencia al DAO
     * y limpie el estado del formulario después de completar la operación.
     */
    @Test
    public void testGuardarCrear() {
        when(procedimientoPasoExamenDAO.findAll())
                .thenReturn(Collections.emptyList());

        procedimientoPasoExamenModel.prepararNuevo();
        procedimientoPasoExamenModel
                .getRegistroActual()
                .setObservaciones("Examen requerido por el paso");

        procedimientoPasoExamenModel.guardar();

        verify(procedimientoPasoExamenDAO)
                .create(any(ProcedimientoPasoExamen.class));
        assertEquals(
                Estado.NINGUNO,
                procedimientoPasoExamenModel.getEstado()
        );
        assertNull(procedimientoPasoExamenModel.getRegistroActual());
    }

    /**
     * Verifica que guardar un registro seleccionado invoque la actualización
     * del DAO y finalice el modo de modificación.
     */
    @Test
    public void testGuardarModificar() {
        when(procedimientoPasoExamenDAO.findAll())
                .thenReturn(Collections.emptyList());

        ProcedimientoPasoExamen asociacion =
                new ProcedimientoPasoExamen(UUID.randomUUID());

        procedimientoPasoExamenModel.seleccionar(asociacion);
        procedimientoPasoExamenModel.guardar();

        verify(procedimientoPasoExamenDAO).update(asociacion);
        assertEquals(
                Estado.NINGUNO,
                procedimientoPasoExamenModel.getEstado()
        );
    }

    /**
     * Verifica que eliminar una asociación delegue la operación al DAO.
     */
    @Test
    public void testEliminar() {
        when(procedimientoPasoExamenDAO.findAll())
                .thenReturn(Collections.emptyList());

        ProcedimientoPasoExamen asociacion =
                new ProcedimientoPasoExamen(UUID.randomUUID());

        procedimientoPasoExamenModel.eliminar(asociacion);

        verify(procedimientoPasoExamenDAO).delete(asociacion);
    }

    /**
     * Verifica el acceso al DAO inyectado y la creación de una instancia vacía
     * de la entidad utilizada por los formularios nuevos.
     */
    @Test
    public void testGettersAndProtectedMethods() {
        assertEquals(
                procedimientoPasoExamenDAO,
                procedimientoPasoExamenModel.getDAO()
        );
        assertEquals(
                procedimientoPasoExamenDAO,
                procedimientoPasoExamenModel
                        .getProcedimientoPasoExamenDAO()
        );
        assertNotNull(
                procedimientoPasoExamenModel.crearNuevoRegistro()
        );
    }
}