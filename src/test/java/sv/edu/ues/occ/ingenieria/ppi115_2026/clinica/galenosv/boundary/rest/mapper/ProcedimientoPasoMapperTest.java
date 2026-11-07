package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoMapperTest {

    @Mock
    ProcedimientoService procedimientoService;

    @Mock
    RolService rolService;

    @InjectMocks
    ProcedimientoPasoMapper mapper;

    @Test
    void idaYVueltaConservaCamposYResuelveRelaciones() {
        UUID id = UUID.randomUUID();
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        Rol rol = new Rol(UUID.randomUUID());
        ProcedimientoPaso entidad = new ProcedimientoPaso(id);
        entidad.setNombre("Paso inicial");
        entidad.setIndicaFin(Boolean.FALSE);
        entidad.setIdProcedimiento(procedimiento);
        entidad.setIdRol(rol);

        ProcedimientoPasoDTO dto = mapper.toDTO(entidad);
        assertEquals(id, dto.id());
        assertEquals("Paso inicial", dto.nombre());
        assertEquals(Boolean.FALSE, dto.indicaFin());
        assertEquals(procedimiento.getIdProcedimiento(), dto.idProcedimiento());
        assertEquals(rol.getIdRol(), dto.idRol());

        when(procedimientoService.findById(procedimiento.getIdProcedimiento())).thenReturn(procedimiento);
        when(rolService.findById(rol.getIdRol())).thenReturn(rol);
        ProcedimientoPaso vuelta = mapper.toEntity(dto);

        assertEquals(id, vuelta.getIdProcedimientoPaso());
        assertEquals(dto.nombre(), vuelta.getNombre());
        assertEquals(dto.indicaFin(), vuelta.getIndicaFin());
        assertSame(procedimiento, vuelta.getIdProcedimiento());
        assertSame(rol, vuelta.getIdRol());
    }

    @Test
    void copiarNoCambiaElIdDelDestino() {
        UUID idDestino = UUID.randomUUID();
        ProcedimientoPaso destino = new ProcedimientoPaso(idDestino);
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        Rol rol = new Rol(UUID.randomUUID());
        ProcedimientoPasoDTO dto = new ProcedimientoPasoDTO(UUID.randomUUID(), "Nuevo paso",
                Boolean.TRUE, procedimiento.getIdProcedimiento(), rol.getIdRol());
        when(procedimientoService.findById(procedimiento.getIdProcedimiento())).thenReturn(procedimiento);
        when(rolService.findById(rol.getIdRol())).thenReturn(rol);

        mapper.copiar(dto, destino);

        assertEquals(idDestino, destino.getIdProcedimientoPaso());
        assertEquals("Nuevo paso", destino.getNombre());
        assertEquals(Boolean.TRUE, destino.getIndicaFin());
        assertSame(procedimiento, destino.getIdProcedimiento());
        assertSame(rol, destino.getIdRol());
    }

    @Test
    void relacionInexistenteLanzaNotFound() {
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        UUID idRol = UUID.randomUUID();
        ProcedimientoPasoDTO dto = new ProcedimientoPasoDTO(null, "Paso", null,
                procedimiento.getIdProcedimiento(), idRol);
        when(procedimientoService.findById(procedimiento.getIdProcedimiento())).thenReturn(procedimiento);
        when(rolService.findById(idRol)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mapper.toEntity(dto));
    }
}
