package models;

import java.util.Objects;

public abstract class ReimbursementRequest extends WebAppObject {
    private double amount;
    private String type;
    private int requesterId;
    private String extraInfo;

    public ReimbursementRequest() {
    }

    public ReimbursementRequest(double amount, String type, String extraInfo, int requesterId) {
        this.amount = amount;
        this.type = type;
        this.requesterId = requesterId;
        this.extraInfo = extraInfo;
    }

    public ReimbursementRequest(int id, double amount, String type, String extraInfo, int requesterId) {
        super(id);
        this.amount = amount;
        this.type = type;
        this.requesterId = requesterId;
        this.extraInfo = extraInfo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReimbursementRequest that = (ReimbursementRequest) o;
        return Double.compare(that.amount, amount) == 0 && type.equals(that.type) && Integer.compare(that.requesterId, requesterId) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, type, requesterId);
    }

    @Override
    public String toString() {
        return "ReimbursementRequest{" +
                "ID=" +this.getId() +
                "amount=" + amount +
                ", type='" + type + '\'' +
                ", requester=" + requesterId +
                ", extraInfo='" + extraInfo + '\'' +
                '}';
    }
}
