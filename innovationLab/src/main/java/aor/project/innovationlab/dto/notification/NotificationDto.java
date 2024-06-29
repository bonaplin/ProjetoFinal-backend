package aor.project.innovationlab.dto.notification;

import aor.project.innovationlab.enums.NotificationType;

import java.time.Instant;

public class NotificationDto {
    private long id;
    private String senderEmail;
    private String receiverEmail;
    private String content;
    private int notificationType;
    private long projectId;
    private Instant instant;
    private boolean read;
    private String senderName;
    private String senderImg;

    public NotificationDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public String getSenderImg() {
        return senderImg;
    }

    public void setSenderImg(String senderImg) {
        this.senderImg = senderImg;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

//    public NotificationType getType() {
//        return type;
//    }
//
//    public void setType(NotificationType type) {
//        this.type = type;
//    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "NotificationDto{" +
                "id=" + id +
                ", senderEmail='" + senderEmail + '\'' +
                ", receiverEmail='" + receiverEmail + '\'' +
                ", content='" + content + '\'' +
                ", notificationType=" + notificationType +
                ", projectId=" + projectId +
                ", instant=" + instant +
                ", read=" + read +
                ", senderName='" + senderName + '\'' +
                ", senderImg='" + senderImg + '\'' +
                '}';
    }
}
