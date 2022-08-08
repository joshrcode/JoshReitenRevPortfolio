package exceptions;

public class ParameterException extends RuntimeException{
    public ParameterException(String parameter){
        super(parameter);
    }
}
