package validators;

import models.User;

import java.util.function.Function;

import static validators.UserValidationResult.*;

public interface UserRegistrationValidator extends Function<User, UserValidationResult> {

    static final String USERNAME_PATTERN = "^\\w+$"; // Must contain at least one of and only a-zA-Z0-9_
    static final String PASSWORD_PATTERN = "^(?=.*[0-9])" // Must contain 1 number
            + "(?=.*[a-z])(?=.*[A-Z])" // Must contain 1 lowercase and 1 uppercase letter
            + "(?=.*[!@#$%^&+=])" // Must contain one of !@#$%^&+=
            + "(?=\\S+$).{8,20}$"; // Must not include white space, and be 8 <= length <= 20
    static final String EMAIL_PATTERN = "^(?=.{1,64}@)" + // Before @ needs 1-64 chars
                                "[A-Za-z0-9_-]+" + // Starts with chars in []
                                "(\\.[A-Za-z0-9_-]+)*" + // optionally can have '.'
                                "@" +                    // followed by more []chars
                                "[^-]" +            // after @ can't start with '-'
                                "[A-Za-z0-9-]+" +  // starts with one or more []chars
                                "(\\.[A-Za-z0-9-]+)*" + // optionally can have '.' // with more []chars
                                "(\\.[A-Za-z]{2,})$"; // top level domain can only be letters, 2 or more chars


    static UserRegistrationValidator isUsernameValid(){
        return user -> user.getUsername().length() <= 16 &&
                user.getUsername().matches(USERNAME_PATTERN) &&
                user.getUsername() != null ?
                SUCCESS : USERNAME_NOT_VALID;
    }

    static UserRegistrationValidator isPasswordValid(){
        return user -> user.getPassword().matches(PASSWORD_PATTERN) &&
                user.getPassword() != null ?
                SUCCESS : PASSWORD_NOT_VALID;
    }

    static UserRegistrationValidator isEmailValid(){
        return user -> user.getEmail().matches(EMAIL_PATTERN) &&
                user.getEmail() != null ?
                SUCCESS : EMAIL_NOT_VALID;
    }




    default UserRegistrationValidator and (UserRegistrationValidator other) {
        return user -> {
            UserValidationResult result = this.apply(user);
            return result.equals(SUCCESS) ? other.apply(user) : result;
        };
    }

}
