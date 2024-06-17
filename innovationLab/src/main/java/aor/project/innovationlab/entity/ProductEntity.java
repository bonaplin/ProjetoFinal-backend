package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.ProductType;
import jakarta.inject.Named;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
@NamedQuery(name = "Product.findProductByName", query = "SELECT p FROM ProductEntity p WHERE p.name = :name")
@NamedQuery(name = "Product.findProductById", query = "SELECT p FROM ProductEntity p WHERE p.id = :id")
@NamedQuery(name = "Product.findProductByIdentifier", query = "SELECT p FROM ProductEntity p WHERE p.identifier = :identifier")
@NamedQuery(name = "Product.findAllBrands", query = "SELECT DISTINCT p.brand FROM ProductEntity p")
public class ProductEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name = "name", nullable = false, unique = true, updatable = true)
    private String name;

    @Column(name = "brand", nullable = false, unique = false, updatable = true)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, unique = false, updatable = true)
    private ProductType type;

    @Column(name = "description", nullable = true, unique = false, updatable = true)
    private String description;

    @Column(name = "identifier", nullable = false, unique = true, updatable = true)
    private String identifier;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false, unique = false, updatable = true)
    private SupplierEntity supplier;

    @Column(name = "quantity", nullable = false, unique = false, updatable = true)
    private int quantity;

    @Column(name = "notes", nullable = true, unique = false, updatable = true)
    private String notes;

    @Column(name = "active", nullable = false, unique = false, updatable = true)
    private boolean active = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProjectProductEntity> projectProducts = new HashSet<>();

    public ProductEntity() {
    }

    @PrePersist
    public void prePersist() {
        this.active = true;
    }

    public long getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public SupplierEntity getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierEntity supplier) {
        this.supplier = supplier;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<ProjectProductEntity> getProjectProducts() {
        return projectProducts;
    }

    public void setProjectProducts(Set<ProjectProductEntity> projectProducts) {
        this.projectProducts = projectProducts;
    }
}
