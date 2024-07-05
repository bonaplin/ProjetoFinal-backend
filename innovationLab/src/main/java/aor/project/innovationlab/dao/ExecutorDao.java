package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.ExecutorEntity;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class ExecutorDao extends AbstractDao<ExecutorEntity>{

    private static final long serialVersionUID = 1L;

    public ExecutorDao() {
        super(ExecutorEntity.class);
    }

    public ExecutorEntity findExecutorByName(String name) {
        try {
            return (ExecutorEntity) em.createNamedQuery("Executor.findExecutorByName").setParameter("name", name)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    public List<ExecutorEntity> findAllExecutors() {
        try {
            return em.createNamedQuery("Executor.findAllExecutors").getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
