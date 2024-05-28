package aor.project.innovationlab.exception;

public class CustomExceptions {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class LabNotFoundException extends RuntimeException {
        public LabNotFoundException(String message) {
            super(message);
        }
    }

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class UserCreationException extends RuntimeException {
        public UserCreationException(String message) {
            super(message);
        }
    }
}
