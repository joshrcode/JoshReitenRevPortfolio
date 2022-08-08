package validatorTests;

import models.Employee;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static validators.UserRegistrationValidator.*;
import static validators.UserValidationResult.SUCCESS;

public class UserRegistrationValidatorTest {
    @Test
    public void usernameValidatorWorks() {
        // Valid Usernames
        String[] validUsernames = new String[5];
        validUsernames[0] = "a"; // Contains at least 1 lower letter
        validUsernames[1] = "A"; // Contains at least 1 upper letter
        validUsernames[2] = "1"; // Contains at least 1 upper letter
        validUsernames[3] = "_"; // Contains at least 1 '_'
        validUsernames[4] = "Usr_NameLength16"; // Contains letters and numbers up to 16 chars

        // Invalid Usernames
        String[] invalidUsernames = new String[5];
        invalidUsernames[0] = "@"; // Contains an invalid character
        invalidUsernames[1] = "Te$t"; // Contains an invalid character
        invalidUsernames[2] = "test-name"; // Contains an invalid character
        invalidUsernames[3] = "nul!"; // Contains an invalid character
        invalidUsernames[4] = "_UserNameLength17"; // Contains > 16 char


        // Map all usernames to Employee's, then Employee's into UserValidationResults
        // Then check if ALL are SUCCESS
        boolean allValidUsernamesAreValid = Arrays.stream(validUsernames)
                .map(username -> new Employee(1, username, "", ""))// turn Usernames into Employees
                .map(user -> isUsernameValid().apply(user))// turn Employees into UserValidationResults. Expect: SUCCESS
                .allMatch(result -> result.equals(SUCCESS)); // Make sure they are ALL SUCCESS

        Assert.assertTrue(allValidUsernamesAreValid);

        // Map all usernames to Employee's, then Employee's into UserValidationResults
        // Then check if NONE are SUCCESS
        // This returns true if NONE of the usernames pass
        boolean allInvalidUsernamesAreInvalid = Arrays.stream(invalidUsernames)
                .map(username -> new Employee(1, username, "", ""))// turn Usernames into Employees
                .map(user -> isUsernameValid().apply(user))// turn Employees into UserValidationResults Expect:!SUCCESS
                .noneMatch(result -> result.equals(SUCCESS)); // Make sure NONE are SUCCESS

        // True means the invalid usernames were invalid
        Assert.assertTrue(allInvalidUsernamesAreInvalid);
    }
    @Test
    public void passwordValidatorWorks() {
        // Valid passwords
        String[] validPasswords = new String[5];
        validPasswords[0] = "aBCDEFGHIJKLMNOPQR1!"; // Contains almost all capital letters
        validPasswords[1] = "Zyxwvutsrqpomnlkji2@"; // Contains almost all lowercase letters
        validPasswords[2] = "AbYz1234567890#"; // Contains all numbers
        validPasswords[3] = "3cdefg$%^&+=XWVUTS"; // Contains remaining special characters
        validPasswords[4] = "Password123!"; // Contains irony

        // Invalid Usernames
        String[] invalidPasswords = new String[6];
        invalidPasswords[0] = "Ab345678"; // Missing a special character
        invalidPasswords[1] = "Ab!DeFgH"; // Missing a number
        invalidPasswords[2] = "A!345678"; // Missing a lowercase letter
        invalidPasswords[3] = "b!345678"; // Missing a capital letter
        invalidPasswords[4] = "Ab!4567"; // Contains < 8 chars
        invalidPasswords[5] = "Ab!456789012345678901"; // Contains > 20 chars


        // Map all passwords to Employee's, then Employee's into UserValidationResults
        // Then check if ALL are SUCCESS
        boolean allValidPasswordsAreValid = Arrays.stream(validPasswords)
                .map(password -> new Employee(1, "", password, ""))// turn passwords into Employees
                .map(user -> isPasswordValid().apply(user))// turn Employees into UserValidationResults. Expect: SUCCESS
                .allMatch(result -> result.equals(SUCCESS)); // Make sure they are ALL SUCCESS

        Assert.assertTrue(allValidPasswordsAreValid);

        // Map all passwords to Employee's, then Employee's into UserValidationResults
        // Then check if NONE are SUCCESS
        // This returns true if NONE of the usernames pass
        boolean allInvalidPasswordsAreInvalid = Arrays.stream(invalidPasswords)
                .map(password -> new Employee(1, "", password, ""))// turn passwords into Employees
                .map(user -> isPasswordValid().apply(user))// turn Employees into UserValidationResults Expect:!SUCCESS
                .noneMatch(result -> result.equals(SUCCESS)); // Make sure NONE are SUCCESS

        // True means the invalid usernames were invalid
        Assert.assertTrue(allInvalidPasswordsAreInvalid);
    }
    @Test
    public void emailValidatorWorks() {
        // Valid emails
        String[] validEmails = new String[5];
        validEmails[0] = "test@test.com"; // Most basic passing email
        validEmails[1] = "test.web@test.com"; // '.' allowed before @
        validEmails[2] = "test.web.site@test.com"; // 2x '.' allowed before @
        validEmails[3] = "1@23.yz"; // Numbers only is allowed before the top level domain
        validEmails[4] = "4_5@67.abcdefghijklmnopqrstuvwxz"; // Ridiculous but valid TLD

        // Invalid emails
        String[] invalidEmails = new String[11];
        invalidEmails[0] = "@test.com"; // Before @ must be > 0 chars
        invalidEmails[1] = "1234567890123456789012345678901234567890" +
                "1234567890123456789012345@test.com"; // Before @ must be < 65 chars
        invalidEmails[2] = "!test@test.com"; // Invalid character
        invalidEmails[3] = "test.!@test.com"; // Invalid character at another section of the regex
        invalidEmails[4] = "testAtTest.com"; // Missing @ character
        invalidEmails[5] = "test@-test.com"; // After @ can't start with '-'
        invalidEmails[6] = "test@test_web.com"; // Invalid character. After @ can't have '_'
        invalidEmails[7] = "t@est.web_site.com"; // Invalid character. After @ can't have '_' (another section of regex)
        invalidEmails[8] = "t@est.website.c"; // Top Level domain can't have < 2 characters
        invalidEmails[9] = "t@est.website.c0m"; // Top Level domain can't have numbers
        invalidEmails[10] = "t@est.website.c()m"; // Top Level domain can't have special characters



        // Map all emails to Employee's, then Employee's into UserValidationResults
        // Then check if ALL are SUCCESS
        boolean allValidEmailsAreValid = Arrays.stream(validEmails)
                .map(email -> new Employee(1, "", "", email))// turn emails into Employees
                .map(user -> isEmailValid().apply(user))// turn Employees into UserValidationResults. Expect: SUCCESS
                .allMatch(result -> result.equals(SUCCESS)); // Make sure they are ALL SUCCESS

        Assert.assertTrue(allValidEmailsAreValid);

        // Map all emails to Employee's, then Employee's into UserValidationResults
        // Then check if NONE are SUCCESS
        // This returns true if NONE of the usernames pass
        boolean allInvalidEmailsAreInvalid = Arrays.stream(invalidEmails)
                .map(email -> new Employee(1, "", "", email))// turn passwords into Employees
                .map(user -> isEmailValid().apply(user))// turn Employees into UserValidationResults Expect:!SUCCESS
                .noneMatch(result -> result.equals(SUCCESS)); // Make sure NONE are SUCCESS

        // True means the invalid usernames were invalid
        Assert.assertTrue(allInvalidEmailsAreInvalid);
    }
}
