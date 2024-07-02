package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.ProductBean;
import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.dto.product.ProductDto;
import aor.project.innovationlab.dto.product.ProductToCreateProjectDto;
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
            @QueryParam("order_field") String orderField,
            @QueryParam("order_direction") String orderDirection,
            @QueryParam("id") Long id,
            @HeaderParam("token") String auth) {
        PaginatedResponse<Object> dto = productBean.getProducts(auth, dtoType, name, type, brand, supplier_name, identifier, pageNumber, pageSize, orderField, orderDirection, id);

        return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
    }

    @GET
    @Path("/{projectId}")
    @Produces("application/json")
    public Response getProductsByProjectId(@PathParam("projectId") Long projectId, @HeaderParam("token") String token) {
        List<ProductToCreateProjectDto> products = productBean.getProjectProducts(token, projectId);
        return Response.status(200).entity(products).build();
    }

    @GET
    @Path("/filter-options")
    @Produces("application/json")
    public Response getFilterOptions(@HeaderParam("token") String token) {
        return Response.ok().entity(JsonUtils.convertObjectToJson(productBean.filterOptions(token))).build();
    }

    @PUT
    @Path("/{id}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response updateProduct(@PathParam("id") Long id, ProductDto dto, @HeaderParam("token") String token) {
        return Response.ok().entity(JsonUtils.convertObjectToJson(productBean.updateProduct(id, dto, token))).build();
    }

    @PUT
    @Path("/{id}/disable")
    @Produces("application/json")
    public Response disableProduct(@PathParam("id") Long id, @HeaderParam("token") String token) {
        return Response.ok().entity(JsonUtils.convertObjectToJson(productBean.disableProduct(id, token))).build();
    }

    @POST
    @Path("/")
    @Produces("application/json")
    @Consumes("application/json")
    public Response createProduct(ProductDto dto, @HeaderParam("token") String token) {
        return Response.ok().entity(JsonUtils.convertObjectToJson(productBean.createProduct(dto, token))).build();
    }
}
