package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.LabBean;
import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.enums.TokenStatus;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/labs")
public class LabService {

    @Inject
    private LabBean labBean;

    @Inject
    private SessionBean sessionBean;

    @GET
    @Produces("application/json")
    public Response getAllLabs() {
        return Response.ok(labBean.findAllLabs()).build();
    }
}
