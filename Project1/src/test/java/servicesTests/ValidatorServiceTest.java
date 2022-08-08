package servicesTests;

import exceptions.PendingRequests.InvalidAmountException;
import exceptions.PendingRequests.InvalidExtraInfoException;
import exceptions.PendingRequests.InvalidTypeException;
import exceptions.PendingRequests.PendingRequestException;
import models.Employee;
import models.PendingRequest;
import org.junit.Assert;
import org.junit.Test;
import services.ValidatorService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidatorServiceTest {
    @Test
    public void userValidatorWorksCorrectly() {
        Employee validEmployee = new Employee(1, "user", "pAssword1!", "ab@cd.com");
        List<Employee> invalidEmployees = Arrays.asList(
                new Employee(1, "!@#$", "pAssword1!", "a@bc.com"), // Invalid username
                new Employee(1, "user", "pass", "a@bc.com"), // Invalid password
                new Employee(1, "user", "pAssword1!", "abccom") // Invalid email
        );
        Assert.assertTrue(ValidatorService.isUserValid(validEmployee));

        boolean invalidEmployeesAreAllInvalid = invalidEmployees.stream()
                .map(ValidatorService::isUserValid)
                .noneMatch(bool -> bool);

        Assert.assertTrue(invalidEmployeesAreAllInvalid);
    }

    @Test
    public void requestValidatorWorksCorrectly() {
        PendingRequest validRequest = new PendingRequest(1, 1.6, "food", "stuff", 1);
        Assert.assertTrue(ValidatorService.isRequestValid(validRequest));
    }
    @Test(expected = InvalidAmountException.class)
    public void requestValidatorThrewBadAmount() {
        PendingRequest invalidRequest = new PendingRequest(1, -3.0, "food", "stuff", 1);  // Invalid amount
        ValidatorService.isRequestValid(invalidRequest);
    }
    @Test(expected = InvalidTypeException.class)
    public void requestValidatorThrewBadType() {
        PendingRequest invalidRequest = new PendingRequest(1, 3.0, "concert", "stuff", 1);  // Invalid type
        ValidatorService.isRequestValid(invalidRequest);
    }
    @Test(expected = InvalidExtraInfoException.class)
    public void requestValidatorThrewBadExtraInfo() {
        PendingRequest invalidRequest = new PendingRequest(1, 3.0, "food", "OMG!!@$#", 1); // Invalid extraInfo
        ValidatorService.isRequestValid(invalidRequest);
    }
}
