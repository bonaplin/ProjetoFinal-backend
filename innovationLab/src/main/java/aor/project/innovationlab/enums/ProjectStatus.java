package aor.project.innovationlab.enums;

public enum ProjectStatus {
    PLANNING(10),
    READY(20),
    IN_PROGRESS(30),
    APPROVED(35),
    CANCELLED(40),
    FINISHED(50);

    private final int value;

    ProjectStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ProjectStatus fromValue(int value) {
        for (ProjectStatus status : ProjectStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }

    public static boolean isValid(int value) {
        for (ProjectStatus status : ProjectStatus.values()) {
            if (status.getValue() == value) {
                return true;
            }
        }
        return false;
    }

}
