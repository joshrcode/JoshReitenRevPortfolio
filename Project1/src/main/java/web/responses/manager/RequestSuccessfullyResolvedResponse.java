package web.responses.manager;

import web.responses.Response;

import javax.servlet.http.HttpServletResponse;

public class RequestSuccessfullyResolvedResponse extends Response {
    public RequestSuccessfullyResolvedResponse(HttpServletResponse resp, int id, boolean isApproved) {
        String approvalStatusMessage;
        if(isApproved){
            approvalStatusMessage = "approved!";
        } else {
            approvalStatusMessage = "denied.";
        }
        this.setMessage("Request ID: "+id+" was "+approvalStatusMessage);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
