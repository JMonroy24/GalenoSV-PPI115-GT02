package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedioContactoServiceTest {

    @Mock
    private MedioContactoDAO medioContactoDAO;

    @InjectMocks
    private MedioContactoService medioContactoService;

    @BeforeEach
    void setUp() {
        medioContactoService.setMedioContactoDAO(medioContactoDAO);
    }

    @Test
    void crearAsignaFechaDeCreacionCuandoNoViene() {
        Persona persona = new Persona(UUID.randomUUID());
        TipoMedioContacto tipo = new TipoMedioContacto(UUID.randomUUID());
        tipo.setActivo(Boolean.TRUE);
        when(medioContactoDAO.existePersonaTipoValor(any(), any(), anyString(), any())).thenReturn(false);

        MedioContacto nuevo = new MedioContacto(UUID.randomUUID());
        nuevo.setValor("correo@dominio.com");
        nuevo.setIdPersona(persona);
        nuevo.setIdTipoMedioContacto(tipo);

        medioContactoService.crear(nuevo);

        assertNotNull(nuevo.getFechaCreacion());
        verify(medioContactoDAO).create(nuevo);
    }

    @Test
    void actualizarNoModificaFechaDeCreacion() {
        Persona persona = new Persona(UUID.randomUUID());
        TipoMedioContacto tipo = new TipoMedioContacto(UUID.randomUUID());
        tipo.setActivo(Boolean.TRUE);
        when(medioContactoDAO.existePersonaTipoValor(any(), any(), anyString(), any())).thenReturn(false);

        Date fechaOriginal = new Date(1600000000000L);
        MedioContacto existente = new MedioContacto(UUID.randomUUID());
        existente.setFechaCreacion(fechaOriginal);
        existente.setValor("correo@dominio.com");
        existente.setIdPersona(persona);
        existente.setIdTipoMedioContacto(tipo);

        medioContactoService.actualizar(existente);

        assertEquals(fechaOriginal, existente.getFechaCreacion());
        verify(medioContactoDAO).update(existente);
    }

    @Test
    void crearValidaFormatoActividadYDuplicadoPorPersona() {
        Persona persona = new Persona(UUID.randomUUID());
        TipoMedioContacto tipo = new TipoMedioContacto(UUID.randomUUID());
        tipo.setActivo(true);
        tipo.setExpresionRegular("[^@ ]+@[^@ ]+[.][^@ ]+");
        when(medioContactoDAO.existePersonaTipoValor(any(), any(), anyString(), any())).thenReturn(false);

        MedioContacto registro = new MedioContacto(UUID.randomUUID());
        registro.setIdPersona(persona);
        registro.setIdTipoMedioContacto(tipo);
        registro.setValor("correo inválido");
        assertThrows(ValidacionNegocioException.class, () -> medioContactoService.crear(registro));

        registro.setValor(" ana@example.org ");
        medioContactoService.crear(registro);
        assertEquals("ana@example.org", registro.getValor());

        tipo.setActivo(false);
        assertThrows(ValidacionNegocioException.class, () -> medioContactoService.crear(registro));

        tipo.setActivo(true);
        when(medioContactoDAO.existePersonaTipoValor(persona.getIdPersona(), tipo.getIdTipoMedioContacto(),
                "ana@example.org", registro.getIdMedioContacto())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> medioContactoService.crear(registro));
        verify(medioContactoDAO).create(registro);
    }
}
