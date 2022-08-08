package web.responses.manager;

import models.Manager;
import models.PendingRequest;
import models.ResolvedRequest;
import web.responses.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

public class ViewAllResolvedRequestsResponse extends Response {
    HashMap<Manager, List<ResolvedRequest>> allResolvedPerManager;
    public ViewAllResolvedRequestsResponse(HttpServletResponse resp, HashMap<Manager, List<ResolvedRequest>> allResolvedPerManager) {
        this.allResolvedPerManager = allResolvedPerManager;
        resp.setStatus(HttpServletResponse.SC_OK);
        this.setMessage("All employees' resolved requests per manager");
    }

    public HashMap<Manager, List<ResolvedRequest>> getAllResolvedPerManager() {
        return allResolvedPerManager;
    }
}
