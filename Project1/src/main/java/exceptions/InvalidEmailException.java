package exceptions;

public class InvalidEmailException extends RuntimeException{
    public InvalidEmailException (String cause) {
        super("Invalid Email Address.");
    }
}
