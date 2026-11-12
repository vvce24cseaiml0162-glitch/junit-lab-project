package com.lab.utilities;

import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"
    );

    // University USN Pattern: e.g., 4VV24CI001, 4VV21CS045
    private static final Pattern USN_PATTERN = Pattern.compile(
            "^4VV\\d{2}[A-Z]{2}\\d{3}$",
            Pattern.CASE_INSENSITIVE
    );

    // 10-digit Indian Mobile number pattern starting with 6, 7, 8, or 9
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[6-9]\\d{9}$"
    );

    // Strong password: min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special character
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    private ValidationUtils() {
        // Utility class
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidUSN(String usn) {
        if (usn == null || usn.trim().isEmpty()) {
            return false;
        }
        return USN_PATTERN.matcher(usn.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("[<>\"']", "");
    }
}
