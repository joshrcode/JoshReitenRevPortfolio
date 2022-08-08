package web.responses.LoginLogout;

import web.responses.Response;

import javax.servlet.http.HttpServletResponse;

public class NotLoggedInResponse extends Response {
    public NotLoggedInResponse(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        this.setMessage("Please log in to use this feature.");
    }
}
