package web.servlets;

import exceptions.*;
import models.Employee;
import web.responses.InvalidParameterResponse;
import web.responses.LoginLogout.NotLoggedInResponse;
import web.responses.ResourceNotLocatedResponse;
import web.responses.Response;
import web.responses.employee.InfoSuccessfullyUpdatedResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="UpdateInfoServlet", value="/update/*")
public class UpdateInfoServlet extends Servlet {
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

        String parameter = req.getRequestURI().split("/")[2];

        try {
            // Login check
            if (currentUser == null) throw new NotLoggedInException();
            Employee employee = (Employee) currentUser;

            boolean isValid;
            String newParameter;

            // Get the parameter name from the req URI
            try {
                newParameter = req.getParameter(parameter);
            }
            catch (NullPointerException e) {
                throw new InvalidParameterException(parameter);
            }

            try {
                if (newParameter.isEmpty() || newParameter == null) {
                    throw new InvalidParameterException(parameter);
                }
                else {
                    try {
                        if (parameter.equalsIgnoreCase("username")) isValid = employeeService.updateMyUsername(employee, newParameter);
                        else if (parameter.equalsIgnoreCase("password")) isValid = employeeService.updateMyPassword(employee, newParameter);
                        else if (parameter.equalsIgnoreCase("email")) isValid = employeeService.updateMyEmail(employee, newParameter);
                        else throw new InvalidParameterException(parameter);

                        if (isValid) {
                            response = new InfoSuccessfullyUpdatedResponse(resp, parameter);
                            updateCurrentUser(employee);
                        } else {
                            response = new InvalidParameterResponse(resp, new InvalidParameterException(parameter));
                        }

                    }
                    catch (UsernameAlreadyUsedException | InvalidParameterException uauipe) {
                        response = new InvalidParameterResponse(resp, uauipe);
                    }
                }
            }
            catch (InvalidParameterException ipe){
                response = new InvalidParameterResponse(resp, ipe);
            }
            catch (NullPointerException e){
                try{
                    throw new MissingParameterException(parameter);
                }
                catch (MissingParameterException mpex) {
                    response = new InvalidParameterResponse(resp, mpex);
                }
            }
        }
        catch (NotLoggedInException nlie) {
            response = new NotLoggedInResponse(resp);
        }
        catch (ResourceDoesNotExistException re) {
            response = new ResourceNotLocatedResponse(resp, re);
        }
        catch (InvalidParameterException sipe) {
            response = new InvalidParameterResponse(resp, sipe);
        }
        // Send response
        respond(resp, response);
    }
}

