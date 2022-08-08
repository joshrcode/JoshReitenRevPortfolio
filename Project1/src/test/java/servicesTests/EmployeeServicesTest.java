package servicesTests;

import exceptions.PendingRequests.InvalidAmountException;
import exceptions.PendingRequests.InvalidExtraInfoException;
import exceptions.PendingRequests.InvalidTypeException;
import exceptions.UsernameAlreadyUsedException;
import models.Employee;
import models.PendingRequest;
import models.ResolvedRequest;
import models.User;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import repositories.RequestRepository;
import repositories.UserRepository;
import services.EmployeeService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EmployeeServicesTest {


    JdbcDataSource dataSource;
    UserRepository userRepository;
    RequestRepository requestRepository;
    EmployeeService employeeServices;

    @Before
    public void init() {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test_db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        requestRepository = new RequestRepository(dataSource);
        userRepository = new UserRepository(dataSource);
        employeeServices = new EmployeeService(userRepository, requestRepository);
    }

    @Before
    public void initDatabase() throws SQLException {
        Connection connection = this.dataSource.getConnection();

        String createRequestsSQL = "create table if not exists requests (\n" +
                "request_id serial primary key,\n" +
                "requester_id int not null,\n" +
                "amount decimal not null,\n" +
                "type varchar(100) not null,\n" +
                "extra_info varchar(200),\n" +
                "is_reviewed boolean not null default false,\n" +
                "is_approved boolean,\n" +
                "reviewer_id int);";

        PreparedStatement ps = connection.prepareStatement(createRequestsSQL);
        ps.executeUpdate();

        String createUsersSQL = "create table if not exists users (\n" +
                "user_id serial primary key,\n" +
                "username varchar(100) unique not null,\n" +
                "password varchar(100) not null,\n" +
                "is_manager boolean not null,\n" +
                "email varchar(100) not null\n" +
                ");";

        ps = connection.prepareStatement(createUsersSQL);
        ps.executeUpdate();

        userRepository.insert(new Employee(1,"test","Test123!","test@test.com"));

    }

    @After
    public void tearDown() throws SQLException {
        Connection connection = dataSource.getConnection();

        String dropSQL = "DROP TABLE IF EXISTS requests, users CASCADE;";

        PreparedStatement ps = connection.prepareStatement(dropSQL);
        ps.executeUpdate();
    }

    @Test
    public void viewMyInfoWorksAsIntended() {
        Employee employee = new Employee(1,"user","pass", "email");
        String expected = "Username: user, Email: email";

        Assert.assertEquals(expected, employeeServices.viewMyInfo(employee));
    }

    @Test
    public void shouldUpdateEmailProperly() {
        // Get employee from database so we can update them
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Change user's email and update it in the database
        employeeServices.updateMyEmail(employee, "test@Updatetest.com");

        // inserted and updated user 1, user 1 returned by DB should be the same
        User actualUser = userRepository.getByID(1).get();

        // Expected user is copy of original user with email set to what was updated on original user
        Employee expectedEmployee = new Employee(1, "test","Test123!", "test@Updatetest.com");

        // Expect that user 1 in database should have email of "test"
        Assert.assertEquals(expectedEmployee, actualUser);
    }

    @Test
    public void updateEmailFailedWhenItWasPassedABadEmail () {
        // Get employee from database so we can update them
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Change user's email with an incorrect email format
        boolean expectedFalse = employeeServices.updateMyEmail(employee, "@test.com");

        Assert.assertFalse(expectedFalse);
    }

    @Test
    public void shouldUpdatePasswordProperly() {
        // Get employee from database so we can update them
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Change user's password and update it in the database
        employeeServices.updateMyPassword(employee, "Sup3rS3cur3P4$$w0Rd");

        // inserted and updated user 1, user 1 returned by DB should be the same
        User actualUser = userRepository.getByID(1).get();

        // Expected user is copy of original user with password set to what was updated on original user
        Employee expectedEmployee = new Employee(1, "test","Sup3rS3cur3P4$$w0Rd", "test@test.com");

        // Expect that user 1 in database should have email of "test"
        Assert.assertEquals(expectedEmployee, actualUser);
    }

    @Test
    public void updatePasswordFailedWhenItWasPassedABadPassword () {
        // Get employee from database so we can update them
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Change user's password and update it in the database
        boolean expectedFalse = employeeServices.updateMyPassword(employee, "badpass");

        Assert.assertFalse(expectedFalse);
    }
    @Test
    public void shouldUpdateUsernameProperly() {
        // Get employee from database so we can update them
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Change user's username and update it in the database
        employeeServices.updateMyUsername(employee, "testEmployee");

        // Updated user 1, user 1 returned by DB should be the same
        User actualUser = userRepository.getByID(1).get();

        // Expected user is copy of original user with username set to what was updated on original user
        Employee expectedEmployee = new Employee(1, "testEmployee","Test123!", "test@test.com");

        // Expect that user 1 in database should have email of "test"
        Assert.assertEquals(expectedEmployee, actualUser);
    }

    @Test
    public void updateUsernameFailedWhenItWasPassedABadUsername () {
        // Get employee from database so we can update them
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Change user's password and update it in the database
        boolean expectedFalse = employeeServices.updateMyUsername(employee, "T3stU$er"); //Invalid, contains $

        Assert.assertFalse(expectedFalse);
    }

    @Test(expected = UsernameAlreadyUsedException.class)
    public void updateUsernameFailedWhenItWasPassedAUsedUsername () {
        // Get employee from database so we can update them
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Insert another employee with the username we want to use for user 1
        Employee employee2 = new Employee(2, "testEmployee","Test123!", "test@test.com");
        userRepository.insert(employee2);

        // Change user's username and update it in the database
        employeeServices.updateMyUsername(employee, "testEmployee");
    }

    @Test
    public void submitRequestWorkedProperly() {
        // Get an employee for the constructor
        Employee employee = (Employee) userRepository.getByID(1).get();
        // Make a valid request
        boolean expectedTrue = employeeServices.submitRequest(employee, 13.99, "Food", "Burger");
        Assert.assertTrue(expectedTrue);

    }
    @Test (expected = InvalidAmountException.class)
    public void submitRequestAmountFailedProperly() {
        // Get an employee for the constructor
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Make an invalid request
        employeeServices.submitRequest(employee, -13.99, "Food", "Burger");
    }
    @Test (expected = InvalidTypeException.class)
    public void submitRequestTypeFailedProperly() {
        // Get an employee for the constructor
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Make an invalid request
        employeeServices.submitRequest(employee, 13.99, "stuff", "Burger");
    }
    @Test (expected = InvalidExtraInfoException.class)
    public void submitRequestExtraInfoFailedProperly() {
        // Get an employee for the constructor
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Make an invalid request
        employeeServices.submitRequest(employee, 13.99, "food", "Burger!");
    }

    @Test
    public void allPendingRequestsForEmployeeGotGot () {
        List<PendingRequest> expectedRequests = new ArrayList<>();

        for (int i=1; i<11; i++) {
            PendingRequest pendingRequest;
            if (i<4) {
                // Requests 1-3 are under Employee 2
                pendingRequest = new PendingRequest(i, 1.0, "", "", 2);
                // insert requests 1-3 to DB
                requestRepository.insert(pendingRequest);
            }
            else {
                // Create requests 4-10 and upload them to the database under Employee 1
                pendingRequest = new PendingRequest(i, 1.0, "", "", 1);
                requestRepository.insert(pendingRequest);
                if (i<8) {
                    // Resolve requests 8-10
                    ResolvedRequest resolvedRequest = new ResolvedRequest(pendingRequest, true, 3);
                    // Update the resolved in the DB
                    requestRepository.update(resolvedRequest);
                } else {
                    // Only add to expected if request isn't resolved and by employee 1
                    expectedRequests.add(pendingRequest);
                }
            }
        }

        // Get an employee object to pass into method
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Expect requests 4-7 to be returned
        Assert.assertArrayEquals(expectedRequests.toArray(), employeeServices.viewMyPendingRequests(employee).toArray());
    }

    @Test
    public void allResolvedRequestsForEmployeeGotGot () {
        List<ResolvedRequest> expectedRequests = new ArrayList<>();

        for (int i=1; i<11; i++) {
            PendingRequest pendingRequest;
            if (i<4) {
                // Requests 1-3 are under Employee 2
                pendingRequest = new PendingRequest(i, 1.0, "", "", 2);
                // insert requests 1-3 to DB
                requestRepository.insert(pendingRequest);
            }
            else {
                // Create requests 4-10 and upload them to the database under Employee 1
                pendingRequest = new PendingRequest(i, 1.0, "", "", 1);
                requestRepository.insert(pendingRequest);

                if (i<8) {
                    // Resolve requests 8-10 and update in DB
                    ResolvedRequest resolvedRequest = new ResolvedRequest(pendingRequest, true, 3);
                    requestRepository.update(resolvedRequest);

                    // Only resolved requests from Employee 1 are to be expected
                    expectedRequests.add(resolvedRequest);
                }
            }
        }

        // Get an employee to pass into method
        Employee employee = (Employee) userRepository.getByID(1).get();

        // Expect requests 8-10 to be returned
        Assert.assertArrayEquals(expectedRequests.toArray(), employeeServices.viewMyResolvedRequests(employee).toArray());
    }


}
