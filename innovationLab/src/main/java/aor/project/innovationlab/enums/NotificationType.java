package aor.project.innovationlab.enums;

public enum NotificationType {
    NOTIFICATION(1),
    MESSAGE(10),
    INVITE(20),
    INVITE_ACCEPTED(21),
    INVITE_PROPOSED(22),
    NEW_MAIL(100),
    LOGOUT(200),

    TASK_EXECUTOR_CHANGED(250),

    PROJECT_MESSAGE(300),
    PROJECT_OPEN(310),
    PROJECT_CLOSE(311),



    PROJECT_KICKED(312),
    PROJECT_ROLE_CHANGED(313),

    PROJECT_STATUS_CHANGED(314),
    PROJECT_CANCELLED(315),
    PROJECT_APPROVED(316);

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
