package exceptions;

public class MissingParameterException extends ParameterException{
    public MissingParameterException(String parameter){
        super("Argument for "+parameter+" was missing. Please correct and try again.");
    }
}
