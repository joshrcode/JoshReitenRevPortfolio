package exceptions;

public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException() {
        super("Please log in as an applicable user to use this feature.");
    }
}
