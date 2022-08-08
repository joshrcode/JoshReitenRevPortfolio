package repositoryTests;

import exceptions.ResourceDoesNotExistException;
import models.*;
import org.apache.logging.log4j.core.Logger;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import repositories.RequestRepository;
import repositories.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestRepositoryTest {


    RequestRepository requestRepository;
    UserRepository userRepository;
    JdbcDataSource dataSource;


    @Before
    public void init() {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test_db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        requestRepository = new RequestRepository(dataSource);
        userRepository = new UserRepository(dataSource);
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

        userRepository.insert(new Employee(1,"","",""));

    }

    @After
    public void tearDown() throws SQLException {
        Connection connection = dataSource.getConnection();

        String dropSQL = "DROP TABLE IF EXISTS requests, users CASCADE;";

        PreparedStatement ps = connection.prepareStatement(dropSQL);
        ps.executeUpdate();
    }

     @Test
     public void testShouldGetConnection() {
         boolean connectionFailed = false;
         try {
             dataSource.getConnection();
         }
         catch (SQLException e) {
             connectionFailed = true;
         }
         boolean expectedResult = false;
         Assert.assertEquals(expectedResult, connectionFailed);
     }

    @Test
    public void shouldGetCorrectRequest() throws SQLException{
        // Create a request to upload to fake DB
        ReimbursementRequest expectedRequest = new PendingRequest(1,12.00,"food", "", 1);

        // Insert a test request
        requestRepository.insert(expectedRequest);

        // Retrieve the request
        Optional<ReimbursementRequest> actualRequest = requestRepository.get(expectedRequest.getId());

        // Compare actual and expected
        Assert.assertEquals(expectedRequest, actualRequest.get());
    }

    @Test
    public void optionalDoesNotReturnWhenMissing() throws SQLException{
        // Create a user to upload to fake DB
        PendingRequest addedRequest = new PendingRequest(1, 1.0, "","",1);

        // Insert a test request
        requestRepository.insert(addedRequest);

        // Retrieve the wrong request
        Optional<ReimbursementRequest> actualRequest = requestRepository.get(123456);
        Assert.assertFalse(actualRequest.isPresent());
    }

    @Test
    public void shouldInsertPendingRequestToTheDatabase() {
        // Create request
        PendingRequest expectedPending = new PendingRequest(1,1.0,"","",1);

        // Insert request
        requestRepository.insert(expectedPending);

        // Get request back from DB
        PendingRequest actualPending = (PendingRequest) requestRepository.get(expectedPending.getId()).get();

        // Expect retrieved request to match created request
        Assert.assertEquals(expectedPending, actualPending);
    }


    @Test
    public void shouldUpdatePendingRequestProperly() {
        // Create request and insert into DB
        PendingRequest expectedPending = new PendingRequest(1,1.0,"","",1);
        requestRepository.insert(expectedPending);

        // Change something on the request and update in DB
        expectedPending.setAmount(2.0);
        requestRepository.update(expectedPending);

        // Retrieve the updated request
        PendingRequest actualPending = (PendingRequest) requestRepository.get(1).get();

        // Compare retrieved request to created request
        Assert.assertEquals(expectedPending, actualPending);
    }

    @Test
    public void shouldUpdateResolvedRequestProperly() {
        // Create new request and upload to DB
        PendingRequest expectedPending = new PendingRequest(1,1.0,"", "",1);
        requestRepository.insert(expectedPending);

        // "Resolve" the request and update in DB
        ResolvedRequest expectedResolved = new ResolvedRequest(expectedPending, true, 2);
        requestRepository.update(expectedResolved);

        // Retrieve request back from DB
        ResolvedRequest actualResolved = (ResolvedRequest) requestRepository.get(1).get();

        // Compare retrieved request to created request
        Assert.assertEquals(expectedResolved, actualResolved);
    }

    @Test(expected = ResourceDoesNotExistException.class)
    public void updateShouldThrowExceptionIfResourceDoesNotExist() {
        // Create a request WITHOUT uploading to DB
        PendingRequest expectedPending = new PendingRequest(1,1.0,"", "",1);
        // Try to update a response that's not in the DB
        requestRepository.update(expectedPending);
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

        // Expect requests 4-7 to be returned
        Assert.assertArrayEquals(expectedRequests.toArray(), requestRepository.getAllPendingForEmployee(1).toArray());
    }
    @Test
    public void allPendingRequestsForManagerGotGot () {
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
        Assert.assertArrayEquals(expectedRequests.toArray(), requestRepository.getAllPendingForManager().toArray());
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

        // Expect requests 8-10 to be returned
        Assert.assertArrayEquals(expectedRequests.toArray(), requestRepository.getAllResolvedForEmployee(1).toArray());
    }
    @Test
    public void allResolvedRequestsForManagerGotGot () {
        List<ResolvedRequest> expectedRequests = new ArrayList<>();

        // Adds 10 requests to DB, 6 of which are resolved
        for (int i=1; i<11; i++) {
            ResolvedRequest resolvedRequest;

            PendingRequest pendingRequest = new PendingRequest(i, 1.0, "", "", i);
            requestRepository.insert(pendingRequest);

            if (i > 4) {

                // Resolve and update requests 5-10
                resolvedRequest = new ResolvedRequest(pendingRequest, true, 3);
                requestRepository.update(resolvedRequest);

                // All resolved requests are expected to be returned
                expectedRequests.add(resolvedRequest);
            }
        }

        // Expect requests 5-10 to be returned
        Assert.assertArrayEquals(expectedRequests.toArray(), requestRepository.getAllResolvedForManager().toArray());
    }

    @Test
    public void getMaxRequestNumberWorks() {
        // Add 10 requests to the database
        for (int i=1; i<11; i++) {
            PendingRequest pendingRequest = new PendingRequest(i, 1.0, "", "", i);
            requestRepository.insert(pendingRequest);
        }

        // Last id added was 10. Expect 10 to be max
        int expectedMax = 10;
        int actualMax = requestRepository.getMaxRequestId();

        Assert.assertEquals(expectedMax, actualMax);
    }
    @Test
    public void getMaxShouldReturnZeroIfDatabaseIsEmpty() {
        // Add 10 requests to the database


        // Last id added was 10. Expect 10 to be max

        int max = requestRepository.getMaxRequestId();

        Assert.assertEquals(0, max);
    }

    @Test
    public void isResolvedWorksProperly() {
        Employee employee = new Employee(1, "employee", "test", "test@test.com");
        userRepository.insert(employee);
        Manager manager = new Manager(2, "manager", "test", "test@test.com");
        userRepository.insert(manager);

        PendingRequest pendingRequest = new PendingRequest(1,1.9, "food", "none", 1);
        requestRepository.insert(pendingRequest);
        PendingRequest pendingRequest2 = new PendingRequest(2,3.6, "travel", "some", 1);
        requestRepository.insert(pendingRequest2);
        ResolvedRequest resolvedRequest = new ResolvedRequest(pendingRequest2, true, 2);
        requestRepository.update(resolvedRequest);

        Assert.assertTrue(requestRepository.isResolved(2));
        Assert.assertFalse(requestRepository.isResolved(1));
    }

    @Test(expected = ResourceDoesNotExistException.class)
    public void isResolvedThrowsExceptionWhenUsernameCannotBeLocated() {
        requestRepository.isResolved(45312);
    }


}