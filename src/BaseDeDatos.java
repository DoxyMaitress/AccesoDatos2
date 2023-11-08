import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDeDatos {
    private Connection connection;

    public BaseDeDatos(String dbUrl, String dbUser, String dbPassword) throws SQLException {
        connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public Connection getConnection() {
        return connection;
    }
}
