package aor.project.innovationlab.email;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailSender {

    private static final String username = "botsemgps@gmail.com";
    private static final String password = "xlvi nhlq blnp olzf";
    private static final String registrationLink = "http://localhost:3000/confirm-account/";
    private static final String resetPasswordLink = "http://localhost:3000/restore-password/";


    public static void sendEmail(String to, String subject, String content) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");


        Session session = Session.getDefaultInstance(props,
                new jakarta.mail.Authenticator() {
                    protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new jakarta.mail.PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);

            // Set the email content to HTML
            message.setContent(content, "text/html; charset=utf-8");


            try{
                Transport.send(message);
            } catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }


        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public static void sendVerificationEmail(String to, String token) {
        String subject = "Account Verification";
        String content = "<div style=\"font-family: Arial, sans-serif; color: #333; border: 1px solid #ddd; padding: 20px; border-radius: 10px;\">" +
                "<h1 style=\"color: #445566;\">Dear User,</h1>" +
                "<p>We have received a request to verify your account. If you made this request, please click the button below:</p>" +
                "<div style=\"margin: 20px 0;\">" +
                "<a href=\"" + registrationLink+token + "\" style=\"background-color: #008CBA; color: white; text-decoration: none; padding: 15px 32px; text-align: center; display: inline-block; font-size: 16px; margin: 4px 2px; cursor: pointer; border: none;\">Verify Account</a>" +
                "</div>" +
                "<p>Please note that this link will expire in 1 hour.</p>"+
                "<p>If you did not make this request, please ignore this email.</p>" +
                "<p>Best Regards,</p>" +
                "<p>InnovationLab</p>" +
                "</div>";

        sendEmail(to, subject, content);
    }

    public static void sendPasswordResetEmail(String to, String token) {
        String subject = "Password Reset";
        String content = "<div style=\"font-family: Arial, sans-serif; color: #333; border: 1px solid #ddd; padding: 20px; border-radius: 10px;\">" +
                "<h1 style=\"color: #445566;\">Dear User,</h1>" +
                "<p>We have received a request to reset your password. If you made this request, please click the button below:</p>" +
                "<div style=\"margin: 20px 0;\">" +
                "<a href=\"" + resetPasswordLink+token + "\" style=\"background-color: #008CBA; color: white; text-decoration: none; padding: 15px 32px; text-align: center; display: inline-block; font-size: 16px; margin: 4px 2px; cursor: pointer; border: none;\">Reset Password</a>" +
                "</div>" +
                "<p>Please note that this link will expire in 1 hour.</p>"+
                "<p>If you did not make this request, please ignore this email.</p>" +
                "<p>Best Regards,</p>" +
                "<p>InnovationLab</p>" +
                "</div>";
        sendEmail(to, subject, content);
    }
}