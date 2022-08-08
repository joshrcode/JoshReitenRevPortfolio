package services;

import exceptions.InvalidLogInException;
import exceptions.ResourceDoesNotExistException;
import models.User;
import repositories.RequestRepository;
import repositories.UserRepository;

public class UserService {
    UserRepository userRepository;
    RequestRepository requestRepository;

    public UserService(UserRepository userRepository, RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public RequestRepository getRequestRepository() {
        return requestRepository;
    }

    public User login(String username, String password) throws ResourceDoesNotExistException, InvalidLogInException {
        boolean loginSuccess = false;
        User user = null;

        // If Successful Login
        if (password.equals(userRepository.getPassword(username))) {
            user = userRepository.get(username).get();
            return user;
        }
        else {
            throw new InvalidLogInException();
        }
    }

    public void logout() {
        return;
    }





}
