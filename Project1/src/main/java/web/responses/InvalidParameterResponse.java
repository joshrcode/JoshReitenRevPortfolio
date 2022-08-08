package web.responses;

import exceptions.InvalidParameterException;
import exceptions.MissingParameterException;
import exceptions.ParameterException;

import javax.servlet.http.HttpServletResponse;

public class InvalidParameterResponse extends Response {
    public InvalidParameterResponse(HttpServletResponse resp, ParameterException ex) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        this.setMessage(ex.getMessage());
    }
}
