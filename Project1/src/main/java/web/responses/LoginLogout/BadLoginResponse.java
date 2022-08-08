package web.responses.LoginLogout;

import web.responses.Response;

import javax.servlet.http.HttpServletResponse;

public class BadLoginResponse extends Response {
    public BadLoginResponse(HttpServletResponse resp, RuntimeException ex) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        this.setMessage(ex.getMessage());
    }
}
