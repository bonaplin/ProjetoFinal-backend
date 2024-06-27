package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.NotificationBean;
import aor.project.innovationlab.dto.PagAndUnreadResponse;
import aor.project.innovationlab.dto.notification.NotificationDto;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/notifications")
public class NotificationService {

    @Inject
    private NotificationBean notificationBean;

    @GET
    @Path("/")
    public Response getMyNotifications(@HeaderParam ("token") String token,@QueryParam("page_number") Integer pageNumber,
                                       @QueryParam("page_size") Integer pageSize) {
        PagAndUnreadResponse<Object> dto = notificationBean.getAllNotifications(token, pageNumber, pageSize);
        return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
    }
}
