package servicesTests;

import exceptions.ResourceDoesNotExistException;
import exceptions.UsernameAlreadyUsedException;
import models.*;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import repositories.RequestRepository;
import repositories.UserRepository;
import services.ManagerService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManagerServicesTest {
    JdbcDataSource dataSource;
    UserRepository userRepository;
    RequestRepository requestRepository;
    ManagerService managerServices;

    @Before
    public void init() {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test_db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        requestRepository = new RequestRepository(dataSource);
        userRepository = new UserRepository(dataSource);
        managerServices = new ManagerService(userRepository, requestRepository);
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



    }

    @After
    public void tearDown() throws SQLException {
        Connection connection = dataSource.getConnection();

        String dropSQL = "DROP TABLE IF EXISTS requests, users CASCADE;";

        PreparedStatement ps = connection.prepareStatement(dropSQL);
        ps.executeUpdate();
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
        Assert.assertArrayEquals(expectedEmployees.toArray(), managerServices.viewAllEmployees().toArray());
    }

    @Test
    public void allResolvedRequestsForManagerGotGot () {
        Manager manager1 = new Manager(1, "Manager1", "passw0rd!", "manager1@test.com");
        Manager manager2 = new Manager(2, "Manager2", "passw0rd!", "manager2@test.com");
        userRepository.insert(manager1);
        userRepository.insert(manager2);

        HashMap<Manager, List<ResolvedRequest>> expectedResult = new HashMap<>();

        // Adds 10 requests to DB, 6 of which are resolved
        for (int i=1; i<11; i++) {
            ResolvedRequest resolvedRequest;

            PendingRequest pendingRequest = new PendingRequest(i, 1.0, "", "", i);
            requestRepository.insert(pendingRequest);

            if (i > 4 && i < 8) {
                // Resolve and update requests 5-7 by manager2
                resolvedRequest = new ResolvedRequest(pendingRequest, true, 2);
                requestRepository.update(resolvedRequest);

                expectedResult.computeIfAbsent(manager2, k -> new ArrayList<>());
                expectedResult.get(manager2).add(resolvedRequest);
            }
            if (i > 7) {
                // Resolve and update requests 8-10 by manager1
                resolvedRequest = new ResolvedRequest(pendingRequest, true, 1);
                requestRepository.update(resolvedRequest);

                // All resolved requests are expected to be returned
                expectedResult.computeIfAbsent(manager1, k -> new ArrayList<>());
                expectedResult.get(manager1).add(resolvedRequest);
            }
        }

        // Expect requests 5-10 to be returned
        Assert.assertEquals(expectedResult, managerServices.viewAllResolvedRequests());
    }

    @Test
    public void resolvingRequestWorkedProperly() {
        // Create a manager to pass into the method
        Manager manager = new Manager(2, "test", "test", "test");

        // Create and insert a pending request
        PendingRequest pr = new PendingRequest(1,1.3,"food", "", 1);
        requestRepository.insert(pr);

        // Resolve the request
        managerServices.resolveRequest(manager, 1, true);

        ResolvedRequest expectedResolved = new ResolvedRequest(1, 1.3, "food", "", 1, true, 2);
        ResolvedRequest actualResolved = (ResolvedRequest) requestRepository.get(1).get();

        Assert.assertEquals(expectedResolved, actualResolved);
    }

    @Test(expected = ResourceDoesNotExistException.class)
    public void resolvingShouldThrowExceptionForNonExistentRequest() {
        // Create a manager to pass into the method
        Manager manager = new Manager(2, "test", "test", "test");

        managerServices.resolveRequest(manager, 1, true);
    }

    @Test
    public void registeringEmployeeWorksCorrectly() {
        managerServices.registerEmployee("test");
        Employee actualEmployee = (Employee) userRepository.get("test").get();
        Employee expectedEmployee = new Employee(1, "test", "Password1!", "test@company.com");

        Assert.assertEquals(expectedEmployee, actualEmployee);
    }

    @Test(expected = UsernameAlreadyUsedException.class)
    public void registerThrowsExceptionWhenThatUsernameExists() {
        Employee employee = new Employee(1,"test", "Password1!", "test@company.com");
        userRepository.insert(employee);
        managerServices.registerEmployee("test");
    }

    @Test
    public void allPendingRequestsGotGot () {
        List<PendingRequest> expectedRequests = new ArrayList<>();

        for (int i=1; i<11; i++) {
            // Add 10 pending requests to the database
            PendingRequest pendingRequest = new PendingRequest(i, 1.0, "", "", i);
            requestRepository.insert(pendingRequest);

            if (i<4) {
                // Only add to expected if request isn't going to be resolved
                expectedRequests.add(pendingRequest);
            }
            else {
                // Resolve requests 4-10
                ResolvedRequest resolvedRequest = new ResolvedRequest(pendingRequest, true, 2);
                // Update the request to resolved in the DB
                requestRepository.update(resolvedRequest);
            }
        }
        // Expect requests 1-3 to be returned
        Assert.assertArrayEquals(expectedRequests.toArray(), managerServices.viewAllPendingRequests().toArray());
    }

}
