package com.lab.tests;

import com.lab.utilities.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validation Utilities Test Suite")
@Tag("validation")
public class ValidationUtilsTest {

    @ParameterizedTest(name = "Valid email: {0}")
    @ValueSource(strings = {
            "pruthvik@vvce.ac.in",
            "student.test@domain.com",
            "qa_dev@sub.domain.org",
            "firstname.lastname@company.co.in"
    })
    @DisplayName("Should accept valid email patterns")
    void testValidEmails(String email) {
        assertTrue(ValidationUtils.isValidEmail(email), "Should be valid email: " + email);
    }

    @ParameterizedTest(name = "Invalid email: {0}")
    @ValueSource(strings = {
            "",
            "   ",
            "plainaddress",
            "@missingusername.com",
            "username@.com",
            "username@domain..com"
    })
    @DisplayName("Should reject malformed email patterns")
    void testInvalidEmails(String email) {
        assertFalse(ValidationUtils.isValidEmail(email), "Should be invalid email: " + email);
    }

    @ParameterizedTest(name = "Valid University USN: {0}")
    @ValueSource(strings = {
            "4VV24CI001",
            "4VV24CI130",
            "4VV21CS045",
            "4vv24ai062",
            "4VV23EC099"
    })
    @DisplayName("Should validate correct university USN formats")
    void testValidUsnFormats(String usn) {
        assertTrue(ValidationUtils.isValidUSN(usn), "USN should be valid: " + usn);
    }

    @ParameterizedTest(name = "Invalid University USN: {0}")
    @ValueSource(strings = {
            "",
            "12345",
            "4VV24C",
            "3VV24CI001",
            "4VV24CI0001",
            "4VV2412001"
    })
    @DisplayName("Should reject non-conforming USN formats")
    void testInvalidUsnFormats(String usn) {
        assertFalse(ValidationUtils.isValidUSN(usn), "USN should be invalid: " + usn);
    }

    @ParameterizedTest(name = "Valid Phone: {0}")
    @ValueSource(strings = {
            "9876543210",
            "8765432109",
            "7654321098",
            "6543210987"
    })
    @DisplayName("Should validate standard 10-digit mobile numbers")
    void testValidPhoneNumbers(String phone) {
        assertTrue(ValidationUtils.isValidPhone(phone), "Phone should be valid: " + phone);
    }

    @ParameterizedTest(name = "Invalid Phone: {0}")
    @ValueSource(strings = {
            "",
            "1234567890",
            "987654321",
            "98765432100",
            "abcdefghij"
    })
    @DisplayName("Should reject invalid mobile numbers")
    void testInvalidPhoneNumbers(String phone) {
        assertFalse(ValidationUtils.isValidPhone(phone), "Phone should be invalid: " + phone);
    }

    @Test
    @DisplayName("Should validate strong password complexity requirements")
    void testPasswordComplexity() {
        assertTrue(ValidationUtils.isStrongPassword("P@ssw0rd2026!"));
        assertFalse(ValidationUtils.isStrongPassword("weakpass"));
        assertFalse(ValidationUtils.isStrongPassword("NoSpecial123"));
        assertFalse(ValidationUtils.isStrongPassword("NOLOWER123!"));
    }

    @Test
    @DisplayName("Should sanitize HTML script injection from user inputs")
    void testInputSanitization() {
        String dirty = "<script>alert('hack');</script>";
        String clean = ValidationUtils.sanitizeInput(dirty);

        assertNotNull(clean);
        assertFalse(clean.contains("<"));
        assertFalse(clean.contains(">"));
    }
}
