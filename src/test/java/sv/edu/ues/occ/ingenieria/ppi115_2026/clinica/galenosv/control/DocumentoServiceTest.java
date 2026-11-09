package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceTest {

    @Mock
    private DocumentoDAO documentoDAO;

    @InjectMocks
    private DocumentoService documentoService;

    @BeforeEach
    void setUp() {
        documentoService.setDocumentoDAO(documentoDAO);
    }

    private Documento documento(String valor, TipoDocumento tipo) {
        Documento registro = new Documento(UUID.randomUUID());
        registro.setIdPersona(new Persona(UUID.randomUUID()));
        registro.setIdTipoDocumento(tipo);
        registro.setValor(valor);
        return registro;
    }

    private TipoDocumento tipoDocumento(boolean activo, String expresion, String indicaciones) {
        TipoDocumento tipo = new TipoDocumento(UUID.randomUUID());
        tipo.setActivo(activo);
        tipo.setExpresionRegular(expresion);
        tipo.setIndicaciones(indicaciones);
        return tipo;
    }

    @Test
    void crearValidaFormatoActividadYDuplicado() {
        TipoDocumento tipo = tipoDocumento(true, "[0-9]{8}-[0-9]",
                "Use ocho dígitos, guion y un dígito.");
        Documento registro = documento("incorrecto", tipo);

        var error = assertThrows(ValidacionNegocioException.class,
                () -> documentoService.crear(registro));
        assertTrue(error.getMessage().contains(tipo.getIndicaciones()));

        registro.setValor(" 12345678-9 ");
        documentoService.crear(registro);
        assertEquals("12345678-9", registro.getValor());

        tipo.setActivo(false);
        assertThrows(ValidacionNegocioException.class, () -> documentoService.crear(registro));

        tipo.setActivo(true);
        when(documentoDAO.existePersonaTipoValor(registro.getIdPersona().getIdPersona(),
                tipo.getIdTipoDocumento(), "12345678-9", registro.getIdDocumento())).thenReturn(true);
        assertThrows(ValidacionNegocioException.class, () -> documentoService.crear(registro));
        verify(documentoDAO).create(registro);
    }

    @Test
    void crearRechazaPersonaTipoYValorFaltantes() {
        Documento registro = new Documento(UUID.randomUUID());
        assertThrows(ValidacionNegocioException.class, () -> documentoService.crear(registro));

        registro.setValor("12345678-9");
        assertThrows(ValidacionNegocioException.class, () -> documentoService.crear(registro));

        registro.setIdPersona(new Persona(UUID.randomUUID()));
        assertThrows(ValidacionNegocioException.class, () -> documentoService.crear(registro));

        verify(documentoDAO, never()).create(any());
        verify(documentoDAO, never()).existePersonaTipoValor(any(), any(), anyString(), any());
    }
}
