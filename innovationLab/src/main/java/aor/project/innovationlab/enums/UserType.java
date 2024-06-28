package aor.project.innovationlab.enums;

public enum UserType {
    ADMIN(1),
    NORMAL(10),
    MANAGER(20),
    GUEST(30),
    PROPOSED(40),
    INVITED(50);


    private final int value;

    UserType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserType fromValue(int value) {
        for (UserType type : UserType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }

    public static UserType fromString(String value) {
        for (UserType type : UserType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
