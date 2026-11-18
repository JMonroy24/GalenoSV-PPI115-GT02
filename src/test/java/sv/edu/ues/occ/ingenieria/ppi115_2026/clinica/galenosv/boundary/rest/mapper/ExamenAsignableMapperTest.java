package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenAsignableDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenAsignableMapperTest {

    @Mock
    ExamenService examenService;

    @InjectMocks
    ExamenAsignableMapper examenAsignableMapper;

    @Test
    void toDTOIncluyeCategoriaDelExamen() {
        UUID idRelacion = UUID.randomUUID();
        UUID idPaso = UUID.randomUUID();
        UUID idExamen = UUID.randomUUID();
        UUID idTipo = UUID.randomUUID();

        Examen examen = new Examen(idExamen);
        examen.setNombre("Hemograma");
        ProcedimientoPaso paso = new ProcedimientoPaso(idPaso);
        ProcedimientoPasoExamen relacion = new ProcedimientoPasoExamen(idRelacion);
        relacion.setIdExamen(examen);
        relacion.setIdProcedimientoPaso(paso);
        relacion.setActivo(Boolean.TRUE);

        TipoExamen tipo = new TipoExamen(idTipo);
        tipo.setNombre("Laboratorio");
        ExamenTipoExamen relacionTipo = new ExamenTipoExamen();
        relacionTipo.setIdTipoExamen(tipo);
        when(examenService.findTiposByExamen(idExamen)).thenReturn(List.of(relacionTipo));

        ExamenAsignableDTO dto = examenAsignableMapper.toDTO(relacion);

        assertEquals(idRelacion, dto.idProcedimientoPasoExamen());
        assertEquals(idExamen, dto.idExamen());
        assertEquals("Hemograma", dto.nombre());
        assertEquals(Boolean.TRUE, dto.activo());
        assertEquals(idPaso, dto.idProcedimientoPaso());
        assertEquals(idTipo, dto.idTipoExamen());
        assertEquals("Laboratorio", dto.nombreTipoExamen());
    }
}
