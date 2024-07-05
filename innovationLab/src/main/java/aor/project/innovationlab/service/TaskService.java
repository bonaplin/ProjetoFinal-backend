package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.TaskBean;
import aor.project.innovationlab.dto.task.TaskContributorsDto;
import aor.project.innovationlab.dto.task.TaskCreateDto;
import aor.project.innovationlab.dto.task.TaskDateUpdateDto;
import aor.project.innovationlab.dto.task.TaskGanttDto;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/tasks")
public class TaskService {

    @Inject
    private TaskBean taskBean;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTasks(@HeaderParam("token") String token,
                             @PathParam("id") Long projectId,
                             @QueryParam("dtoType") String dtoType){
        List<Object> dtos = taskBean.getTasks(token,projectId, dtoType);
        return Response.ok().entity(JsonUtils.convertObjectToJson(dtos)).build();
    }

    @PUT
    @Path("/{id}/date")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTaskDate(@HeaderParam("token") String token,
                                   @PathParam("id") Long taskId,
                                   TaskDateUpdateDto dto){

        System.out.println(dto.getInitialDate());
        System.out.println(dto.getFinalDate());
        TaskGanttDto taskGanttDto = taskBean.updateTaskDate(token, taskId, dto);
        return Response.ok().build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTask(@HeaderParam("token") String token,
                               TaskCreateDto dto){
        System.out.println("bateu aqui");
        TaskGanttDto taskGanttDto = taskBean.createTask(token, dto);
        return Response.ok().entity(JsonUtils.convertObjectToJson(taskGanttDto)).build();
    }

    @GET
    @Path("/create-info/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTaskCreateInfo(@HeaderParam("token") String token,
                                      @PathParam("projectId") Long projectId){
        TaskContributorsDto taskCreateDto = taskBean.getTaskCreateInfo(token, projectId);
        return Response.ok().entity(JsonUtils.convertObjectToJson(taskCreateDto)).build();
    }
}
