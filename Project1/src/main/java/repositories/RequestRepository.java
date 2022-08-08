package repositories;

import exceptions.ResourceAlreadyExistsException;
import exceptions.ResourceDoesNotExistException;
import models.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestRepository implements RepositoryInterface<ReimbursementRequest, Integer>{
    private final DataSource dataSource;
    private final static String SQLEXMSG = "SQL Exception Caught";


    public RequestRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<ReimbursementRequest> get(Integer id) throws ResourceDoesNotExistException {

        // Set up
        UserRepository userRepository = new UserRepository(dataSource);
        Connection connection = null;
        ReimbursementRequest request = null;

        try {

            // Get a user with username ....
            String sql = "select * from requests where request_id = ?;";

            // Establish connection, prepare, then execute query
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                // construct the request
                int requestId = rs.getInt("request_id");
                int requesterId = rs.getInt("requester_id");
                double amount = rs.getDouble("amount");
                String type = rs.getString("type");
                String extraInfo = rs.getString("extra_info");
                boolean isReviewed = rs.getBoolean("is_reviewed");
                boolean isApproved = rs.getBoolean("is_approved");
                int reviewerId = rs.getInt("reviewer_id");


                if (isReviewed) {

                    request = new ResolvedRequest(requestId, amount, type,  extraInfo, requesterId, isApproved, reviewerId);
                }
                else {
                    request = new PendingRequest(requestId, amount, type, extraInfo, requesterId);
                }

                // Return request if one was found, else return null
                Optional<ReimbursementRequest> requestOptional = Optional.of(request);
                if (requestOptional.isPresent()) {
                    return Optional.of(request);
                }
                else throw new ResourceDoesNotExistException("Request id ("+id+")");
            }
        }
        catch (SQLException e) {
            System.out.println(SQLEXMSG);
        }
        finally {
            closeConnection(connection);
        }

        return Optional.ofNullable(request);
    }

    @Override
    public void update(ReimbursementRequest request) throws ResourceDoesNotExistException {
        Connection connection = null;

        int requestId = request.getId();
        int requesterId = request.getRequesterId();
        double amount = request.getAmount();
        String type = request.getType();
        String extraInfo = request.getExtraInfo();
        boolean isReviewed = false;
        boolean isApproved = false;
        int reviewerId = 0;

        if (request.getClass().equals(ResolvedRequest.class)) {

            ResolvedRequest resolvedRequest = new ResolvedRequest((ResolvedRequest) request);

            isReviewed = true;
            isApproved = resolvedRequest.isApproved();
            reviewerId = resolvedRequest.getReviewerId();
        }

        try {

            // "insert this user into 'users' table"
            String sql = "update requests set " +
                    "request_id = ?, requester_id = ?, amount = ?, type = ?, " +
                    "extra_info = ?, is_reviewed = ?, is_approved = ?, reviewer_id = ? " +
                    "where request_id = ?;";

            // establish connection, prepare, then execute update
            connection = dataSource.getConnection();

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, requestId);
            ps.setInt(2, requesterId);
            ps.setDouble(3, amount);
            ps.setString(4, type);
            ps.setString(5, extraInfo);
            ps.setBoolean(6, isReviewed);
            ps.setBoolean(7, isApproved);
            ps.setInt(8, reviewerId);
            ps.setInt(9, requestId);

            int numOfRowsAffected = ps.executeUpdate();
            if (numOfRowsAffected<1) {
                throw new ResourceDoesNotExistException("Request id ("+request.getId()+")");
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
    public void insert(ReimbursementRequest request) throws ResourceAlreadyExistsException {
        Connection connection = null;

        try {

            // "insert this request into 'requests' table"
            String sql = "insert into requests (requester_id, amount, type, extra_info) values (?, ?, ?, ?);";

            // establish connection, prepare, then execute update
            connection = dataSource.getConnection();

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, request.getRequesterId());
            ps.setDouble(2, request.getAmount());
            ps.setString(3, request.getType());
            ps.setString(4,request.getExtraInfo());

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

    public List<PendingRequest> getAllPendingForManager() {
        Connection connection = null;

        PendingRequest pr;
        List<PendingRequest> pendingRequests = new ArrayList<>();

        try {

            String sql = "select request_id, amount, type, extra_info, requester_id from requests " +
                    "where is_reviewed = false order by request_id;";

            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int requestId;
            double amount;
            String type;
            String extraInfo;
            int requesterId;


            while (rs.next()) {
                requestId = rs.getInt("request_id");
                amount = rs.getDouble("amount");
                type = rs.getString("type");
                extraInfo = rs.getString("extra_info");
                requesterId = rs.getInt("requester_id");

                pendingRequests.add(new PendingRequest(requestId, amount, type, extraInfo, requesterId));
            }

            return  pendingRequests;
        }
        // Exception handling
        catch (SQLException e) {
            System.out.println(SQLEXMSG + e);
        }

        // finally block used to close connection
        finally { closeConnection(connection); }

        return pendingRequests;
    }

    public List<ResolvedRequest> getAllResolvedForManager() {
        Connection connection = null;

        ResolvedRequest rr;
        List<ResolvedRequest> resolvedRequests = new ArrayList<>();

        try {

            String sql = "select request_id, amount, type, extra_info, requester_id, is_approved, reviewer_id from requests " +
                    "where is_reviewed = true " +
                    "order by reviewer_id, request_id;";

            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            int requestId;
            double amount;
            String type;
            String extraInfo;
            int requesterId;
            boolean isApproved;
            int reviewerId;

            while (rs.next()) {
                requestId = rs.getInt("request_id");
                amount = rs.getDouble("amount");
                type = rs.getString("type");
                extraInfo = rs.getString("extra_info");
                requesterId = rs.getInt("requester_id");
                isApproved = rs.getBoolean("is_approved");
                reviewerId = rs.getInt("reviewer_id");

                rr = new ResolvedRequest(requestId, amount, type, extraInfo, requesterId, isApproved, reviewerId);
                resolvedRequests.add(new ResolvedRequest(requestId, amount, type, extraInfo, requesterId, isApproved, reviewerId));
            }

            return  resolvedRequests;
        }
        // Exception handling
        catch (SQLException e) {
            System.out.println(SQLEXMSG + e);
        }

        // finally block used to close connection
        finally { closeConnection(connection); }

        return resolvedRequests;
    }

    public List<PendingRequest> getAllPendingForEmployee(int employeeId) {
        Connection connection = null;

        PendingRequest pr;
        List<PendingRequest> pendingRequests = new ArrayList<>();

        try {

            String sql = "select request_id, amount, type, extra_info from requests " +
                    "where requester_id = ? and is_reviewed = false;";

            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();

            int requestId;
            double amount;
            String type;
            String extraInfo;

            while (rs.next()) {
                requestId = rs.getInt("request_id");
                amount = rs.getDouble("amount");
                type = rs.getString("type");
                extraInfo = rs.getString("extra_info");

                pendingRequests.add(new PendingRequest(requestId, amount, type, extraInfo, employeeId));
            }

            return  pendingRequests;
        }
        // Exception handling
        catch (SQLException e) {
            System.out.println(SQLEXMSG + e);
        }

        // finally block used to close connection
        finally { closeConnection(connection); }

        return pendingRequests;
    }

    public List<ResolvedRequest> getAllResolvedForEmployee(int employeeId) {
        Connection connection = null;

        ResolvedRequest rr;
        List<ResolvedRequest> resolvedRequests = new ArrayList<>();

        try {

            String sql = "select request_id, amount, type, extra_info, is_approved, reviewer_id from requests " +
                    "where requester_id = ? and is_reviewed = true " +
                    "order by request_id;";

            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();

            int requestId;
            double amount;
            String type;
            String extraInfo;
            boolean isApproved;
            int reviewerId;

            while (rs.next()) {
                requestId = rs.getInt("request_id");
                amount = rs.getDouble("amount");
                type = rs.getString("type");
                extraInfo = rs.getString("extra_info");
                isApproved = rs.getBoolean("is_approved");
                reviewerId = rs.getInt("reviewer_id");

                rr = new ResolvedRequest(requestId, amount, type, extraInfo, employeeId, isApproved, reviewerId);
                resolvedRequests.add(new ResolvedRequest(requestId, amount, type, extraInfo, employeeId, isApproved, reviewerId));
            }

            return  resolvedRequests;
        }

        // Exception handling
        catch (SQLException e) {
            System.out.println(SQLEXMSG + e);
        }

        // finally block used to close connection
        finally { closeConnection(connection); }

        return resolvedRequests;
    }

    public int getMaxRequestId() {
        // Set up
        Connection connection = null;
        int max = 0;

        try {

            // Get a user with username ....
            String sql = "select request_id from requests order by request_id desc limit 1;";

            // Establish connection, prepare, then execute query
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                // assign max to its value
                max = rs.getInt("request_id");
            }
        }
        catch (SQLException e) {
            System.out.println(SQLEXMSG);
        }
        finally {closeConnection(connection);}

        return max;
    }

    public boolean isResolved(int id) throws ResourceDoesNotExistException {

        // Set up
        Connection connection = null;
        boolean isResolved = false;

        try {
            // Get a request via id
            String sql = "select is_reviewed from requests where request_id = ?;";

            // Establish connection, prepare, then execute query
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            rs.next();
            isResolved = rs.getBoolean("is_reviewed");
        }
        catch (SQLException e) {
            System.out.println(SQLEXMSG);
            throw new ResourceDoesNotExistException("Request ID: "+id);
        }
        finally {closeConnection(connection);}
        return isResolved;
    }




    private void closeConnection(Connection connection) {
        if (connection != null) {
            try { connection.close(); }

            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
