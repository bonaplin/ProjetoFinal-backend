package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.ProductBean;
import aor.project.innovationlab.enums.ProductType;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.ProjectUserType;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/products")
public class ProductService {

    @Inject
    private ProductBean productBean;

    @GET
    @Path("/")
    @Produces("application/json")
    public Response getProductsByDto(
            @QueryParam("dtoType") String dtoType,
            @QueryParam("name") String name,
            @QueryParam("type") List<String> type,
            @QueryParam("brand") List<String> brand,
            @QueryParam("supplier_name") String supplier_name,
            @QueryParam("identifier") String identifier,
            @QueryParam("page_number") Integer pageNumber,
            @QueryParam("page_size") Integer pageSize,
            @HeaderParam("token") String auth) {
        List<?> dto = productBean.getProducts(auth, dtoType, name, type, brand, supplier_name, identifier, pageNumber, pageSize);

        return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
    }

    @GET
    @Path("/filter-options")
    @Produces("application/json")
    public Response getFilterOptions(@HeaderParam("token") String token) {
        return Response.ok().entity(JsonUtils.convertObjectToJson(productBean.filterOptions(token))).build();
    }

}
