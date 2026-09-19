/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

/**
 *
 * @author jmonroy
 */
public class PersonaModelTest {
    
    public PersonaModelTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of init method, of class PersonaModel.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        PersonaModel instance = new PersonaModel();
        instance.init();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDAO method, of class PersonaModel.
     */
    @Test
    public void testGetDAO() {
        System.out.println("getDAO");
        PersonaModel instance = new PersonaModel();
        DAOInterface<Persona, UUID> expResult = null;
        DAOInterface<Persona, UUID> result = instance.getDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of crearNuevoRegistro method, of class PersonaModel.
     */
    @Test
    public void testCrearNuevoRegistro() {
        System.out.println("crearNuevoRegistro");
        PersonaModel instance = new PersonaModel();
        Persona expResult = null;
        Persona result = instance.crearNuevoRegistro();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPersonaDAO method, of class PersonaModel.
     */
    @Test
    public void testGetPersonaDAO() {
        System.out.println("getPersonaDAO");
        PersonaModel instance = new PersonaModel();
        PersonaDAO expResult = null;
        PersonaDAO result = instance.getPersonaDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPersonaDAO method, of class PersonaModel.
     */
    @Test
    public void testSetPersonaDAO() {
        System.out.println("setPersonaDAO");
        PersonaDAO personaDAO = null;
        PersonaModel instance = new PersonaModel();
        instance.setPersonaDAO(personaDAO);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
