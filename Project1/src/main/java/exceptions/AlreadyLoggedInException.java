package exceptions;

public class AlreadyLoggedInException extends RuntimeException {
    public AlreadyLoggedInException(String username) {
        super(username + " is already logged in.");
    }
}
