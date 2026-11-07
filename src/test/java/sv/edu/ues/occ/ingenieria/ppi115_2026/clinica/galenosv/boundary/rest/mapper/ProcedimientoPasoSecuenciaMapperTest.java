package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoSecuenciaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoSecuenciaMapperTest {

    @Mock
    ProcedimientoPasoService procedimientoPasoService;

    @InjectMocks
    ProcedimientoPasoSecuenciaMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelacion() {
        UUID id = UUID.randomUUID();
        UUID idReferencia = UUID.randomUUID();
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        ProcedimientoPasoSecuencia entidad = new ProcedimientoPasoSecuencia(id);
        entidad.setIdProcedimientoPasoReferencia(idReferencia);
        entidad.setTipoSecuencia("SIGUIENTE");
        entidad.setIdProcedimientoPaso(paso);

        ProcedimientoPasoSecuenciaDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals(idReferencia, dto.idProcedimientoPasoReferencia());
        assertEquals("SIGUIENTE", dto.tipoSecuencia());
        assertEquals(paso.getIdProcedimientoPaso(), dto.idProcedimientoPaso());

        when(procedimientoPasoService.findById(paso.getIdProcedimientoPaso())).thenReturn(paso);
        ProcedimientoPasoSecuencia vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdProcedimientoPasoSecuencia());
        assertEquals(idReferencia, vuelta.getIdProcedimientoPasoReferencia());
        assertEquals(dto.tipoSecuencia(), vuelta.getTipoSecuencia());
        assertSame(paso, vuelta.getIdProcedimientoPaso());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        UUID idReferencia = UUID.randomUUID();
        ProcedimientoPasoSecuencia destino = new ProcedimientoPasoSecuencia(idDestino);
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        ProcedimientoPasoSecuenciaDTO dto = new ProcedimientoPasoSecuenciaDTO(UUID.randomUUID(),
                idReferencia, "ALTERNATIVO", paso.getIdProcedimientoPaso());
        when(procedimientoPasoService.findById(paso.getIdProcedimientoPaso())).thenReturn(paso);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdProcedimientoPasoSecuencia());
        assertEquals(idReferencia, destino.getIdProcedimientoPasoReferencia());
        assertEquals("ALTERNATIVO", destino.getTipoSecuencia());
        assertSame(paso, destino.getIdProcedimientoPaso());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        UUID idPaso = UUID.randomUUID();
        ProcedimientoPasoSecuenciaDTO dto = new ProcedimientoPasoSecuenciaDTO(null, null, "SIGUIENTE", idPaso);
        when(procedimientoPasoService.findById(idPaso)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}
