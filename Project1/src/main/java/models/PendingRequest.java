package models;

public class PendingRequest extends ReimbursementRequest {
    public PendingRequest(int id, double amount, String type, String extraInfo, int requesterId) {
        super(id, amount, type, extraInfo, requesterId);
    }
}
