package web.responses.employee;

import models.Employee;
import models.PendingRequest;
import models.ResolvedRequest;
import web.responses.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewResolvedRequestsResponse extends Response {
    List<ResolvedRequest> resolvedRequests;
    public ViewResolvedRequestsResponse(HttpServletResponse resp, List<ResolvedRequest> resolvedRequests, Employee employee) {
        this.resolvedRequests = resolvedRequests;
        resp.setStatus(HttpServletResponse.SC_OK);
        this.setMessage(employee.getUsername()+"'s resolved requests.");
    }

    public List<ResolvedRequest> getResolvedRequests() {
        return resolvedRequests;
    }
}
