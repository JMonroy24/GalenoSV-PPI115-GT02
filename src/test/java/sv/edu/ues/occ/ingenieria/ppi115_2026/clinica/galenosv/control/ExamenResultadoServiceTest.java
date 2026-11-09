package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenResultadoServiceTest {

    @Mock
    private ExamenResultadoDAO examenResultadoDAO;

    @InjectMocks
    private ExamenResultadoService examenResultadoService;

    @BeforeEach
    void setUp() {
        examenResultadoService.setExamenResultadoDAO(examenResultadoDAO);
    }

    @Test
    void crearAsignaFechaDeCreacionCuandoNoViene() {
        ExamenResultado nuevo = new ExamenResultado(UUID.randomUUID());
        nuevo.setResultado("5.2");
        nuevo.setInterpretacion("Normal");
        nuevo.setIdOrdenExamen(new OrdenExamen(UUID.randomUUID()));

        examenResultadoService.crear(nuevo);

        assertNotNull(nuevo.getFechaCreacion());
        verify(examenResultadoDAO).create(nuevo);
    }

    @Test
    void actualizarNoModificaFechaDeCreacion() {
        Date fechaOriginal = new Date(1600000000000L);
        ExamenResultado existente = new ExamenResultado(UUID.randomUUID());
        existente.setFechaCreacion(fechaOriginal);
        existente.setResultado("5.2");
        existente.setInterpretacion("Normal");
        existente.setIdOrdenExamen(new OrdenExamen(UUID.randomUUID()));

        examenResultadoService.actualizar(existente);

        assertEquals(fechaOriginal, existente.getFechaCreacion());
        verify(examenResultadoDAO).update(existente);
    }
}
