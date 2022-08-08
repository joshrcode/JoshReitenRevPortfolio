package exceptions;

public class RequestAlreadyResolvedException extends RuntimeException {
    public RequestAlreadyResolvedException(int id) {
        super("Request ID: "+id+" has already been resolved.");
    }
}
