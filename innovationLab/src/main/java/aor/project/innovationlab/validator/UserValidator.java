package aor.project.innovationlab.validator;

public class UserValidator {

    private static final int MIN_PASSWORD_LENGTH = 8;

    public static boolean validateEmail(String email) {
        return email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$");
    }

    public static boolean validatePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must have at least " + MIN_PASSWORD_LENGTH + " characters");
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        if (!hasUpper) {
            throw new IllegalArgumentException("Password must have at least one uppercase character");
        }
        if (!hasLower) {
            throw new IllegalArgumentException("Password must have at least one lowercase character");
        }
        if (!hasDigit) {
            throw new IllegalArgumentException("Password must have at least one digit");
        }
        if (!hasSpecial) {
            throw new IllegalArgumentException("Password must have at least one special character");
        }

        return true;
    }
}
