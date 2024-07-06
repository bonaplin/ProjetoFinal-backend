package aor.project.innovationlab.enums;

public enum TaskStatus {
    PLANNED(10),
    IN_PROGRESS(30),
    FINISHED(50),
    PRESENTATION(70);

    private final int value;

    TaskStatus(int value) {
        this.value = value;
    }

    public static boolean contains(Integer status) {
        for (TaskStatus taskStatus : TaskStatus.values()) {
            if (taskStatus.getValue() == status) {
                return true;
            }
        }
        return false;
    }

    public int getValue() {
        return value;
    }

    public static TaskStatus fromValue(int value) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }

}
