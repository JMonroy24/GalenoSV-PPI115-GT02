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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;

/**
 *
 * @author jmonroy
 */
public class DocumentoModelTest {
    
    public DocumentoModelTest() {
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
     * Test of init method, of class DocumentoModel.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        DocumentoModel instance = new DocumentoModel();
        instance.init();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDAO method, of class DocumentoModel.
     */
    @Test
    public void testGetDAO() {
        System.out.println("getDAO");
        DocumentoModel instance = new DocumentoModel();
        DAOInterface<Documento, UUID> expResult = null;
        DAOInterface<Documento, UUID> result = instance.getDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of crearNuevoRegistro method, of class DocumentoModel.
     */
    @Test
    public void testCrearNuevoRegistro() {
        System.out.println("crearNuevoRegistro");
        DocumentoModel instance = new DocumentoModel();
        Documento expResult = null;
        Documento result = instance.crearNuevoRegistro();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDocumentoDAO method, of class DocumentoModel.
     */
    @Test
    public void testGetDocumentoDAO() {
        System.out.println("getDocumentoDAO");
        DocumentoModel instance = new DocumentoModel();
        DocumentoDAO expResult = null;
        DocumentoDAO result = instance.getDocumentoDAO();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDocumentoDAO method, of class DocumentoModel.
     */
    @Test
    public void testSetDocumentoDAO() {
        System.out.println("setDocumentoDAO");
        DocumentoDAO documentoDAO = null;
        DocumentoModel instance = new DocumentoModel();
        instance.setDocumentoDAO(documentoDAO);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
