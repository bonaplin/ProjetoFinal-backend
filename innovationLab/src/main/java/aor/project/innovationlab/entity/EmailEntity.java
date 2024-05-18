package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;
@Entity
@Table(name = "email")
public class EmailEntity implements Serializable {

    //FAZ SENTIDO TER ENTITY DE EMAIL?
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name = "email_sender", nullable = false, unique = true)
    private String emailSender;

    @Column(name = "email_receiver", nullable = false, unique = true)
    private String emailReceiver;

    @Column(name = "subject", nullable = false, unique = true)
    private String subject;

    @Column(name = "body", nullable = false, unique = true)
    private String body;

    @Column(name = "sent_date", nullable = false, unique = true)
    private String sentDate;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public long getId() {
        return id;
    }

    public String getEmailSender() {
        return emailSender;
    }

    public void setEmailSender(String emailSender) {
        this.emailSender = emailSender;
    }

    public String getEmailReceiver() {
        return emailReceiver;
    }

    public void setEmailReceiver(String emailReceiver) {
        this.emailReceiver = emailReceiver;
    }
}
