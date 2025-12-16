package ma.WhiteLab.conf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import ma.WhiteLab.conf.util.PropertiesExtractor;

/**
 * Classe responsable de la création et de la gestion d'une connexion JDBC unique.
 * Pattern Singleton thread-safe avec double-checked locking.
 */
public final class SessionFactory {

    private static volatile SessionFactory INSTANCE;
    private Connection connection;

    private static final String PROPS_PATH = "config/db.properties";
    private static final String URL_KEY    = "db.url";
    private static final String USER_KEY   = "db.username";
    private static final String PASS_KEY   = "db.password";
    private static final String DRIVER_KEY = "db.driver";

    private final String url;
    private final String user;
    private final String password;
    private final String driver;

    private SessionFactory() {
        Properties properties = PropertiesExtractor.loadConfigFile(PROPS_PATH);

        this.url      = PropertiesExtractor.getPropertyValue(URL_KEY, properties);
        this.user     = PropertiesExtractor.getPropertyValue(USER_KEY, properties);
        this.password = PropertiesExtractor.getPropertyValue(PASS_KEY, properties);
        this.driver   = PropertiesExtractor.getPropertyValue(DRIVER_KEY, properties);

        // Charger le driver JDBC
        if (driver != null && !driver.isBlank()) {
            try {
                Class.forName(driver);
                System.out.println("Driver JDBC chargé avec succès : " + driver);
            } catch (ClassNotFoundException e) {
                System.err.println("Driver JDBC introuvable : " + driver);
                e.printStackTrace();
            }
        }
    }

    public static SessionFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (SessionFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SessionFactory();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Retourne une connexion JDBC active et valide.
     * Si elle n'existe pas ou n'est plus valide, elle est recréée.
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !isValid(connection)) {
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    private boolean isValid(Connection conn) {
        try {
            return conn != null && conn.isValid(2); // timeout 2 secondes
        } catch (SQLException e) {
            return false;
        }
    }

    /** Ferme proprement la connexion JDBC */
    public synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion JDBC fermée correctement.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
