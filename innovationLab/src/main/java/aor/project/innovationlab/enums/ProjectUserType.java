package aor.project.innovationlab.enums;

public enum ProjectUserType {
    NORMAL(10),
    MANAGER(20),
    INVITED(30),
    PROPOSED(40);

    private final int value;

    ProjectUserType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ProjectUserType fromValue(int value) {
        for (ProjectUserType type : ProjectUserType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
