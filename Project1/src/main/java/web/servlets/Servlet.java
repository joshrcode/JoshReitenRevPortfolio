package web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Employee;
import models.Manager;
import models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.EmployeeService;
import services.ManagerService;
import web.responses.Response;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class Servlet extends HttpServlet {
    private static Logger logger = LogManager.getLogger(Servlet.class);
    ObjectMapper objectMapper;
    User currentUser;
    EmployeeService employeeService;
    ManagerService managerService;

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
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
        currentUser = (User) getServletContext().getAttribute("currentUser");
        employeeService = (EmployeeService) getServletContext().getAttribute("employeeService");
        managerService = (ManagerService) getServletContext().getAttribute("managerService");
        logger.info("{} initializing", this.getClass().getSimpleName());
    }

    public void updateCurrentUser() {
        currentUser = (User) getServletContext().getAttribute("currentUser");
    }

    public void updateCurrentUser(Employee employee) {
        getServletContext().setAttribute("currentUser", employee);
    }
    public void updateCurrentUser(Manager manager) {
        getServletContext().setAttribute("currentUser", manager);
    }

    public boolean isManager() {
        return employeeService.getUserRepository().isManager(currentUser.getUsername());
    }
    public boolean isResolved(int id) {
        return employeeService.getRequestRepository().isResolved(id);
                //.getUserRepository().isManager(currentUser.getUsername());
    }

    public void respond(HttpServletResponse resp, Response response) {
        try {
            resp.getWriter().write(objectMapper.writeValueAsString(response));
        } catch (IOException e) {
            System.out.println("Exception Handled");
        }
    }
}
