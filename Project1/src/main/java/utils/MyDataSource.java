//package utils;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.SQLTimeoutException;
//
//public class MyDataSource implements DataSource {
//    /**
//     * <p>Attempts to establish a connection with the data source that
//     * this {@code DataSource} object represents.
//     *
//     * @param username the database user on whose behalf the connection is
//     *                 being made
//     * @param password the user's password
//     * @return a connection to the data source
//     * @throws SQLException        if a database access error occurs
//     * @throws SQLTimeoutException when the driver has determined that the
//     *                             timeout value specified by the {@code setLoginTimeout} method
//     *                             has been exceeded and has at least tried to cancel the
//     *                             current database connection attempt
//     * @since 1.4
//     */
//    @Override
//    public Connection getConnection(String username, String password) throws SQLException {
//        return null;
//    }
//}
