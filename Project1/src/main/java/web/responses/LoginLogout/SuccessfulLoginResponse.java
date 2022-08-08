package web.responses.LoginLogout;

import web.responses.Response;

import javax.servlet.http.HttpServletResponse;

public class SuccessfulLoginResponse extends Response {
    public SuccessfulLoginResponse(HttpServletResponse resp, String username) {
        resp.setStatus(HttpServletResponse.SC_OK);
        this.setMessage("Hello "+username+". You have logged in successfully!");
    }
}
