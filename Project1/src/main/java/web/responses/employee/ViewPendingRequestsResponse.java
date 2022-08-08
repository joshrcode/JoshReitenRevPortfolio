package web.responses.employee;

import models.Employee;
import models.PendingRequest;
import web.responses.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewPendingRequestsResponse extends Response {
    List<PendingRequest> pendingRequests;
    public ViewPendingRequestsResponse(HttpServletResponse resp, List<PendingRequest> pendingRequests, Employee employee) {
        this.pendingRequests = pendingRequests;
        resp.setStatus(HttpServletResponse.SC_OK);
        this.setMessage(employee.getUsername()+"'s pending requests.");
    }

    public List<PendingRequest> getPendingRequests() {
        return pendingRequests;
    }
}
