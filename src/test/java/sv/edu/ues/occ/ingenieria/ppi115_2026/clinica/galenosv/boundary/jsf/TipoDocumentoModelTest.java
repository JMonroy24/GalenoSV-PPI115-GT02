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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoDocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

/**
 *
 * @author jmonroy
 */
public class TipoDocumentoModelTest {
    
    public TipoDocumentoModelTest() {
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
     * Test of init method, of class TipoDocumentoModel.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        TipoDocumentoModel instance = new TipoDocumentoModel();
        instance.init();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDAO method, of class TipoDocumentoModel.
     */
    @Test
    public void testGetDAO() {
        System.out.println("getDAO");
        TipoDocumentoModel instance = new TipoDocumentoModel();
        DAOInterface<TipoDocumento, UUID> expResult = null;
        DAOInterface<TipoDocumento, UUID> result = instance.getDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of crearNuevoRegistro method, of class TipoDocumentoModel.
     */
    @Test
    public void testCrearNuevoRegistro() {
        System.out.println("crearNuevoRegistro");
        TipoDocumentoModel instance = new TipoDocumentoModel();
        TipoDocumento expResult = null;
        TipoDocumento result = instance.crearNuevoRegistro();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTipoDocumentoDAO method, of class TipoDocumentoModel.
     */
    @Test
    public void testGetTipoDocumentoDAO() {
        System.out.println("getTipoDocumentoDAO");
        TipoDocumentoModel instance = new TipoDocumentoModel();
        TipoDocumentoDAO expResult = null;
        TipoDocumentoDAO result = instance.getTipoDocumentoDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTipoDocumentoDAO method, of class TipoDocumentoModel.
     */
    @Test
    public void testSetTipoDocumentoDAO() {
        System.out.println("setTipoDocumentoDAO");
        TipoDocumentoDAO tipoDocumentoDAO = null;
        TipoDocumentoModel instance = new TipoDocumentoModel();
        instance.setTipoDocumentoDAO(tipoDocumentoDAO);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
