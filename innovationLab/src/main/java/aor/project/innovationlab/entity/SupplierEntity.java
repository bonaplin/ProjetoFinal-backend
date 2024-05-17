package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "supplier")
@NamedQuery(name = "Supplier.findSupplierByName", query = "SELECT s FROM SupplierEntity s WHERE s.name = :name")
@NamedQuery(name = "Supplier.findSupplierById", query = "SELECT s FROM SupplierEntity s WHERE s.id = :id")

public class SupplierEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "name", nullable = false, unique = true, updatable = true)
    private String name;

    @Column(name = "phone", nullable = false, unique = false, updatable = true)
    private String phone;

    public SupplierEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "SupplierEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

}
