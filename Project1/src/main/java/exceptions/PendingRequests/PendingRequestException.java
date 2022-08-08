package exceptions.PendingRequests;

import exceptions.ParameterException;

public class PendingRequestException extends ParameterException {
    public PendingRequestException(String message) {
        super(message);
    }
}
