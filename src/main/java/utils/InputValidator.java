package utils;

/**
 * Utility class for input validation
 */
public class InputValidator {

    /**
     * Check if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validate phone number (basic validation)
     */
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) {
            return false;
        }

        // Allow digits, spaces, parentheses, dashes, and plus sign
        String phoneRegex = "^[0-9\\s()+-]{7,20}$";
        return phone.matches(phoneRegex);
    }

    /**
     * Validate ISBN format (ISBN-10 or ISBN-13)
     */
    public static boolean isValidISBN(String isbn) {
        if (isNullOrEmpty(isbn)) {
            return false;
        }

        // Remove hyphens and spaces
        String cleanIsbn = isbn.replaceAll("[\\s-]", "");

        // Check if it's ISBN-10 or ISBN-13
        return cleanIsbn.matches("^\\d{10}$") || cleanIsbn.matches("^\\d{13}$");
    }

    /**
     * Validate positive integer
     */
    public static boolean isPositiveInteger(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }

        try {
            int value = Integer.parseInt(str);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate non-negative integer
     */
    public static boolean isNonNegativeInteger(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }

        try {
            int value = Integer.parseInt(str);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate positive double
     */
    public static boolean isPositiveDouble(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }

        try {
            double value = Double.parseDouble(str);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate non-negative double
     */
    public static boolean isNonNegativeDouble(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }

        try {
            double value = Double.parseDouble(str);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parse integer safely
     */
    public static Integer parseIntSafely(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parse double safely
     */
    public static Double parseDoubleSafely(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Validate string length
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }

        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate username format (alphanumeric and underscore)
     */
    public static boolean isValidUsername(String username) {
        if (isNullOrEmpty(username)) {
            return false;
        }

        // Allow alphanumeric characters and underscore, 3-20 characters
        String usernameRegex = "^[a-zA-Z0-9_]{3,20}$";
        return username.matches(usernameRegex);
    }

    /**
     * Validate password strength (at least 4 characters)
     */
    public static boolean isValidPassword(String password) {
        if (isNullOrEmpty(password)) {
            return false;
        }

        // Minimum 4 characters for this exercise
        return password.length() >= 4;
    }

    /**
     * Sanitize string input (remove extra spaces)
     */
    public static String sanitize(String str) {
        if (str == null) {
            return "";
        }

        return str.trim().replaceAll("\\s+", " ");
    }
}