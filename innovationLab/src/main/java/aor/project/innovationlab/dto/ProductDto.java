package aor.project.innovationlab.dto;

import aor.project.innovationlab.enums.ProjectStatus;

public class ProductDto {
    private long id;
    private String name;
    private String description;
    private String type;
    private String supplier;
    private String supplierPhone;
    private String brand;
    private String identifier;
    private int quantity;
    private int status;

    public ProductDto() {
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getSupplier() {
        return supplier;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", supplier='" + supplier + '\'' +

                ", quantity='" + quantity + '\'' +

                '}';
    }
}
