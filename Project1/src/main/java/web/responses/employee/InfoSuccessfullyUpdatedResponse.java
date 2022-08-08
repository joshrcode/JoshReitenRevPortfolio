package web.responses.employee;

import web.responses.Response;

import javax.servlet.http.HttpServletResponse;

public class InfoSuccessfullyUpdatedResponse extends Response {
    public InfoSuccessfullyUpdatedResponse(HttpServletResponse resp, String parameter) {
        resp.setStatus(HttpServletResponse.SC_OK);
        this.setMessage(parameter+" was successfully updated!");
    }
}
