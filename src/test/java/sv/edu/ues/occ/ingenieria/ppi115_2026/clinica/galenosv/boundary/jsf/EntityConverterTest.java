package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.faces.convert.ConverterException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

@ExtendWith(MockitoExtension.class)
class EntityConverterTest {

    @Mock
    private EntityManager em;
    @Mock
    private Metamodel metamodel;
    @Mock
    private EntityType<Persona> personaType;
    @InjectMocks
    private EntityConverter converter;

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void seleccionVaciaEsNullSinConsultarBD(String value) {
        assertNull(converter.getAsObject(null, null, value));
        verifyNoInteractions(em);
    }

    @Test
    void convierteEntidadRegistradaYExistente() {
        registrarPersona();
        UUID id = UUID.randomUUID();
        Persona persona = new Persona(id);
        when(em.find(Persona.class, id)).thenReturn(persona);

        assertSame(persona, converter.getAsObject(null, null, Persona.class.getName() + ":" + id));
    }

    @ParameterizedTest
    @ValueSource(strings = {"sin-separador", ":123", "inventada:", "inventada:no-es-uuid",
            "inventada:1-1-1-1-1", "inventada:00000000-0000-0000-0000-000000000001:otro"})
    void formatoOUuidInvalidoProduceMensajeClaro(String value) {
        ConverterException ex = assertThrows(ConverterException.class,
                () -> converter.getAsObject(null, null, value));
        assertEquals("Selección no válida", ex.getFacesMessage().getSummary());
        verifyNoInteractions(em);
    }

    @ParameterizedTest
    @ValueSource(strings = {"java.lang.Runtime", "no.existe.Entidad"})
    void noCargaClasesArbitrariasDelValorEnviado(String className) {
        registrarPersona();
        assertThrows(ConverterException.class,
                () -> converter.getAsObject(null, null, className + ":" + UUID.randomUUID()));
        verify(em, never()).find(any(Class.class), any());
    }

    @Test
    void entidadEliminadaMientrasElFormularioEstaAbiertoEsSeleccionInvalida() {
        registrarPersona();
        UUID id = UUID.randomUUID();
        when(em.find(Persona.class, id)).thenReturn(null);
        assertThrows(ConverterException.class,
                () -> converter.getAsObject(null, null, Persona.class.getName() + ":" + id));
    }

    @Test
    void entidadNoIdentificableNoSeAceptaAunqueEsteEnMetamodelo() {
        @SuppressWarnings("unchecked") EntityType<String> textoType = mock(EntityType.class);
        when(em.getMetamodel()).thenReturn(metamodel);
        when(metamodel.getEntities()).thenReturn(Set.of(textoType));
        when(textoType.getJavaType()).thenReturn(String.class);
        assertThrows(ConverterException.class,
                () -> converter.getAsObject(null, null, String.class.getName() + ":" + UUID.randomUUID()));
        verify(em, never()).find(any(Class.class), any());
    }

    @Test
    void serializaEntidadConClaseJpaRegistradaInclusoSiEsSubclase() {
        registrarPersona();
        UUID id = UUID.randomUUID();
        PersonaProxy persona = new PersonaProxy(id);
        assertEquals(Persona.class.getName() + ":" + id, converter.getAsString(null, null, persona));
    }

    @Test
    void serializarNullDevuelveVacioYObjetoAjenoFalla() {
        assertEquals("", converter.getAsString(null, null, null));
        assertThrows(ConverterException.class, () -> converter.getAsString(null, null, new Object()));
        verifyNoInteractions(em);
    }

    @Test
    void entidadSinIdentificadorNoEmiteSeleccionIrrecuperable() {
        assertThrows(ConverterException.class, () -> converter.getAsString(null, null, new Persona()));
    }

    private void registrarPersona() {
        when(em.getMetamodel()).thenReturn(metamodel);
        when(metamodel.getEntities()).thenReturn(Set.of(personaType));
        when(personaType.getJavaType()).thenReturn(Persona.class);
    }

    private static final class PersonaProxy extends Persona {
        PersonaProxy(UUID id) { super(id); }
    }
}
