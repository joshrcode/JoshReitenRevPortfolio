package exceptions;

public class InvalidLogInException extends RuntimeException{
    public InvalidLogInException () {
        super("Invalid Log In Details.");
    }
}
