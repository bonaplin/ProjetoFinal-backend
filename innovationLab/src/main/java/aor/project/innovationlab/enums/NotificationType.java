package aor.project.innovationlab.enums;

public enum NotificationType {
    MESSAGE(10),
    INVITE(20);

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
