package web.responses;

import javax.servlet.http.HttpServletResponse;

public class RequestSubmittedSuccessfullyResponse extends Response {
    public RequestSubmittedSuccessfullyResponse(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
        this.setMessage("Request Submitted Successfully!");
    }
}
