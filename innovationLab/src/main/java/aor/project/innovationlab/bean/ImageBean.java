package aor.project.innovationlab.bean;
import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.utils.Color;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@ApplicationScoped
public class ImageBean {

    @EJB
    UserDao userDao;

    @EJB
    SessionDao sessionDao;

    @Inject
    SessionBean sessionBean;

    private static final String IMAGE_DIRECTORY = "InnovationLab/images";
    private static final String DIRECTORY = "https://localhost:8443/images/";
    private static final String IMAGE_TYPE = "image.png";

    public String saveImage(InputStream imageData, String originalFileName, long id) {
        String fileExtension = getFileExtension(originalFileName);
        String fileName = "profile." + fileExtension;
        String directory = IMAGE_DIRECTORY + "/" + id;
        Path imagePathLg = Paths.get(directory, fileName);

        String path = "";

        try {
            if (!Files.exists(imagePathLg.getParent())) {
                Files.createDirectories(imagePathLg.getParent());
            }
            // Read the original image from the InputStream
            BufferedImage originalImage = ImageIO.read(imageData);
            // Calculate the new height to maintain aspect ratio
            int widthLg = 150; // New width

            int heightLg = (originalImage.getHeight() * widthLg) / originalImage.getWidth(); // New height

            // Create a new BufferedImage with the new dimensions
            BufferedImage resizedImageLg = new BufferedImage(widthLg, heightLg, originalImage.getType());
            // Draw the original image into the resized image
            Graphics2D gLg = resizedImageLg.createGraphics();
            gLg.drawImage(originalImage, 0, 0, widthLg, heightLg, null);
            gLg.dispose();

            // Write the resized image to a ByteArrayOutputStream
            ByteArrayOutputStream baosLg = new ByteArrayOutputStream();
            ImageIO.write(resizedImageLg, fileExtension, baosLg);

            // Convert the ByteArrayOutputStream to an InputStream
            InputStream isLg = new ByteArrayInputStream(baosLg.toByteArray());

            Files.copy(isLg, imagePathLg, StandardCopyOption.REPLACE_EXISTING);

            path = DIRECTORY + id + "/" + fileName;

        }catch (IOException e){
            e.printStackTrace();
        }
        return path;
    }

    public byte[] getImage(String imagePath) throws IOException {
        Path path = Paths.get(imagePath);
        return Files.readAllBytes(path);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public String saveUserProfileImage(String token, InputStream imageData) {
        UserEntity userEntity = sessionDao.findSessionByToken(token).getUser();
        if (userEntity == null) {
            throw new IllegalArgumentException("User not found with token: " + token);
        }

        long userId = userEntity.getId();

        String imageType = "image/" + getFileExtension(IMAGE_TYPE);
        String imagePaths = saveImage(imageData, IMAGE_TYPE, userId);

        userEntity.setProfileImagePath(imagePaths);

        userEntity.setProfileImageType(imageType);
        userDao.merge(userEntity);
        return imagePaths;
    }

    public InputStream getUserImage(String token) throws IOException {
        UserEntity userEntity = sessionBean.getUserByToken(token);
        String imagePath ="";

        imagePath = userEntity.getProfileImagePath();


        if (imagePath == null) {
            throw new IOException("Image path is null");
        }

        byte[] imageData = getImage(imagePath);
        return new ByteArrayInputStream(imageData);
    }



}