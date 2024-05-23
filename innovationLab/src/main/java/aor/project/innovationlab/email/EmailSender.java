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
        String subject = "Verificação de conta";
        String content = "<h1>Olá, " + "!</h1>" +
                "<p>Para verificar a sua conta, clique no link abaixo:</p>" +
                "<p><a href=\"" + registrationLink+token + "\">Verificar conta</a></p>"+
                "<p>Este link apenas é válido por 1 hora.</p>"+
                "<p>Se você não se registou, por favor ignore este email.</p>";

        sendEmail(to, subject, content);
    }

    public static void sendPasswordResetEmail(String to, String token) {
        String subject = "Redefinição de senha";
        String content = "<h1>Olá, " + "!</h1>" +
                "<p>Para redefinir sua senha, clique no link abaixo:</p>" +
                "<p><a href=\"" + resetPasswordLink+token + "\">Redefinir senha</a></p>"+
                "<p>Este link apenas é válido por 1 hora.</p>"+
                "<p>Se você não pretende restaurar a sua palavra-passe, por favor ignore este email.</p>";
        sendEmail(to, subject, content);
    }
}