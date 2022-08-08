package web.context;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import models.Employee;
import models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import repositories.RequestRepository;
import repositories.UserRepository;
import services.EmployeeService;
import services.ManagerService;
import services.UserService;

import java.util.Optional;


@WebListener()
public class ContextListener implements ServletContextListener {
    private static Logger logger = LogManager.getLogger(ContextListener.class);

    @Resource(name="jdbc/database", authenticationType=AuthenticationType.CONTAINER)
    private DataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        logger.info("database URL = {}", servletContextEvent.getServletContext().getInitParameter("jdbcUrl"));

        ObjectMapper objectMapper = new ObjectMapper();
        servletContextEvent.getServletContext().setAttribute("objectMapper", objectMapper);
        logger.info("ObjectMapper: " + servletContextEvent.getServletContext().getAttribute("objectMapper").toString());

        UserRepository userRepository = new UserRepository(dataSource);
        servletContextEvent.getServletContext().setAttribute("userRepository", userRepository);

        RequestRepository requestRepository = new RequestRepository(dataSource);
        servletContextEvent.getServletContext().setAttribute("requestRepository", requestRepository);

        UserService userService = new UserService(userRepository, requestRepository);
        servletContextEvent.getServletContext().setAttribute("userService", userService);

        EmployeeService employeeService = new EmployeeService(userRepository, requestRepository);
        servletContextEvent.getServletContext().setAttribute("employeeService", employeeService);

        ManagerService managerService = new ManagerService(userRepository, requestRepository);
        servletContextEvent.getServletContext().setAttribute("managerService", managerService);

        User user = null;
        servletContextEvent.getServletContext().setAttribute("currentUser", user);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("destroying context");
    }
}