package exceptions.PendingRequests;

public class InvalidAmountException extends PendingRequestException{
    public InvalidAmountException(String cause) {
        super("Invalid Reimbursement Amount.");
    }
}
