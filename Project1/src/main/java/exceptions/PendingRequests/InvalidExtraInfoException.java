package exceptions.PendingRequests;

public class InvalidExtraInfoException extends PendingRequestException{
    public InvalidExtraInfoException(String cause) {
        super("Invalid Reimbursement Extra Info.");
    }
}
