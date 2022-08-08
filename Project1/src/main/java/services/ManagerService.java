package services;

import exceptions.ResourceDoesNotExistException;
import exceptions.UsernameAlreadyUsedException;
import models.*;
import repositories.RequestRepository;
import repositories.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ManagerService extends UserService {

    UserRepository userRepository;
    RequestRepository requestRepository;

    public ManagerService(UserRepository userRepository, RequestRepository requestRepository) {
        super(userRepository, requestRepository);
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public List<Employee> viewAllEmployees() {
        return userRepository.getAllEmployees();
    }

    public HashMap<Manager, List<ResolvedRequest>> viewAllResolvedRequests() {
        List<ResolvedRequest> allResolvedRequests = requestRepository.getAllResolvedForManager();
        HashMap<Manager, List<ResolvedRequest>> resolvedRequestsPerManager = new HashMap<>();
        allResolvedRequests.stream()
                .forEach(request -> {
                    int managerId = request.getReviewerId();
                    Manager manager = (Manager) userRepository.getByID(managerId).get();
                    resolvedRequestsPerManager.computeIfAbsent(manager, k -> new ArrayList<>());
                    resolvedRequestsPerManager.get(manager).add(request);
                });
        return resolvedRequestsPerManager;
    }

    public void resolveRequest(Manager manager, int requestId, boolean approved) throws ResourceDoesNotExistException {
        Optional<ReimbursementRequest> opr = requestRepository.get(requestId);
        PendingRequest pr;
        if (opr.isPresent()) {
            pr = (PendingRequest) opr.get();
        } else {
            throw new ResourceDoesNotExistException("Request id (" +requestId+")");
        }
        ResolvedRequest resolved = new ResolvedRequest(pr, approved, manager.getId());
        requestRepository.update(resolved);
    }



    public void registerEmployee(String username) throws UsernameAlreadyUsedException {
        Employee employee;
        if (userRepository.isUsernameAvailable(username)) {
            employee = new Employee(0, username, "Password1!", username+"@company.com");
        } else {
            throw new UsernameAlreadyUsedException(username);
        }
        if (ValidatorService.isUserValid(employee)) {
            userRepository.insert(employee);
            int id = userRepository.get(username).get().getId();
            employee.setId(id);
        }
    }

    public List<PendingRequest> viewAllPendingRequests() {
        return requestRepository.getAllPendingForManager();
    }

}
