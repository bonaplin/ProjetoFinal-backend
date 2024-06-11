package aor.project.innovationlab.enums;

public enum ProductType {
    COMPONENT(1),
    RESOURCE(2),
    ;

    private final int id;

    ProductType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ProductType fromId(int id) {
        for (ProductType productType : ProductType.values()) {
            if (productType.getId() == id) {
                return productType;
            }
        }
        return null;
    }

    public static boolean contains(String type) {
        for (ProductType productType : ProductType.values()) {
            if (productType.name().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public String toUpperCase() {
        return this.name().toUpperCase();
    }
}
