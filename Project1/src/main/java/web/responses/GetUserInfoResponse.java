package web.responses;

import models.Employee;
import models.User;

import javax.servlet.http.HttpServletResponse;

public class GetUserInfoResponse extends Response {
    public GetUserInfoResponse(HttpServletResponse resp, User user) {
        resp.setStatus(HttpServletResponse.SC_OK);
        this.setMessage("Username: " +user.getUsername()+ ", Email: " +user.getEmail());
    }
}
