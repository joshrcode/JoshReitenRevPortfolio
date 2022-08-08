package web.servlets;

import exceptions.*;
import models.Employee;
import models.Manager;
import web.responses.InvalidParameterResponse;
import web.responses.LoginLogout.NotLoggedInResponse;
import web.responses.ResourceNotLocatedResponse;
import web.responses.Response;
import web.responses.employee.InfoSuccessfullyUpdatedResponse;
import web.responses.manager.RequestAlreadyResolvedResponse;
import web.responses.manager.RequestSuccessfullyResolvedResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="ResolveServlet", value="/resolve/*")
public class ResolveServlet extends Servlet {
    /**
     * Called by the server (via the <code>service</code> method)
     * to allow a servlet to handle a PUT request.
     * <p>
     * The PUT operation allows a client to
     * place a file on the server and is similar to
     * sending a file by FTP.
     *
     * <p>When overriding this method, leave intact
     * any content headers sent with the request (including
     * Content-Length, Content-Type, Content-Transfer-Encoding,
     * Content-Encoding, Content-Base, Content-Language, Content-Location,
     * Content-MD5, and Content-Range). If your method cannot
     * handle a content header, it must issue an error message
     * (HTTP 501 - Not Implemented) and discard the request.
     * For more information on HTTP 1.1, see RFC 2616
     * <a href="http://www.ietf.org/rfc/rfc2616.txt"></a>.
     *
     * <p>This method does not need to be either safe or idempotent.
     * Operations that <code>doPut</code> performs can have side
     * effects for which the user can be held accountable. When using
     * this method, it may be useful to save a copy of the
     * affected URL in temporary storage.
     *
     * <p>If the HTTP PUT request is incorrectly formatted,
     * <code>doPut</code> returns an HTTP "Bad Request" message.
     *
     * @param req  the {@link HttpServletRequest} object that
     *             contains the request the client made of
     *             the servlet
     * @param resp the {@link HttpServletResponse} object that
     *             contains the response the servlet returns
     *             to the client
     * @throws IOException      if an input or output error occurs
     *                          while the servlet is handling the
     *                          PUT request
     * @throws ServletException if the request for the PUT
     *                          cannot be handled
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response response = null;
        int id = 0;

        try {
            if (currentUser == null) throw new NotLoggedInException();
            if (isManager()) {
                // Do manager stuff

                // This section is dedicated to retrieving the "isApproved" status from the URI parameters
                String strIsApproved;
                boolean isApproved;

                // This is a string that SHOULD be 'true' or 'false' -disregarding case
                strIsApproved = req.getParameter("isApproved");

                // If it is NOT 'true' or 'false' throw exception
                try {
                    if (!strIsApproved.equalsIgnoreCase("true") &&
                            !strIsApproved.equalsIgnoreCase("false")) {
                        throw new InvalidParameterException("isApproved");
                    }
                    else {
                        isApproved = Boolean.parseBoolean(strIsApproved);
                    }

                }
                catch (NullPointerException e){
                    throw new MissingParameterException("isApproved");
                }

                // This section obtains the id of the request based on the uri
                try{
                    // get everything after /resolve/
                    String uri = req.getRequestURI().split("/")[2];
                    id = Integer.parseInt(uri);
                }catch (Exception ex) {
                    ex.printStackTrace();
                    id = 0;
                }
                if (id == 0) {
                    throw new ResourceDoesNotExistException("Request ID: "+id);
                }
                if (this.isResolved(id)) {
                    throw new RequestAlreadyResolvedException(id);
                }
                else {
                    managerService.resolveRequest((Manager) currentUser, id, isApproved);
                    response = new RequestSuccessfullyResolvedResponse(resp, id, isApproved);
                }
            }
            // If current user is Employee
            else {
                response = new NotAuthorizedResponse(resp);
            }

        }
        catch (MissingParameterException mpe) {
            response = new InvalidParameterResponse(resp, mpe);
        }
        catch (RequestAlreadyResolvedException rare) {
            response = new RequestAlreadyResolvedResponse(resp, rare);
        }
        catch (NotLoggedInException nlie) {
            response = new NotLoggedInResponse(resp);
        }
        catch (ResourceDoesNotExistException re) {
            response = new ResourceNotLocatedResponse(resp, re);
        }
        catch (InvalidParameterException ipe) {
            response = new InvalidParameterResponse(resp, ipe);
        }
        // Send response
        respond(resp, response);
    }
}

