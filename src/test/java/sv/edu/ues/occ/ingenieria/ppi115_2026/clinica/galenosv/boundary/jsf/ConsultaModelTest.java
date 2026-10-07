package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class ConsultaModelTest {

    @Mock
    private ConsultaDAO consultaDAO;

    @Mock
    private ConsultaProcedimientoDAO consultaProcedimientoDAO;

    @InjectMocks
    private ConsultaModel consultaModel;

    @BeforeEach
    public void setUp() {
        consultaModel.setConsultaDAO(consultaDAO);
        consultaModel.sesionBean = new SesionBean();
        var clinica = new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica(UUID.randomUUID());
        clinica.setActivo(true);
        consultaModel.sesionBean.setClinicaActual(clinica);
    }

    @Test
    public void testInitYCargarDatos() {
        consultaModel.init();
        assertNotNull(consultaModel.getLazyModel());
        assertNull(consultaModel.getRegistros());

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
        consultaModel.prepararNuevo();
        consultaModel.getRegistroActual().setObservaciones("Consulta general");

        var paciente = new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol(UUID.randomUUID());
        paciente.setIdClinica(consultaModel.sesionBean.getClinicaActual());
        var rol = new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol(UUID.randomUUID());
        rol.setActivo(true); rol.setNombre("Paciente"); paciente.setIdRol(rol);
        consultaModel.getRegistroActual().setIdPersonaRol(paciente);
        consultaModel.guardar();

        verify(consultaDAO).create(any(Consulta.class));
        assertEquals(Estado.NINGUNO, consultaModel.getEstado());
        assertNull(consultaModel.getRegistroActual());
    }

    @Test
    public void testGuardarModificar() {
        Consulta c = new Consulta(UUID.randomUUID());
        consultaModel.seleccionar(c);

        var paciente = new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol(UUID.randomUUID());
        paciente.setIdClinica(consultaModel.sesionBean.getClinicaActual());
        var rol = new sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol(UUID.randomUUID());
        rol.setActivo(true); rol.setNombre("Paciente"); paciente.setIdRol(rol);
        consultaModel.getRegistroActual().setIdPersonaRol(paciente);
        consultaModel.guardar();

        verify(consultaDAO).update(c);
        assertEquals(Estado.NINGUNO, consultaModel.getEstado());
    }

    @Test
    public void testEliminar() {
        Consulta c = new Consulta(UUID.randomUUID());
        consultaModel.eliminar(c);

        verify(consultaDAO).delete(c);
    }

    @Test
    public void testGettersAndSetters() {
        consultaModel.init();
        assertEquals(consultaDAO, consultaModel.getDAO());
        assertEquals(consultaDAO, consultaModel.getConsultaDAO());
        assertNotNull(consultaModel.crearNuevoRegistro());
        assertNotNull(consultaModel.getLazyModel());
    }
}
