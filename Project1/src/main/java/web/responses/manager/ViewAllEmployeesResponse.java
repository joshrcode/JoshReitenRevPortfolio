package web.responses.manager;

import models.Employee;
import web.responses.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewAllEmployeesResponse extends Response {
    List<Employee> employees;
    public ViewAllEmployeesResponse(HttpServletResponse resp, List<Employee> employees) {
        resp.setStatus(HttpServletResponse.SC_OK);
        this.setMessage("All Employees (sorted by ID):");
        this.employees = employees;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}
