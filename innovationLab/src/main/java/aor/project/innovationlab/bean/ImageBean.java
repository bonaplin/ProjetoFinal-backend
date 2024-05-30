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

    private static final String BASE_DIRECTORY = "https://localhost:8443/";
    private static final String SHARED_DIRECTORY_WILDFLY = "InnovationLab/";
    private static final String IMAGE_DIRECTORY = "images/";
    private static final String IMAGE_TYPE = "image.png";
    public static final String DEFAULT_IMAGE_PATH = BASE_DIRECTORY.trim()+ IMAGE_DIRECTORY + "default/default.png" ;

    /**
     * Saves an image to the server, given the image data, original file name, and the id of the entity
     * @param imageData the image data
     * @param id the id of the entity
     * @return the path of the saved image to the server
     */
    public String saveImage(InputStream imageData, long id) {
        String fileExtension = getFileExtension(IMAGE_TYPE);
        String fileName = "profile." + fileExtension;
        String shared_path = IMAGE_DIRECTORY + id + "/";
        String directory = SHARED_DIRECTORY_WILDFLY + shared_path;
        Path imagePathLg = Paths.get(directory, fileName);

        String path = BASE_DIRECTORY + shared_path + fileName;

        try {
            createDirectoryIfNotExists(imagePathLg.getParent());
            BufferedImage originalImage = readOriginalImage(imageData);
            BufferedImage resizedImage = resizeImage(originalImage, 150);
            InputStream isLg = convertBufferedImageToInputStream(resizedImage, fileExtension);
            Files.copy(isLg, imagePathLg, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(Color.RED+"Image saved to: "+path+Color.RED);
        return path;
    }

    /**
     * Creates a directory if it does not exist
     * @param path
     * @throws IOException
     */
    private void createDirectoryIfNotExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /**
     * Reads an image from an InputStream, and returns a BufferedImage
     * @param imageData
     * @return
     * @throws IOException
     */
    private BufferedImage readOriginalImage(InputStream imageData) throws IOException {
        return ImageIO.read(imageData);
    }

    /**
     * Resizes an image to a new width, maintaining the aspect ratio
     * @param originalImage
     * @param newWidth
     * @return
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int newWidth) {
        int newHeight = (originalImage.getHeight() * newWidth) / originalImage.getWidth();
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * Converts a BufferedImage to an InputStream, given the file extension
     * @param image
     * @param fileExtension
     * @return
     * @throws IOException
     */
    private InputStream convertBufferedImageToInputStream(BufferedImage image, String fileExtension) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, fileExtension, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

//    /**
//     * Gets the image from the server, given the image path
//     * @param imagePath
//     * @return
//     * @throws IOException
//     */
//    public byte[] getImage(String imagePath) throws IOException {
//        Path path = Paths.get(imagePath);
//        return Files.readAllBytes(path);
//    }

    /**
     * Gets the file extension of a file
     * @param fileName
     * @return
     */
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * Saves the user profile image, and updates the user entity with the new image path
     * @param token
     * @param imageData
     * @return
     */
    public String saveUserProfileImage(String token, InputStream imageData) {
        UserEntity userEntity = sessionDao.findSessionByToken(token).getUser();
        if (userEntity == null) {
            throw new IllegalArgumentException("User not found with token: " + token);
        }

        long userId = userEntity.getId();

//        String imageType = "image/" + getFileExtension(IMAGE_TYPE);
        String imagePaths = saveImage(imageData, userId);

        userEntity.setProfileImagePath(imagePaths);

//        userEntity.setProfileImageType(imageType);
        userDao.merge(userEntity);
        return imagePaths;
    }


//    /**
//     * Gets the image of a user.
//     * @param token
//     * @return
//     * @throws IOException
//     */
//    public InputStream getUserImage(String token) throws IOException {
//        UserEntity userEntity = sessionDao.findSessionByToken(token).getUser();
//        if (userEntity == null) {
//            throw new IllegalArgumentException("User not found with token: " + token);
//        }
//
//        String imagePath = userEntity.getProfileImagePath();
//        return new ByteArrayInputStream(getImage(imagePath));
//    }

    /**
     * Gets the image path of a user.
     * @param token
     * @return
     */
    public String getUserImageString(String token, long id){
        UserEntity userEntity = sessionDao.findSessionByToken(token).getUser();
        if (userEntity == null) {
            throw new IllegalArgumentException("User not found with token: " + token);
        }
        if(id==0){
            return userEntity.getProfileImagePath();
        }
        UserEntity userQuery = userDao.find(id);
        if(userQuery == null){
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        return userQuery.getProfileImagePath();
    }



}