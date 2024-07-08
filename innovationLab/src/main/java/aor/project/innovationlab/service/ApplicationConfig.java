package aor.project.innovationlab.service;

import aor.project.innovationlab.exception.GlobalExceptionHandler;
import aor.project.innovationlab.utils.JacksonConfig;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest")
public class ApplicationConfig extends Application{
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(GlobalExceptionHandler.class);
        resources.add(EmailService.class);
        resources.add(ImageService.class);
        resources.add(InterestService.class);
        resources.add(LabService.class);
        resources.add(NotificationService.class);
        resources.add(ObjectMapperContextResolver.class);
        resources.add(ProductService.class);
        resources.add(ProjectService.class);
        resources.add(SkillService.class);
        resources.add(SupplierService.class);
        resources.add(TaskService.class);
        resources.add(UserService.class);
        resources.add(JsonUtils.class);
        resources.add(JacksonConfig.class);
        resources.add(ApplicationConfig.class);
        resources.add(AdminService.class);

        return resources;
    }
}
