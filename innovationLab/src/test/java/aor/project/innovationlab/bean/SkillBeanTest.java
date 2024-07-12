package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.skill.SkillDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.SkillType;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.service.SkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SkillBeanTest {
    @Mock
    private SkillDao skillDao;

    @InjectMocks
    private SkillBean skillBean;

    @Mock
    private UserDao userDao;

    @Mock
    private UserSkillDao userSkillDao;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private ProjectUserDao projectUserDao;

    @Mock
    private ProjectSkillDao ProjectSkillDao;

    @Mock
    private SessionDao sessionDao;

    @Mock
    private InterestBean interestBean;

    @Mock
    private SessionBean sessionBean;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateSkillIfNotExists_SkillDoesNotExist() {
        // Arrange
        SkillEntity skill = new SkillEntity();

        String name = "Java";
        SkillType type = SkillType.KNOWLEDGE;
        skill.setName(name);
        skill.setSkillType(type);

        when(skillDao.findSkillByName(name)).thenReturn(null);

        when(skillDao.findSkillByName(name)).thenReturn(skill);
        when(skillDao.findSkillByName(name)).thenReturn(null);

        // Act
        skillBean.createSkillIfNotExists(name, type);

        // Assert
        verify(skillDao, times(1)).persist(any(SkillEntity.class));
    }

    @Test
    void testCreateSkillIfNotExists_ThrowsIllegal() {
        // Arrange
        SkillEntity skill = new SkillEntity();

        String name = "Java";
        SkillType type = SkillType.KNOWLEDGE;
        skill.setName(name);
        skill.setSkillType(type);

        when(skillDao.findSkillByName(name)).thenReturn(null);

        when(skillDao.findSkillByName(name)).thenReturn(skill);
        when(skillDao.findSkillByName(name)).thenReturn(null);

        // Act
        skillBean.createSkillIfNotExists(name, type);

        // Assert
        verify(skillDao, times(1)).persist(any(SkillEntity.class));
    }

    @Test
    void testAddSkillToUser_UserNotFound() {
        // Arrange
        String email = "user@example.com";
        String skillName = "Java";
        SkillType type = SkillType.KNOWLEDGE;

        when(userDao.findUserByEmail(email)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            skillBean.addSkillToUser(email, skillName, type);
        });


    }

    @Test
    void testGetAllSkillType() {
        // Arrange
        String token = "validToken";
        List<String> expectedSkillTypes = Arrays.asList("KNOWLEDGE", "SOFTWARE", "HARDWARE", "TOOLS");
        when(skillDao.getAllSkillType()).thenReturn(expectedSkillTypes);

        // Act
        List<String> actualSkillTypes = skillBean.getAllSkillType(token);

        // Assert
        verify(sessionBean, times(1)).validateUserToken(token);
        assertEquals(expectedSkillTypes, actualSkillTypes);
    }

    @Test
    void testGetAllSkillType_InvalidToken() {
        // Arrange
        String invalidToken = "invalidToken";
        doThrow(new IllegalArgumentException("Invalid token")).when(sessionBean).validateUserToken(invalidToken);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> skillBean.getAllSkillType(invalidToken));
    }

    @Test
    void testGetAllSkillTypes_InvalidToken() {
        // Arrange
        String invalidToken = "invalidToken";
        doThrow(new IllegalArgumentException("Invalid token")).when(sessionBean).validateUserToken(invalidToken);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> skillBean.getAllSkillType(invalidToken));
    }


}
