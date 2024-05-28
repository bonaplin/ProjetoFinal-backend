package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.InterestBean;
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

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInterests(@HeaderParam("token") String token, @QueryParam("userEmail") String userEmail) {
        List<InterestDto> interests = new ArrayList<>();
            if(userEmail != null){
                interests = interestBean.getUserInterests(token, userEmail);
                return Response.status(200).entity(interests).build();
            }
            interests = interestBean.getAllInterests(token);
            return Response.status(200).entity(interests).build();

    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addInterest(@HeaderParam("token") String token, InterestDto interestDto) {
        InterestDto interest = interestBean.addInterest(token, interestDto);
        return Response.status(200).entity(interest).build();
    }

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteInterest(@HeaderParam("token") String token, InterestDto interestDto) {
        interestBean.deleteInterest(token, interestDto);
        return Response.status(200).entity("Interest deleted successfully").build();

    }
}
