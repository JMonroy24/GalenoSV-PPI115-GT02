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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoMedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

/**
 *
 * @author jmonroy
 */
public class TipoMedioContactoModelTest {
    
    public TipoMedioContactoModelTest() {
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
     * Test of init method, of class TipoMedioContactoModel.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        TipoMedioContactoModel instance = new TipoMedioContactoModel();
        instance.init();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDAO method, of class TipoMedioContactoModel.
     */
    @Test
    public void testGetDAO() {
        System.out.println("getDAO");
        TipoMedioContactoModel instance = new TipoMedioContactoModel();
        DAOInterface<TipoMedioContacto, UUID> expResult = null;
        DAOInterface<TipoMedioContacto, UUID> result = instance.getDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of crearNuevoRegistro method, of class TipoMedioContactoModel.
     */
    @Test
    public void testCrearNuevoRegistro() {
        System.out.println("crearNuevoRegistro");
        TipoMedioContactoModel instance = new TipoMedioContactoModel();
        TipoMedioContacto expResult = null;
        TipoMedioContacto result = instance.crearNuevoRegistro();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTipoMedioContactoDAO method, of class TipoMedioContactoModel.
     */
    @Test
    public void testGetTipoMedioContactoDAO() {
        System.out.println("getTipoMedioContactoDAO");
        TipoMedioContactoModel instance = new TipoMedioContactoModel();
        TipoMedioContactoDAO expResult = null;
        TipoMedioContactoDAO result = instance.getTipoMedioContactoDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTipoMedioContactoDAO method, of class TipoMedioContactoModel.
     */
    @Test
    public void testSetTipoMedioContactoDAO() {
        System.out.println("setTipoMedioContactoDAO");
        TipoMedioContactoDAO tipoMedioContactoDAO = null;
        TipoMedioContactoModel instance = new TipoMedioContactoModel();
        instance.setTipoMedioContactoDAO(tipoMedioContactoDAO);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
