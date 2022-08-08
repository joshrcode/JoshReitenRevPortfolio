package web.responses.LoginLogout;

import web.responses.Response;

import javax.servlet.http.HttpServletResponse;

public class LoggedOutResponse extends Response {
    public LoggedOutResponse(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
        this.setMessage("You have logged out successfully! Goodbye!");
    }
}
