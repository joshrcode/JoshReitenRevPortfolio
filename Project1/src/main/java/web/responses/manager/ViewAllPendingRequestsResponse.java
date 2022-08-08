package web.responses.manager;

import models.Employee;
import models.PendingRequest;
import web.responses.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewAllPendingRequestsResponse extends Response {
    List<PendingRequest> pendingRequests;
    public ViewAllPendingRequestsResponse(HttpServletResponse resp, List<PendingRequest> pendingRequests) {
        this.pendingRequests = pendingRequests;
        resp.setStatus(HttpServletResponse.SC_OK);
        this.setMessage("All employees' pending requests");
    }

    public List<PendingRequest> getPendingRequests() {
        return pendingRequests;
    }
}
