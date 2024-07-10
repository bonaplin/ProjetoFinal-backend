package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.ProductStatus;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "project_product")
@NamedQuery(name = "ProjectProduct.findProjectProductIds", query = "SELECT p FROM ProjectProductEntity p WHERE p.project.id = :projectid AND p.product.id = :productid")
@NamedQuery(name = "ProjectProduct.findProductInProjectById", query = "SELECT p FROM ProjectProductEntity p WHERE p.project = :project AND p.product = :product AND p.active = true")
public class ProjectProductEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "quantity", nullable = false, unique = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, unique = false)
    private ProductStatus status;

    @Column(name = "active", nullable = false, unique = false)
    private boolean active = true;

    public ProjectProductEntity() {
    }

    @PrePersist
    public void prePersist() {
        this.active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
