package services;

import exceptions.*;
import exceptions.PendingRequests.InvalidAmountException;
import exceptions.PendingRequests.InvalidExtraInfoException;
import exceptions.PendingRequests.InvalidTypeException;
import models.ReimbursementRequest;
import models.User;
import validators.ReimbursementRequestValidationResult;
import validators.UserValidationResult;

import static validators.RequestRegistrationValidator.*;
import static validators.UserRegistrationValidator.*;

public class ValidatorService {
    public static boolean isUserValid(User user) {
        try {
            // Using combinator pattern
            UserValidationResult result = isUsernameValid()
                    .and(isPasswordValid())
                    .and(isEmailValid())
                    .apply(user);

            switch (result) {
                case EMAIL_NOT_VALID:
                    throw new InvalidEmailException(result.name());
                case USERNAME_NOT_VALID:
                case PASSWORD_NOT_VALID:
                    throw new InvalidLogInException();
                default:
                    System.out.println("User validated");
                    return true;
            }
        }
        catch (InvalidEmailException e) {
            System.out.println("Email Address Format Incorrect");
        }
        catch (InvalidLogInException e) {
            System.out.println("Incorrect Login Details");
        }

        return false;
    }

    public static boolean isRequestValid(ReimbursementRequest request)
            throws InvalidAmountException, InvalidTypeException, InvalidExtraInfoException {

        try {
            // Using combinator pattern
            ReimbursementRequestValidationResult result = isAmountValid()
                    .and(isTypeValid())
                    .and(isExtraInfoValid())
                    .apply(request);

            switch (result) {
                case AMOUNT_NOT_VALID:
                    throw new InvalidAmountException(result.name());
                case TYPE_NOT_VALID:
                    throw new InvalidTypeException(result.name());
                case EXTRA_INFO_NOT_VALID:
                    throw new InvalidExtraInfoException(result.name());
                default:
                    System.out.println("Request validated");
                    return true;
            }
        }
        catch (InvalidAmountException e) {
            System.out.println("Amount cannot be negative");
            throw new InvalidAmountException("Amount cannot be negative");
        }
        catch (InvalidTypeException e) {
            System.out.println("Type can only be: food, travel, supplies, other");
            throw new InvalidTypeException("Type can only be: food, travel, supplies, other");
        }
        catch (InvalidExtraInfoException e) {
            System.out.println("Incorrect Extra Info Format");
            throw new InvalidExtraInfoException("Incorrect Extra Info Format");
        }
    }
}
