package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.TaskExecutorEntity;
import jakarta.ejb.Stateless;

@Stateless
public class TaskExecutorDao extends AbstractDao<TaskExecutorEntity> {

        private static final long serialVersionUID = 1L;

        public TaskExecutorDao() {
            super(TaskExecutorEntity.class);
        }



}
