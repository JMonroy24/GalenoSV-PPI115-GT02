package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenExamenServiceTest {

    @Mock
    private OrdenExamenDAO ordenExamenDAO;

    @InjectMocks
    private OrdenExamenService ordenExamenService;

    @BeforeEach
    void setUp() {
        ordenExamenService.setOrdenExamenDAO(ordenExamenDAO);
    }

    @Test
    void crearAsignaFechaDeCreacionCuandoNoViene() {
        OrdenExamen nuevo = new OrdenExamen(UUID.randomUUID());
        nuevo.setIndicaciones("Ayuno de 8 horas");
        nuevo.setIdConsultaProcedimientoPaso(new ConsultaProcedimientoPaso(UUID.randomUUID()));

        ordenExamenService.crear(nuevo);

        assertNotNull(nuevo.getFechaCreacion());
        verify(ordenExamenDAO).create(nuevo);
    }

    @Test
    void actualizarNoModificaFechaDeCreacion() {
        Date fechaOriginal = new Date(1600000000000L);
        OrdenExamen existente = new OrdenExamen(UUID.randomUUID());
        existente.setFechaCreacion(fechaOriginal);
        existente.setIndicaciones("Ayuno de 8 horas");
        existente.setIdConsultaProcedimientoPaso(new ConsultaProcedimientoPaso(UUID.randomUUID()));

        ordenExamenService.actualizar(existente);

        assertEquals(fechaOriginal, existente.getFechaCreacion());
        verify(ordenExamenDAO).update(existente);
    }
}
