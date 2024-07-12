package aor.project.innovationlab.dto.product;

public class ProductToCreateProjectDto {

    private long id;
    private String name;
    private int quantity;

    public ProductToCreateProjectDto() {
    }

    public ProductToCreateProjectDto(long id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
