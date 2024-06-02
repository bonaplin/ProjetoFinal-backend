package aor.project.innovationlab.utils;

/**
 * Utility class to sanitize input.
 * It removes all characters that are not letters, digits, whitespaces, '@' or '.'.
 * Protects against SQL injection.
 */
public class InputSanitizerUtil {

    private InputSanitizerUtil() {
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[^\\w\\s@.=]", "");
    }
}