package exceptions;

public class ResourceDoesNotExistException extends RuntimeException {

    public ResourceDoesNotExistException(String resource) {
        super(resource+ " Could Not Be Located.");
    }
}
