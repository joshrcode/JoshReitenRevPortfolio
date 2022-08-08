package repositoryTests;

import exceptions.ResourceAlreadyExistsException;
import exceptions.ResourceDoesNotExistException;
import models.Employee;
import models.Manager;
import models.User;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;
import repositories.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryTest {

    // Establish needed resources
    UserRepository userRepository;
    JdbcDataSource dataSource;


    @Before
    public void init() {

        // Set up datasource and repository
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test_db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        userRepository = new UserRepository(dataSource);
    }

    @Before
    public void initDatabase() throws SQLException {
        Connection connection = this.dataSource.getConnection();

        // Create doppleganger of users table
        String createTableSQL = "create table if not exists users " +
                "(user_id serial primary key, username varchar(100) unique not null, " +
                "password varchar(100) not null, is_manager boolean not null, email varchar(100) not null);";

        PreparedStatement ps = connection.prepareStatement(createTableSQL);
        ps.executeUpdate();
    }

    @After
    public void tearDown() throws SQLException {
        Connection connection = dataSource.getConnection();

        // Get rid of the user table after test. Just in case.
        String dropSQL = "DROP TABLE IF EXISTS users CASCADE;";

        PreparedStatement ps = connection.prepareStatement(dropSQL);
        ps.executeUpdate();
    }

    @Test
    public void shouldGetConnection() {
        // Variable will track if connection failed
        boolean connectionFailed = false;

        try { dataSource.getConnection(); }

        catch (SQLException e) {
            // update failed variable
            connectionFailed = true;
        }

        // Expect failed to be false i.e. connection success
        boolean expectedResult = false;
        Assert.assertEquals(expectedResult, connectionFailed);
    }

    @Test
    public void shouldGetCorrectUser() throws SQLException{
        // Create a user to upload to DB
        User expectedUser = new Employee(1,"testEmployee","","");
        userRepository.insert(expectedUser);

        // add 9 more users as control group. i.e. none of these should be returned
        for (int i=2; i<11; i++) {
            Employee newEmployee = new Employee(i, String.valueOf(i), "", "");
            userRepository.insert(newEmployee);
        }

        // Retrieve the user
        Optional<User> actualUser = userRepository.get("testEmployee");

        // Compare actual and expected
        Assert.assertEquals(expectedUser, actualUser.get());
    }
    @Test
    public void shouldGetCorrectUserByID() throws SQLException{
        // Create a user to upload to DB
        User expectedUser = new Employee(1,"testEmployee","","");
        userRepository.insert(expectedUser);

        // add 9 more users as control group. i.e. none of these should be returned
        for (int i=2; i<11; i++) {
            Employee newEmployee = new Employee(i, String.valueOf(i), "", "");
            userRepository.insert(newEmployee);
        }

        // Retrieve the user
        Optional<User> actualUser = userRepository.getByID(1);

        // Expect username to be "testEmployee" as the rest are named after the variable i
        Assert.assertEquals(expectedUser.getUsername(), actualUser.get().getUsername());
    }

    @Test
    public void optionalDoesNotReturnWhenMissing() throws SQLException{
        // Create a user to upload to fake DB
        User addedUser = new Employee(1,"testEmployee","","");
        userRepository.insert(addedUser);

        // Retrieve an incorrect user
        Optional<User> gottenUser = userRepository.get("wrongEmployee");
        // Expect gottenUser Optional to be empty
        Assert.assertFalse(gottenUser.isPresent());
    }
    
    @Test
    public void shouldInsertUserToDatabase() {
        // Create and upload an Employee to the DB
        User expectedUser = new Employee(1,"","","");
        userRepository.insert(expectedUser);

        // retrieve the inserted User to see if it matches the Employee it was based on
        User actualUser = userRepository.getByID(1).get();

        // Expect created employee to match employee 1 from database
        Assert.assertEquals(expectedUser,actualUser);
    }
    
    @Test(expected = ResourceAlreadyExistsException.class) // Expect AlreadyExists to be thrown
    public void shouldNotAllowDuplicateEntriesIntoDatabase() {
        // Create a new Employee and insert into database
        User user = new Employee(1,"","","");
        userRepository.insert(user);
        
        // Insert that same employee to trigger ResourceAlreadyExistsException
        userRepository.insert(user);

        // Expect ResourceAlreadyExistsException to be thrown
    }

    @Test
    public void shouldUpdateUserInfoProperly() {
        // Create a new Employee and insert into database
        User user = new Employee(1,"","","");
        userRepository.insert(user);
        
        // Change user's email and update it in the database
        user.setPassword("test");
        userRepository.update(user);

        // inserted and updated user 1, user 1 returned by DB should be the same
        User actualUser = userRepository.getByID(1).get();

        // Expected user is copy of original user with email set to what was updated on original user
        User expectedUser = new Employee(1, "","test", "");

        // Expect that user 1 in database should have password of "test"
        Assert.assertEquals(expectedUser, actualUser);
    }

    @Test(expected = ResourceDoesNotExistException.class) // Expect NotExist to be thrown
    public void updateShouldThrowExceptionIfResourceDoesNotExist() {
        // No items currently in database

        // Create an Employee and attempt to update in the database
        User user = new Employee(12,"","","");
        userRepository.update(user);

        // Expect ResourceDoesNotExistException to be thrown
    }

    @Test
    public void allEmployeesGotGot () {
        List<Employee> expectedEmployees = new ArrayList<>();

        for (int i=1; i<11; i++) {
            // Create 10 Employees, add them to list of database
            Employee employee = new Employee(i,String.valueOf(i),String.valueOf(i),String.valueOf(i));
            userRepository.insert(employee);

            // "Promote" Employees 1-3, so that they are now Managers and update in DB
            if (i<4) {
                Manager manager = new Manager(i,String.valueOf(i),String.valueOf(i),String.valueOf(i));
                userRepository.update(manager);
            }

            // Any Employees that are still Employees (i.e. 4-10) are expected to be returned
            else {
                expectedEmployees.add(employee);
            }
        }

        // Expect Employees 4-10 to be returned
        Assert.assertArrayEquals(expectedEmployees.toArray(), userRepository.getAllEmployees().toArray());
    }

    @Test
    public void usernameDoesNotExistSoItShouldBeAvailable() {
        boolean expectedToBeTrue = userRepository.isUsernameAvailable("test");
        Assert.assertTrue(expectedToBeTrue);
    }
    @Test
    public void usernameDoesExistSoItShouldNotBeAvailable() {
        Employee employee = new Employee(1, "test", "test", "test@test.com");
        userRepository.insert(employee);

        boolean expectedToBeFalse = userRepository.isUsernameAvailable("test");
        Assert.assertFalse(expectedToBeFalse);
    }

    @Test
    public void isManagerWorksProperly() {
        Employee employee = new Employee(1, "employee", "test", "test@test.com");
        userRepository.insert(employee);
        Manager manager = new Manager(2, "manager", "test", "test@test.com");
        userRepository.insert(manager);

        Assert.assertTrue(userRepository.isManager("manager"));
        Assert.assertFalse(userRepository.isManager("employee"));
    }

    @Test(expected = ResourceDoesNotExistException.class)
    public void isManagerThrowsExceptionWhenUsernameCannotBeLocated() {
        userRepository.isManager("KLSJDHFKJSHDKJFH");
    }

    @Test
    public void getPasswordWorksCorrectly() {
        Employee employee = new Employee(1, "employee", "Test123!", "test@test.com");
        userRepository.insert(employee);
        String expectedPassword = "Test123!";
        String actualPassword = userRepository.getPassword("employee");
        Assert.assertEquals(expectedPassword,actualPassword);
    }

    @Test(expected = ResourceDoesNotExistException.class)
    public void getPasswordThrowsExceptionWhenUserDoesNotExist() {
        userRepository.getPassword("KSDHFKHSDFKJHSDKJ");
    }

}