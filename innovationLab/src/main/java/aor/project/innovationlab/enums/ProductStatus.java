package aor.project.innovationlab.enums;

public enum ProductStatus {
    REQUESTED(10),
    STOCK(20);

    private final int value;

    ProductStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ProductStatus fromValue(int value) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }

}
