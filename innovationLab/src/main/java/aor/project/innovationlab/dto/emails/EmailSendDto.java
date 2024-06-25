package aor.project.innovationlab.dto.emails;

public class EmailSendDto {
    private String to;
    private String subject;
    private String body;

    public EmailSendDto() {
    }

    public EmailSendDto(String body, String to, String subject) {
        this.body = body;

        this.to = to;
        this.subject = subject;
    }

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

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
