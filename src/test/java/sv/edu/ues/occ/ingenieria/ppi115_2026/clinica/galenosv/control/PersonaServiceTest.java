package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaDAO personaDAO;

    @InjectMocks
    private PersonaService personaService;

    @BeforeEach
    void setUp() {
        personaService.setPersonaDAO(personaDAO);
    }

    @Test
    void crearAsignaFechaDeCreacionCuandoNoViene() {
        Persona nueva = new Persona(UUID.randomUUID());
        nueva.setNombres("Ana");
        nueva.setApellidos("Pérez");

        personaService.crear(nueva);

        assertNotNull(nueva.getFechaCreacion());
        verify(personaDAO).create(nueva);
    }

    @Test
    void actualizarNoModificaFechaDeCreacion() {
        Date fechaOriginal = new Date(1600000000000L);
        Persona existente = new Persona(UUID.randomUUID());
        existente.setFechaCreacion(fechaOriginal);
        existente.setNombres("Ana");
        existente.setApellidos("Pérez");

        personaService.actualizar(existente);

        assertEquals(fechaOriginal, existente.getFechaCreacion());
        verify(personaDAO).update(existente);
    }
}
