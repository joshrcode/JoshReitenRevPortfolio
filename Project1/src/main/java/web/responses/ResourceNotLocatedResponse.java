package web.responses;

import exceptions.ResourceDoesNotExistException;

import javax.servlet.http.HttpServletResponse;

public class ResourceNotLocatedResponse extends Response {
    public ResourceNotLocatedResponse(HttpServletResponse resp, ResourceDoesNotExistException ex) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        this.setMessage(ex.getMessage());
    }
}
