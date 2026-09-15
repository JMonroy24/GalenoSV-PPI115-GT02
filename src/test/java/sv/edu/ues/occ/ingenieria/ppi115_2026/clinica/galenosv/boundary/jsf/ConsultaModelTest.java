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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultaModelTest {

    @Mock
    private ConsultaDAO consultaDAO;

    @InjectMocks
    private ConsultaModel consultaModel;

    @BeforeEach
    public void setUp() {
        consultaModel.setConsultaDAO(consultaDAO);
    }

    @Test
    public void testInitYCargarDatos() {
        Consulta c = new Consulta(UUID.randomUUID());
        when(consultaDAO.findAll()).thenReturn(List.of(c));

        consultaModel.init();

        assertNotNull(consultaModel.getRegistros());
        assertEquals(1, consultaModel.getRegistros().size());
        verify(consultaDAO).findAll();
    }

    @Test
    public void testPrepararNuevo() {
        consultaModel.prepararNuevo();

        assertNotNull(consultaModel.getRegistroActual());
        assertEquals(Estado.CREAR, consultaModel.getEstado());
        assertTrue(consultaModel.isEstadoCrear());
    }

    @Test
    public void testSeleccionar() {
        Consulta c = new Consulta(UUID.randomUUID());
        consultaModel.seleccionar(c);

        assertEquals(c, consultaModel.getRegistroActual());
        assertEquals(Estado.MODIFICAR, consultaModel.getEstado());
        assertTrue(consultaModel.isEstadoModificar());
    }

    @Test
    public void testCancelar() {
        consultaModel.prepararNuevo();
        consultaModel.cancelar();

        assertNull(consultaModel.getRegistroActual());
        assertEquals(Estado.NINGUNO, consultaModel.getEstado());
        assertTrue(consultaModel.isEstadoNinguno());
    }

    @Test
    public void testGuardarCrear() {
        when(consultaDAO.findAll()).thenReturn(Collections.emptyList());

        consultaModel.prepararNuevo();
        consultaModel.getRegistroActual().setObservaciones("Consulta general");

        consultaModel.guardar();

        verify(consultaDAO).create(any(Consulta.class));
        assertEquals(Estado.NINGUNO, consultaModel.getEstado());
        assertNull(consultaModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        when(consultaDAO.findAll()).thenReturn(Collections.emptyList());

        Consulta c = new Consulta(UUID.randomUUID());
        consultaModel.seleccionar(c);

        consultaModel.guardar();

        verify(consultaDAO).update(c);
        assertEquals(Estado.NINGUNO, consultaModel.getEstado());
    }

    @Test
    public void testEliminar() {
        when(consultaDAO.findAll()).thenReturn(Collections.emptyList());

        Consulta c = new Consulta(UUID.randomUUID());
        consultaModel.eliminar(c);

        verify(consultaDAO).delete(c);
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals(consultaDAO, consultaModel.getDAO());
        assertEquals(consultaDAO, consultaModel.getConsultaDAO());
        assertNotNull(consultaModel.crearNuevoRegistro());
    }
}
