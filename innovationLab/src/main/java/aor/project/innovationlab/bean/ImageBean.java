package aor.project.innovationlab.bean;
import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@ApplicationScoped
public class ImageBean {

    @Inject
    UserDao userDao;

    @Inject
    SessionDao sessionDao;

    @Inject
    SessionBean sessionBean;

    private static final String IMAGE_DIRECTORY = "InnovationLab/images";

    public String saveImage(InputStream imageData, String originalFileName, long id) throws IOException {
        String fileExtension = getFileExtension(originalFileName);
        String fileName = "profile." + fileExtension;
        String directory = IMAGE_DIRECTORY + "/" + id;
        Path imagePath = Paths.get(directory, fileName);

        if (!Files.exists(imagePath.getParent())) {
            Files.createDirectories(imagePath.getParent());
        }
        Files.copy(imageData, imagePath, StandardCopyOption.REPLACE_EXISTING);

        return imagePath.toString();
    }

    public byte[] getImage(String imagePath) throws IOException {
        Path path = Paths.get(imagePath);
        return Files.readAllBytes(path);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public void saveUserProfileImage(String token, InputStream imageData, String originalFileName) throws IOException {
        UserEntity userEntity = sessionDao.findSessionByToken(token).getUser();
        if (userEntity == null) {
            throw new IllegalArgumentException("User not found with token: " + token);
        }

        long userId = userEntity.getId();

        String imageType = "image/" + getFileExtension(originalFileName);
        String imagePath = saveImage(imageData, originalFileName, userId);

        userEntity.setProfileImagePath(imagePath);
        userEntity.setProfileImageType(imageType);
        userDao.merge(userEntity);
    }

    public InputStream getUserImage(String token) throws IOException {
        UserEntity userEntity = sessionBean.getUserByToken(token);
        String imagePath = userEntity.getProfileImagePath();

        if (imagePath == null) {
            throw new IOException("Image path is null");
        }

        byte[] imageData = getImage(imagePath);
        return new ByteArrayInputStream(imageData);
    }



}