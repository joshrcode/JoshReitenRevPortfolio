package web.servlets;

import exceptions.AlreadyLoggedInException;
import exceptions.InvalidLogInException;
import exceptions.ResourceDoesNotExistException;
import models.Employee;
import models.Manager;
import models.User;
import services.UserService;
import services.ValidatorService;
import web.responses.LoginLogout.BadLoginResponse;
import web.responses.Response;
import web.responses.LoginLogout.SuccessfulLoginResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="LoginServlet", value="/login")
public class LoginServlet extends Servlet {

    UserService userService;

    /**
     * A convenience method which can be overridden so that there's no need
     * to call <code>super.init(config)</code>.
     *
     * <p>Instead of overriding {@link #init(ServletConfig)}, simply override
     * this method and it will be called by
     * <code>GenericServlet.init(ServletConfig config)</code>.
     * The <code>ServletConfig</code> object can still be retrieved via {@link
     * #getServletConfig}.
     *
     * @throws ServletException if an exception occurs that
     *                          interrupts the servlet's
     *                          normal operation
     */
    @Override
    public void init() throws ServletException {
        super.init();
        userService = (UserService) getServletContext().getAttribute("userService");
    }


    /**
     * Called by the server (via the <code>service</code> method)
     * to allow a servlet to handle a POST request.
     * <p>
     * The HTTP POST method allows the client to send
     * data of unlimited length to the Web server a single time
     * and is useful when posting information such as
     * credit card numbers.
     *
     * <p>When overriding this method, read the request data,
     * write the response headers, get the response's writer or output
     * stream object, and finally, write the response data. It's best
     * to include content type and encoding. When using a
     * <code>PrintWriter</code> object to return the response, set the
     * content type before accessing the <code>PrintWriter</code> object.
     *
     * <p>The servlet container must write the headers before committing the
     * response, because in HTTP the headers must be sent before the
     * response body.
     *
     * <p>Where possible, set the Content-Length header (with the
     * {@link ServletResponse#setContentLength} method),
     * to allow the servlet container to use a persistent connection
     * to return its response to the client, improving performance.
     * The content length is automatically set if the entire response fits
     * inside the response buffer.
     *
     * <p>When using HTTP 1.1 chunked encoding (which means that the response
     * has a Transfer-Encoding header), do not set the Content-Length header.
     *
     * <p>This method does not need to be either safe or idempotent.
     * Operations requested through POST can have side effects for
     * which the user can be held accountable, for example,
     * updating stored data or buying items online.
     *
     * <p>If the HTTP POST request is incorrectly formatted,
     * <code>doPost</code> returns an HTTP "Bad Request" message.
     *
     * @param req  an {@link HttpServletRequest} object that
     *             contains the request the client has made
     *             of the servlet
     * @param resp an {@link HttpServletResponse} object that
     *             contains the response the servlet sends
     *             to the client
     * @throws IOException      if an input or output error is
     *                          detected when the servlet handles
     *                          the request
     * @throws ServletException if the request for the POST
     *                          could not be handled
     * @see ServletOutputStream
     * @see ServletResponse#setContentType
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        updateCurrentUser();
        Response response;

        final String CURRENT_USER = "currentUser";


        try {
            if (currentUser != null) {
                throw new AlreadyLoggedInException(currentUser.getUsername());
            }
            else {
                String username;
                String password;

                // Get the username and password from the request
                username = req.getParameter("username");
                password = req.getParameter("password");

                if (username.isEmpty() || password.isEmpty()) {
                    throw new InvalidLogInException();
                }
                User user = userService.login(username, password);

                if (ValidatorService.isUserValid(user)) {
                    System.out.println(user);
                    // set current user
                    getServletContext().setAttribute("currentUser", user);
                    updateCurrentUser();

                    // If user is a manager, cast to a Manager
                    if (isManager()) getServletContext().setAttribute("currentUser", (Manager) user);
                        // Otherwise cast into an Employee
                    else getServletContext().setAttribute("currentUser", (Employee) user);

                    response = new SuccessfulLoginResponse(resp, username);
                }
                else {
                    throw new InvalidLogInException();
                }

            }
        }

        catch (AlreadyLoggedInException | InvalidLogInException | ResourceDoesNotExistException e) {
            response = new BadLoginResponse(resp, e);
        }

        respond(resp, response);

    }
}
