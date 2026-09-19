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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.MedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;

/**
 *
 * @author jmonroy
 */
public class MedioContactoModelTest {
    
    public MedioContactoModelTest() {
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
     * Test of init method, of class MedioContactoModel.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        MedioContactoModel instance = new MedioContactoModel();
        instance.init();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDAO method, of class MedioContactoModel.
     */
    @Test
    public void testGetDAO() {
        System.out.println("getDAO");
        MedioContactoModel instance = new MedioContactoModel();
        DAOInterface<MedioContacto, UUID> expResult = null;
        DAOInterface<MedioContacto, UUID> result = instance.getDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of crearNuevoRegistro method, of class MedioContactoModel.
     */
    @Test
    public void testCrearNuevoRegistro() {
        System.out.println("crearNuevoRegistro");
        MedioContactoModel instance = new MedioContactoModel();
        MedioContacto expResult = null;
        MedioContacto result = instance.crearNuevoRegistro();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMedioContactoDAO method, of class MedioContactoModel.
     */
    @Test
    public void testGetMedioContactoDAO() {
        System.out.println("getMedioContactoDAO");
        MedioContactoModel instance = new MedioContactoModel();
        MedioContactoDAO expResult = null;
        MedioContactoDAO result = instance.getMedioContactoDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMedioContactoDAO method, of class MedioContactoModel.
     */
    @Test
    public void testSetMedioContactoDAO() {
        System.out.println("setMedioContactoDAO");
        MedioContactoDAO medioContactoDAO = null;
        MedioContactoModel instance = new MedioContactoModel();
        instance.setMedioContactoDAO(medioContactoDAO);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
