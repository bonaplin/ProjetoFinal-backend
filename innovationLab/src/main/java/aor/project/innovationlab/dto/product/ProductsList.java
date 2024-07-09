package aor.project.innovationlab.dto.product;

import java.util.List;

public class ProductsList {

    private List<ProductToCreateProjectDto> products;

    public ProductsList() {
    }

    public ProductsList(List<ProductToCreateProjectDto> products) {
        this.products = products;
    }

    public List<ProductToCreateProjectDto> getProducts() {
        return products;
    }
}
