package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.persistence;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import javax.naming.NamingException;

/** Reconoce fallos de conexión del proveedor, incluso dentro de errores de transacción. */
public final class ErroresConexion {
    private ErroresConexion() {
    }

    public static boolean esConexion(Throwable error) {
        Set<Throwable> visitadas = Collections.newSetFromMap(new IdentityHashMap<>());
        var pendientes = new ArrayDeque<Throwable>();
        pendientes.add(error);
        while (!pendientes.isEmpty()) {
            Throwable causa = pendientes.removeFirst();
            if (!visitadas.add(causa)) continue;
            if (causa instanceof SQLTransientConnectionException
                    || causa instanceof SQLNonTransientConnectionException
                    || causa instanceof ConnectException || causa instanceof UnknownHostException
                    || causa instanceof NamingException) {
                return true;
            }
            if (causa instanceof SQLException sql) {
                String estado = sql.getSQLState();
                if (estado != null && (estado.startsWith("08") || estado.startsWith("28")
                        || "3D000".equals(estado) || "57P03".equals(estado))) {
                    return true;
                }
                if (sql.getNextException() != null) pendientes.add(sql.getNextException());
            }
            if (causa.getCause() != null) pendientes.add(causa.getCause());
        }
        return false;
    }
}
