package validators;

import models.ReimbursementRequest;
import models.User;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static validators.ReimbursementRequestValidationResult.*;
import static validators.ReimbursementRequestValidationResult.SUCCESS;
import static validators.UserValidationResult.*;

public interface RequestRegistrationValidator
        extends Function<ReimbursementRequest, ReimbursementRequestValidationResult> {

    static final String[] ACCEPTABLE_TYPES = {"TRAVEL", "FOOD", "SUPPLIES", "OTHER"};

    static RequestRegistrationValidator isAmountValid() {
        return reimbursementRequest -> reimbursementRequest.getAmount() > 0.0 ? SUCCESS : AMOUNT_NOT_VALID;
    }

    static RequestRegistrationValidator isTypeValid() {
        return reimbursementRequest -> Arrays.stream(ACCEPTABLE_TYPES)
                .anyMatch(string -> string.equalsIgnoreCase(reimbursementRequest.getType())) ?
                SUCCESS : TYPE_NOT_VALID;
    }

    static RequestRegistrationValidator isExtraInfoValid() {
        // String MUST contain a-zA-Z0-9._-
        final String regex = "^[a-zA-Z0-9: ,._-]*$";
        return reimbursementRequest -> (reimbursementRequest.getExtraInfo().matches(regex) &&
                reimbursementRequest.getExtraInfo().length() < 200) ||
                reimbursementRequest.getExtraInfo() == null ?
                SUCCESS : EXTRA_INFO_NOT_VALID;
    }


    default RequestRegistrationValidator and (RequestRegistrationValidator other) {
        return request -> {
            ReimbursementRequestValidationResult result = this.apply(request);
            return result.equals(SUCCESS) ? other.apply(request) : result;
        };
    }
}
