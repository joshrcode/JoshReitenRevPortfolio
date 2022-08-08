package utils;

import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConnectionManager class registers a driver and establishes a connection.
 */
public class ConnectionManager {
    ConnectionAuthorizer connectionAuthorizer = new ConnectionAuthorizer();
    private final String url;
    private final String username;
    private final String password;

    private final Driver driver;
    private boolean driverReady;

    public ConnectionManager() {
        this.url = connectionAuthorizer.getURL();
        this.username = connectionAuthorizer.getUSERNAME();
        this.password = connectionAuthorizer.getPASSWORD();
        this.driver = connectionAuthorizer.getDRIVER();
    }


    public void registerDriver() throws SQLException {
        if (!driverReady) {
            DriverManager.registerDriver(this.driver);
        }
    }

    public Connection getConnection() throws SQLException {
        if (!driverReady) {
            registerDriver();
        }
        return DriverManager.getConnection(url, username, password);
    }
}
