package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDetalleDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaDetalleMapperTest {

    @Mock
    ConsultaMapper consultaMapper;

    @Mock
    ConsultaProcedimientoService consultaProcedimientoService;

    @Mock
    ConsultaProcedimientoPasoService consultaProcedimientoPasoService;

    @InjectMocks
    ConsultaDetalleMapper consultaDetalleMapper;

    @Test
    void toDTOIncluyePasosOrdenadosConNombreYOrden() {
        UUID idConsulta = UUID.randomUUID();
        UUID idPaso = UUID.randomUUID();
        Consulta consulta = new Consulta(idConsulta);
        ConsultaDTO consultaBase = new ConsultaDTO(idConsulta, new Date(), new Date(), "REF", "OBS", UUID.randomUUID());

        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        procedimiento.setNombre("Procedimiento base");
        ConsultaProcedimiento consultaProcedimiento = new ConsultaProcedimiento(UUID.randomUUID());
        consultaProcedimiento.setIdProcedimiento(procedimiento);

        ConsultaProcedimientoPaso paso1 = new ConsultaProcedimientoPaso(UUID.randomUUID());
        paso1.setFechaInicio(new Date(2000));
        paso1.setEstado("EJECUTADO");
        paso1.setValor("7.5");
        ProcedimientoPaso definicion = new ProcedimientoPaso(idPaso);
        definicion.setNombre("Toma de muestra");
        paso1.setIdProcedimientoPaso(definicion);

        ConsultaProcedimientoPaso paso2 = new ConsultaProcedimientoPaso(UUID.randomUUID());
        paso2.setFechaInicio(new Date(1000));
        paso2.setEstado("PENDIENTE");
        paso2.setIdConsultaProcedimiento(consultaProcedimiento);

        when(consultaMapper.toDTO(consulta)).thenReturn(consultaBase);
        when(consultaProcedimientoService.findByConsulta(idConsulta)).thenReturn(List.of(consultaProcedimiento));
        when(consultaProcedimientoPasoService.findByConsultaProcedimiento(consultaProcedimiento.getIdConsultaProcedimiento()))
                .thenReturn(List.of(paso1, paso2));

        ConsultaDetalleDTO respuesta = consultaDetalleMapper.toDTO(consulta);

        assertEquals(idConsulta, respuesta.id());
        assertEquals("REF", respuesta.referenciaExterna());
        assertEquals(2, respuesta.pasos().size());
        assertEquals(paso2.getIdConsultaProcedimientoPaso(), respuesta.pasos().get(0).id());
        assertEquals(1, respuesta.pasos().get(0).orden());
        assertEquals("Procedimiento base", respuesta.pasos().get(0).nombre());
        assertNull(respuesta.pasos().get(0).idProcedimientoPaso());
        assertEquals(paso1.getIdConsultaProcedimientoPaso(), respuesta.pasos().get(1).id());
        assertEquals(2, respuesta.pasos().get(1).orden());
        assertEquals("Toma de muestra", respuesta.pasos().get(1).nombre());
        assertEquals(idPaso, respuesta.pasos().get(1).idProcedimientoPaso());
        assertEquals("7.5", respuesta.pasos().get(1).valor());
    }
}
