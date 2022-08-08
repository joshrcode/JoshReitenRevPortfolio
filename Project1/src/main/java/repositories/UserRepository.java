package repositories;

import exceptions.ResourceAlreadyExistsException;
import exceptions.ResourceDoesNotExistException;
import models.Employee;
import models.Manager;
import models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements RepositoryInterface<User, String> {

    // Repeated String used in basically every method
    private final String SQLEXMSG = "SQL Exception Caught";

    // #Inject those dependencies
    private final DataSource dataSource;
    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<User> get(String username) throws ResourceDoesNotExistException {

        // Set up
        Connection connection = null;
        User user = null;

        try {

            // Get a user with username ....
            String sql = "select * from users where username = ?;";

            // Establish connection, prepare, then execute query
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                // construct the user
                int userId = rs.getInt("user_id");
                String userUsername = rs.getString("username");
                String userPassword = rs.getString("password");
                boolean userIsManager = rs.getBoolean("is_manager");
                String userEmail = rs.getString("email");

                if (userIsManager) {
                    user = new Manager(userId, userUsername, userPassword, userEmail);
                }
                else {
                    user = new Employee(userId, userUsername, userPassword, userEmail);
                }

                // Return User if one was found, else throw NotExist exception
                Optional<User> userOptional = Optional.of(user);
                if (userOptional.isPresent()) {
                    return Optional.of(user);
                }
                else {
                    throw new ResourceDoesNotExistException(username);
                }
            }
        }

        catch (SQLException e) {
            System.out.println(SQLEXMSG);
        }

        finally {closeConnection(connection);}

        return Optional.ofNullable(user);
    }

    public Optional<User> getByID(int id) throws ResourceDoesNotExistException {

        // Set up
        Connection connection = null;
        User user = null;

        try {

            // Get a user with username ....
            String sql = "select * from users where user_id = ?;";

            // Establish connection, prepare, then execute query
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                // construct the user
                int userId = rs.getInt("user_id");
                String userUsername = rs.getString("username");
                String userPassword = rs.getString("password");
                boolean userIsManager = rs.getBoolean("is_manager");
                String userEmail = rs.getString("email");

                if (userIsManager) {
                    user = new Manager(userId, userUsername, userPassword, userEmail);
                }
                else {
                    user = new Employee(userId, userUsername, userPassword, userEmail);
                }

                // Return User if one was found, else return null
                Optional<User> userOptional = Optional.of(user);
                if (userOptional.isPresent()) {
                    return Optional.of(user);
                }
                else {
                    throw new ResourceDoesNotExistException("User ID ("+id+")");
                }

            }
        }
        catch (SQLException e) {
            System.out.println(SQLEXMSG);
        }
        finally { closeConnection(connection); }

        return Optional.ofNullable(user);
    }

    @Override
    public void update(User user) throws ResourceDoesNotExistException {
        Connection connection = null;
        boolean isManager = false;

        if (user.getClass().equals(Manager.class)) {
            isManager = true;
        }

        try {

            // "insert this user into 'users' table"
            String sql = "update users set  username = ?, password = ?, email = ?, is_manager = ? where user_id = ?;";

            // establish connection, prepare, then execute update
            connection = dataSource.getConnection();

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,user.getUsername());
            ps.setString(2,user.getPassword());
            ps.setString(3,user.getEmail());
            ps.setBoolean(4, isManager);
            ps.setInt(5, user.getId());

            int numOfRowsAffected = ps.executeUpdate();
            if (numOfRowsAffected<1) {
                throw new ResourceDoesNotExistException(user.getUsername());
            }
        }

        // Exception handling
        catch (SQLException e) {
                System.out.println(SQLEXMSG + e);
        }

        // finally block used to close connection
        finally { closeConnection(connection); }
    }


    @Override
    public void insert(User user) throws ResourceAlreadyExistsException {
        Connection connection = null;
        boolean isManager = false;

        if (user.getClass().equals(Manager.class)) {
            isManager = true;
        }

        try {

            // "insert this user into 'users' table"
            String sql = "insert into users (username, password, is_manager, email) values (?, ?, ?, ?);";

            // establish connection, prepare, then execute update
            connection = dataSource.getConnection();

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, isManager);
            ps.setString(4,user.getEmail());

            ps.executeUpdate();
        }

        // Exception handling
        catch (SQLException e) {
            // Error 23505 is duplicate key SQL error code
            if (e.getErrorCode() == 23505) {
                throw new ResourceAlreadyExistsException();
            } else {
                System.out.println(SQLEXMSG + e);
            }
        }

        // finally block used to close connection
        finally { closeConnection(connection); }
    }

    public List<Employee> getAllEmployees() {
        Connection connection = null;

        User user;
        List<Employee> employees = new ArrayList<>();

        try {

            String sql = "SELECT user_id, username, password, email from users WHERE is_manager = FALSE ORDER BY user_id;";

            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int id;
            String username;
            String password;
            String email;


            while (rs.next()) {
                id = rs.getInt("user_id");
                username = rs.getString("username");
                password = rs.getString("password");
                email = rs.getString("email");

                employees.add(new Employee(id, username, password, email));
            }

            return employees;
        }
        // Exception handling
        catch (SQLException e) {
                System.out.println(SQLEXMSG + e);
        }

        // finally block used to close connection
        finally {
            closeConnection(connection);
        }

        return employees;
    }
    public boolean isUsernameAvailable(String username) {

        // Set up
        Connection connection = null;
        int userId = -1;

        try {

            // Get a user with username ....
            String sql = "select user_id from users where username = ?;";

            // Establish connection, prepare, then execute query
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                userId = rs.getInt("user_id");
            }
        }
        catch (SQLException e) {
            System.out.println(SQLEXMSG);
        }
        finally {
            closeConnection(connection);

            if (userId == -1) {
                return true;
            }
            return false;
        }
    }

    public boolean isManager(String username) throws ResourceDoesNotExistException {

        // Set up
        Connection connection = null;
        boolean isManager = false;

        try {

            System.out.println("Entered Try Block");
            // Get a user with username ....
            String sql = "select is_manager from users where username = ?;";

            // Establish connection, prepare, then execute query
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            rs.next();
            isManager = rs.getBoolean("is_manager");
        }
        catch (SQLException e) {
            System.out.println(SQLEXMSG);
            throw new ResourceDoesNotExistException(username);
        }
        finally {closeConnection(connection);}
        return isManager;
    }
    public String getPassword(String username) throws ResourceDoesNotExistException {

        // Set up
        Connection connection = null;
        String password = "";

        try {
            // Get a user with username ....
            String sql = "select password from users where username = ?;";

            // Establish connection, prepare, then execute query
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            rs.next();
            password = rs.getString("password");
        }
        catch (SQLException e) {
            System.out.println(SQLEXMSG);
            throw new ResourceDoesNotExistException(username);
        }
        finally {closeConnection(connection);}
        return password;
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
