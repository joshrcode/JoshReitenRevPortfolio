package exceptions;

public class UsernameAlreadyUsedException extends ParameterException{
    public UsernameAlreadyUsedException(String username) {
        super(username+ " is already used. Please try again.");
    }
}
