package services;

import exceptions.PendingRequests.InvalidAmountException;
import exceptions.PendingRequests.InvalidExtraInfoException;
import exceptions.PendingRequests.InvalidTypeException;
import exceptions.UsernameAlreadyUsedException;
import models.Employee;
import models.PendingRequest;
import models.ResolvedRequest;
import repositories.RequestRepository;
import repositories.UserRepository;

import java.util.List;

public class EmployeeService extends UserService {
    UserRepository userRepository;
    RequestRepository requestRepository;

    public EmployeeService(UserRepository userRepository, RequestRepository requestRepository) {
        super(userRepository, requestRepository);
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;

    }

    // *** Might return a JSON object later on in development ***
    public String viewMyInfo(Employee employee){
        return "Username: " +employee.getUsername()+ ", Email: " +employee.getEmail();}


    // *** Might return a JSON object later on in development ***
    public boolean updateMyEmail(Employee employee, String email) {
        String preUpdatedEmail = employee.getEmail();
        // Save parameters into new variables
        String updatedEmail = email;

        // If any email was blank, it wasn't "updated"
        // Retain employees original details

        if (email.equals("")) {
            updatedEmail = employee.getEmail();
        }

        // Set all variables to employee
        employee.setEmail(updatedEmail);


        // Validate employee and if success save to DB
        if (ValidatorService.isUserValid(employee)) {
            userRepository.update(employee);
            return true;
        }
        employee.setEmail(preUpdatedEmail);
        return false;
    }
    public boolean updateMyPassword(Employee employee, String password) {
        String preUpdatedPassword = employee.getPassword();
        // Save password into new variable
        String updatedPassword = password;


        // If any password blank, it wasn't "updated"
        // Retain employees original details
        if (password.equals("")) {
            updatedPassword = employee.getPassword();
        }

        // Set all variables to employee
        employee.setPassword(updatedPassword);

        // Validate employee and if success save to DB
        if (ValidatorService.isUserValid(employee)) {
            userRepository.update(employee);
            return true;
        }
        employee.setPassword(preUpdatedPassword);
        return false;
    }
    public boolean updateMyUsername(Employee employee, String username) throws UsernameAlreadyUsedException {
        String preUpdateUsername = employee.getUsername();
        // Save username into new variable
        String updatedUsername = username;


        // If any username blank, it wasn't "updated"
        // Retain employees original details
        if (username.equals("")) {
            updatedUsername = employee.getUsername();
        }

        // Set username to employee
        employee.setUsername(updatedUsername);

        if (!userRepository.isUsernameAvailable(username)) {
            int currentUserIdWithUsername = userRepository.get(username).get().getId();

            if (currentUserIdWithUsername != employee.getId()) {
                throw new UsernameAlreadyUsedException(username);
            }
        }

        // Validate employee
        if (ValidatorService.isUserValid(employee)) {
            // If valid save to DB
            userRepository.update(employee);
            return true;
        }
        // Username wasn't valid
        employee.setUsername(preUpdateUsername);
        return false;
    }

    public boolean submitRequest(Employee loggedInEmployee, double amount, String type, String extraInfo)
            throws InvalidAmountException, InvalidTypeException, InvalidExtraInfoException {
        // Get the currently highest request number
        int maxRequestId = requestRepository.getMaxRequestId();

        // Create the new request
        PendingRequest pendingRequest = new PendingRequest(++maxRequestId, amount, type, extraInfo, loggedInEmployee.getId());

        // Validate request
        if (ValidatorService.isRequestValid(pendingRequest)) {
            // Upload request to database.
            requestRepository.insert(pendingRequest);
            // Return true to indicate it was successfully created and added to DB.
            return true;
        }
        // If it could not be validated, return false
        return false;
    }

    public List<PendingRequest> viewMyPendingRequests(Employee employee) {
        return requestRepository.getAllPendingForEmployee(employee.getId());
    }
    public List<ResolvedRequest> viewMyResolvedRequests(Employee employee) {
        return requestRepository.getAllResolvedForEmployee(employee.getId());
    }



}
