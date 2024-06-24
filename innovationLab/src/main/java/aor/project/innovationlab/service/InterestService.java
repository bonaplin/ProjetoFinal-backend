package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.InterestBean;
import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.dto.interests.InterestDto;
import aor.project.innovationlab.utils.Color;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/interests")
public class InterestService {
    @Inject
    private InterestBean interestBean;
    @Inject
    private SessionBean sessionBean;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInterests(@HeaderParam("token") String auth, @QueryParam("userEmail") String userEmail, @QueryParam("projectName") String projectName){
//        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        List<InterestDto> interests = new ArrayList<>();
            if(userEmail != null){
                interests = interestBean.getUserInterests(auth, userEmail);
                return Response.status(200).entity(interests).build();
            }
            if(projectName != null){
                interests = interestBean.getProjectInterests(auth, projectName);
                return Response.status(200).entity(interests).build();
            }
            interests = interestBean.getAllInterests(auth);
            return Response.status(200).entity(interests).build();

    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addInterest(@HeaderParam("token") String auth, InterestDto interestDto) {
//        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        InterestDto interest = interestBean.addInterest(auth, interestDto);
        return Response.status(200).entity(interest).build();
    }

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteInterest(@HeaderParam("token") String auth, InterestDto interestDto) {
//        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        interestBean.deleteInterest(auth, interestDto);
        return Response.status(200).entity("Interest deleted successfully").build();

    }
}
