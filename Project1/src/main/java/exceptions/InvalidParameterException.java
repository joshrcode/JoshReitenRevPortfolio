package exceptions;

public class InvalidParameterException extends ParameterException{
    public InvalidParameterException(String parameter){
        super(parameter+" was invalid. Please correct and try again.");
    }
}
