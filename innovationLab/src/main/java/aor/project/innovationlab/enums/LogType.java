package aor.project.innovationlab.enums;

public enum LogType {
    TASK_CREATE(10), //.
    TASK_CHANGE(11),
    TASK_DELETE(12),
    TASK_COMPLETE(13),
    TASK_STATE_CHANGE(14),

    USER_JOIN(20), //.
    USER_LEAVE(21),
    USER_CHANGE(22), //.
    USER_KICKED(23),

    PROJECT_CHANGE(30),
    PROJECT_STATE_CHANGE(31); //.


    private final int value;

    LogType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LogType fromValue(int value) {
        for (LogType type : LogType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }


}
