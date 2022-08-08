package web.responses.manager;

import models.Employee;
import models.PendingRequest;
import models.ResolvedRequest;
import web.responses.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewEmployeeResponse extends Response {

    List<PendingRequest> pendingRequests;
    List<ResolvedRequest> resolvedRequests;

    public ViewEmployeeResponse(HttpServletResponse resp,
                                List<PendingRequest> pendingRequests, List<ResolvedRequest> resolvedRequests, Employee employee) {
        resp.setStatus(HttpServletResponse.SC_OK);
        this.setMessage(employee.getUsername()+"'s Requests:");
        this.pendingRequests = pendingRequests;
        this.resolvedRequests = resolvedRequests;
    }

    public List<PendingRequest> getPendingRequests() {
        return pendingRequests;
    }

    public List<ResolvedRequest> getResolvedRequests() {
        return resolvedRequests;
    }


}
