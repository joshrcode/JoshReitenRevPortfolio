package web.responses.manager;

import exceptions.RequestAlreadyResolvedException;
import web.responses.Response;

import javax.servlet.http.HttpServletResponse;

public class RequestAlreadyResolvedResponse extends Response {
    public RequestAlreadyResolvedResponse(HttpServletResponse resp, RequestAlreadyResolvedException ex) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        this.setMessage(ex.getMessage());
    }
}
