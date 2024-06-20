package aor.project.innovationlab.dto.emails;

public class EmailResponseDto {
    private String body;
    private String subject;
    private String from;
    private String to;
    private long id;
    private long groupId;

    public EmailResponseDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "EmailResponseDto{" +
                "body='" + body + '\'' +
                ", subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", id=" + id +
                ", groupId=" + groupId +
                '}';
    }
}
