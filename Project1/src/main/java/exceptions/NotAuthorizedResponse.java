package exceptions;

import web.responses.Response;

import javax.servlet.http.HttpServletResponse;

public class NotAuthorizedResponse extends Response {
    public NotAuthorizedResponse(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        this.setMessage("You are not authorized to use this feature.");
    }
}
