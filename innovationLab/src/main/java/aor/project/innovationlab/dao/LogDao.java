package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.LogEntity;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@Stateless
public class LogDao extends AbstractDao<LogEntity> {

    private static final long serialVersionUID = 1L;

    public LogDao() {
        super(LogEntity.class);
    }

    public List<LogEntity> findLogByProjectId(long projectId) {
        try {
            return em.createNamedQuery("Log.findLogByProjectId").setParameter("projectId", projectId)
                    .getResultList();

        } catch (Exception e) {
            return null;
        }
    }
}
