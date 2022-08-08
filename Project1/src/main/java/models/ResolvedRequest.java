package models;

import java.util.Objects;

public class ResolvedRequest extends ReimbursementRequest {
    private boolean isApproved;
    private int reviewerId;

    public ResolvedRequest() {
    }

    public ResolvedRequest(int id, double amount, String type,  String extraInfo, int requesterId, boolean isApproved, int reviewerId) {
        super(id, amount, type, extraInfo, requesterId);
        this.isApproved = isApproved;
        this.reviewerId = reviewerId;
    }

    // This constructor takes a PendingRequest and "Upgrades" it to a ResolvedRequest
    public ResolvedRequest(PendingRequest pr, boolean isApproved, int reviewerId) {
        this.setId(pr.getId());
        this.setRequesterId(pr.getRequesterId());
        this.setAmount(pr.getAmount());
        this.setType(pr.getType());
        this.setExtraInfo(pr.getExtraInfo());
        this.isApproved = isApproved;
        this.reviewerId = reviewerId;
    }

    // This constructor is to clone a request. Used in a RequestRepository method.
    public ResolvedRequest(ResolvedRequest rr) {
        this.setId(rr.getId());
        this.setRequesterId(rr.getRequesterId());
        this.setAmount(rr.getAmount());
        this.setType(rr.getType());
        this.setExtraInfo(rr.getExtraInfo());
        this.isApproved = rr.isApproved();
        this.reviewerId = rr.getReviewerId();
    }



    public boolean isApproved() {
        return isApproved;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ResolvedRequest that = (ResolvedRequest) o;
        return isApproved == that.isApproved && Integer.compare(that.reviewerId, reviewerId) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isApproved, reviewerId);
    }

    @Override
    public String toString() {
        return "ResolvedRequest{" + super.toString() +
                "isApproved=" + isApproved +
                ", reviewer=" + reviewerId +
                '}';
    }
}
