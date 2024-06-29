package aor.project.innovationlab.dto.product.filter;

import aor.project.innovationlab.dto.response.IdNameDto;

import java.util.List;

public class FilterOptionsProductDto {
    private List<IdNameDto> types;
    private List<IdNameDto> brands;

    public FilterOptionsProductDto() {
    }

    public List<IdNameDto> getBrands() {
        return brands;
    }

    public void setBrands(List<IdNameDto> brands) {
        this.brands = brands;
    }

    public List<IdNameDto> getTypes() {
        return types;
    }

    public void setTypes(List<IdNameDto> types) {
        this.types = types;
    }
}
