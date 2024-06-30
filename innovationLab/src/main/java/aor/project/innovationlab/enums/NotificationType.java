package aor.project.innovationlab.enums;

public enum NotificationType {
    NOTIFICATION(1),
    MESSAGE(10),
    INVITE(20),
    NEW_MAIL(100),
    LOGOUT(200),
    PROJECT_MESSAGE(300),
    PROJECT_OPEN(310),
    PROJECT_CLOSE(311);

    private final int value;

    NotificationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static NotificationType fromValue(int value) {
        for(NotificationType type : NotificationType.values()) {
            if(type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
