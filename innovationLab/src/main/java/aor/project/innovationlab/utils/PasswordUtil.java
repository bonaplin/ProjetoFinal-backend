package aor.project.innovationlab.utils;

import org.mindrot.jbcrypt.BCrypt;

import java.security.MessageDigest;

public class PasswordUtil {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
