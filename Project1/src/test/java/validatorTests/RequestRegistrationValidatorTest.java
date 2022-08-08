package validatorTests;

import models.PendingRequest;
import org.junit.Assert;
import org.junit.Test;


import java.util.Arrays;

import static validators.RequestRegistrationValidator.*;
import static validators.ReimbursementRequestValidationResult.SUCCESS;

public class RequestRegistrationValidatorTest {
    @Test
    public void amountValidatorWorks() {
        // Valid amounts
        Double[] validAmounts = new Double[3];
        validAmounts[0] = 0.01; // Small but valid
        validAmounts[1] = 9999999999999999999999999.99; // Large but valid
        validAmounts[2] = 1.234567890; // Weird, but we'll work with it

        // Invalid amounts
        double invalidAmount = -1.2; // cannot have negative amount
        PendingRequest invalidRequest = new PendingRequest(1, invalidAmount, "", "", 1);

        // Map all amounts to PendingRequests, then PendingRequests into UserValidationResults
        // Then check if ALL are SUCCESS
        boolean allValidAmountsAreValid = Arrays.stream(validAmounts)
                .map(amount -> new PendingRequest(1, amount, "", "", 1))// turn amounts to PendingRequests
                .map(pendingRequest -> isAmountValid().apply(pendingRequest))// turn PendingRequests to ValidationResults. Expect: SUCCESS
                .allMatch(result -> result.equals(SUCCESS)); // Make sure they are ALL SUCCESS

        // Expect all valid amounts to return as SUCCESS
        Assert.assertTrue(allValidAmountsAreValid);

        // Expect invalid request to not equal SUCCESS. -negative number
        Assert.assertFalse(isAmountValid().apply(invalidRequest).equals(SUCCESS));
    }

    @Test
    public void typeValidatorWorks() {
        // Valid types
        String[] validTypes = new String[4];
        validTypes[0] = "TRAVEL"; // all uppercase is valid
        validTypes[1] = "food";  // all lowercase is valid
        validTypes[2] = "SuPpLiEs"; // Mixture of cases are valid
        validTypes[3] = "oThEr"; // Mixture of cases are valid

        // Invalid types
        String[] invalidTypes = new String[2];
        invalidTypes[0] = "anythingBesidesthelistedtypes"; // Must be one of four options
        invalidTypes[1] = "123415"; // Must be one of four options. None of which include a number

        // Map all types to PendingRequests, then PendingRequests into UserValidationResults
        // Then check if ALL are SUCCESS
        boolean allValidTypesAreValid = Arrays.stream(validTypes)
                .map(type -> new PendingRequest(1, 3, type, "", 1))// turn amounts to PendingRequests
                .map(pendingRequest -> isAmountValid().apply(pendingRequest))// turn PendingRequests to ValidationResults. Expect: SUCCESS
                .allMatch(result -> result.equals(SUCCESS)); // Make sure they are ALL SUCCESS

        // Expect all valid amounts to return as SUCCESS
        Assert.assertTrue(allValidTypesAreValid);


        // Map all types to PendingRequests, then PendingRequests into UserValidationResults
        // Then check if NONE are SUCCESS
        boolean allInvalidTypesAreInvalid = Arrays.stream(invalidTypes)
                .map(type -> new PendingRequest(1, 3, type, "", 1))// turn amounts to PendingRequests
                .map(pendingRequest -> isTypeValid().apply(pendingRequest))// turn PendingRequests to ValidationResults. Expect: !SUCCESS
                .noneMatch(result -> result.equals(SUCCESS)); // Make sure that NONE are SUCCESS

        // Expect all invalid amounts to return as SUCCESS
        Assert.assertTrue(allInvalidTypesAreInvalid);
    }

    @Test
    public void extraInfoValidatorWorks() {
        // Valid types
        String[] validExtraInfos = new String[5];
        validExtraInfos[0] = "I BOUGHT STUFF"; // all uppercase is valid
        validExtraInfos[1] = "i bought other stuff"; // all lowercase valid
        validExtraInfos[2] = "This is a really long unnecessary description " +
                "of my reimbursement request and I hope it gets approved.";  // Length is < 200
        validExtraInfos[3] = ""; // Empty string is valid
        validExtraInfos[4] = "Stuff: paper-stock, scissors, bag-of_stuff"; // These special characters valid


        // Invalid types
        String[] invalidExtraInfos = new String[2];
        invalidExtraInfos[0] = "stuff!!"; // Contains invalid character
        invalidExtraInfos[1] = "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "123456789012345678901234567890123456789012345678901"; // Contains > 200 Characters

        // Map all types to PendingRequests, then PendingRequests into UserValidationResults
        // Then check if ALL are SUCCESS
        boolean allValidTypesAreValid = Arrays.stream(validExtraInfos)
                .map(extraInfo -> new PendingRequest(1, 3, "other", extraInfo, 1))// turn amounts to PendingRequests
                .map(pendingRequest -> isExtraInfoValid().apply(pendingRequest))// turn PendingRequests to ValidationResults. Expect: SUCCESS
                .allMatch(result -> result.equals(SUCCESS)); // Make sure they are ALL SUCCESS

        // Expect all valid amounts to return as SUCCESS
        Assert.assertTrue(allValidTypesAreValid);


        // Map all types to PendingRequests, then PendingRequests into UserValidationResults
        // Then check if NONE are SUCCESS
        boolean allInvalidTypesAreInvalid = Arrays.stream(invalidExtraInfos)
                .map(extraInfo -> new PendingRequest(1, 3, "other", extraInfo, 1))// turn amounts to PendingRequests
                .map(pendingRequest -> isExtraInfoValid().apply(pendingRequest))// turn PendingRequests to ValidationResults. Expect: !SUCCESS
                .noneMatch(result -> result.equals(SUCCESS)); // Make sure that NONE are SUCCESS

        // Expect all invalid amounts to return as SUCCESS
        Assert.assertTrue(allInvalidTypesAreInvalid);
    }

}
