package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.SupplierBean;
import aor.project.innovationlab.dto.supplier.SupplierDto;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/suppliers")
public class SupplierService {

    @Inject
    private SupplierBean supplierBean;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSuppliers(@HeaderParam("token") String token, @QueryParam("dtoType") String dtoType) {
        List<SupplierDto> dto = supplierBean.getAllSuppliers(dtoType, token);
        return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
    }
}
