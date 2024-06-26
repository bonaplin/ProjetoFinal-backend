package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.ImageBean;
import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.TokenStatus;
import aor.project.innovationlab.responses.ResponseMessage;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


@Path("/images")
public class ImageService {

    @Inject
    ImageBean imageBean;

    @Inject
    SessionBean sessionBean;

    public ImageService() {
    }

    /**
     * Uploads an image to the server
     * @param token
     * @param imageData
     * @return
     */
    @POST
    @Path("/user")
    @Consumes("image/*")
    public Response uploadUserImage(@HeaderParam("token") String auth, InputStream imageData) {
//        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        String path = imageBean.saveUserProfileImage(auth, imageData);
        return Response.ok().entity(path).build();
    }


//    /**
//     * Gets the image of a user
//     * @param token
//     * @return
//     */
//    @GET
//    @Path("/user/{id}")
//    @Produces("image/*")
//    public Response getUserPicture(@HeaderParam("token") String token, @PathParam("id") long id) {
//
//        UserEntity userEntity = sessionBean.getUserByToken(token);
//
//        String imageType = userEntity.getProfileImageType();
//
//        try {
//            InputStream imageStream = imageBean.getUserImage(token);
//            return Response.ok(imageStream).type(imageType).build();
//        } catch (IOException e) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @GET
    @Path("/user")
    @Produces("APPLICATION/JSON")
    public Response getUserPicture(@HeaderParam("Authorization") String auth, @QueryParam("id") long id){
        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        String path = imageBean.getUserImageString(token,id);
        return Response.ok().entity(path).build();
    }



}
