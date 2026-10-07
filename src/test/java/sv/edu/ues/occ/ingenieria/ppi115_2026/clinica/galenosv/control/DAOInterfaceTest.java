package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DAOInterfaceTest {

    @Test
    void testDefaultMethods() {
        DAOInterface<String, Long> dao = new DAOInterface<String, Long>() {
            @Override public void create(String entity) {}
            @Override public void update(String entity) {}
            @Override public void delete(String entity) {}
            @Override public String findById(Long id) { return null; }
            @Override public List<String> findAll() { return null; }
            @Override public List<String> findRange(int first, int max) { return List.of("test"); }
            @Override public long count() { return 5L; }
            @Override public List<String> findRange(int first, int max, String filtroGlobal, List<OrdenConsulta> orden, List<FiltroConsulta> filtros) { return null; }
            @Override public long count(String filtroGlobal, List<FiltroConsulta> filtros) { return 0; }
        };

        assertEquals(5L, dao.count("filtro"));
        assertEquals(1, dao.findRange(0, 10, "filtro").size());
    }
}
