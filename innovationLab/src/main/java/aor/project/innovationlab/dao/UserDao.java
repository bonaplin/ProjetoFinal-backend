package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.UserEntity;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class UserDao extends AbstractDao<UserEntity> {

    private static final long serialVersionUID = 1L;

    public UserDao() {
        super(UserEntity.class);
    }


}

