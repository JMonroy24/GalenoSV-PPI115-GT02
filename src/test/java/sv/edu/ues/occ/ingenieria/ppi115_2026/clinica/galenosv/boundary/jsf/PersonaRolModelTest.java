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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

/**
 *
 * @author jmonroy
 */
public class PersonaRolModelTest {
    
    public PersonaRolModelTest() {
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
     * Test of init method, of class PersonaRolModel.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        PersonaRolModel instance = new PersonaRolModel();
        instance.init();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDAO method, of class PersonaRolModel.
     */
    @Test
    public void testGetDAO() {
        System.out.println("getDAO");
        PersonaRolModel instance = new PersonaRolModel();
        DAOInterface<PersonaRol, UUID> expResult = null;
        DAOInterface<PersonaRol, UUID> result = instance.getDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of crearNuevoRegistro method, of class PersonaRolModel.
     */
    @Test
    public void testCrearNuevoRegistro() {
        System.out.println("crearNuevoRegistro");
        PersonaRolModel instance = new PersonaRolModel();
        PersonaRol expResult = null;
        PersonaRol result = instance.crearNuevoRegistro();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPersonaRolDAO method, of class PersonaRolModel.
     */
    @Test
    public void testGetPersonaRolDAO() {
        System.out.println("getPersonaRolDAO");
        PersonaRolModel instance = new PersonaRolModel();
        PersonaRolDAO expResult = null;
        PersonaRolDAO result = instance.getPersonaRolDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPersonaRolDAO method, of class PersonaRolModel.
     */
    @Test
    public void testSetPersonaRolDAO() {
        System.out.println("setPersonaRolDAO");
        PersonaRolDAO personaRolDAO = null;
        PersonaRolModel instance = new PersonaRolModel();
        instance.setPersonaRolDAO(personaRolDAO);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
