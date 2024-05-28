package aor.project.innovationlab.utils.logs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class LoggerUtil {

    private static final Logger logger = LogManager.getLogger(LoggerUtil.class);

    public static void logInfo(String prefix, String message, String user, String token) {
        logger.info(prefix + " - " + message + " - User: " + user + " - Token: " + token);
    }

    public static void logError(String prefix, String message, String user, String token) {
        logger.error(prefix + " - " + message + " - User: " + user + " - Token: " + token);
    }

    public static void logWarn(String prefix, String message, String user, String token) {
        logger.warn(prefix + " - " + message + " - User: " + user + " - Token: " + token);
    }

}
