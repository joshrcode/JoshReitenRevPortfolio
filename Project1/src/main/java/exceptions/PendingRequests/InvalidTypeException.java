package exceptions.PendingRequests;

public class InvalidTypeException extends PendingRequestException{
    public InvalidTypeException(String cause) {
        super("Invalid Reimbursement Type.");
    }
}
