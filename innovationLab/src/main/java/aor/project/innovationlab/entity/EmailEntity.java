package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "email")
public class EmailEntity implements Serializable {

    //FAZ SENTIDO TER ENTITY DE EMAIL?
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver;

    @Column(name = "subject", nullable = false, unique = false)
    private String subject;

    @Column(name = "body", nullable = false, unique = false)
    private String body;

    @Column(name = "sent_date", nullable = false, unique = false)
    private Instant sentDate;

    @Column(name = "group_id", nullable = false, unique = false)
    private long groupId;

    @Column(name = "is_read", nullable = false, unique = false)
    private boolean isRead;

    @Column(name = "active", nullable = false, unique = false)
    private boolean active;

    @Column(name = "deleted_by_sender")
    private boolean deletedBySender;

    @Column(name = "deleted_by_receiver")
    private boolean deletedByReceiver;


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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Instant getSentDate() {
        return sentDate;
    }

    public void setSentDate(Instant sentDate) {
        this.sentDate = sentDate;
    }

    public long getId() {
        return id;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public boolean isDeletedByReceiver() {
        return deletedByReceiver;
    }

    public void setDeletedByReceiver(boolean deletedByReceiver) {
        this.deletedByReceiver = deletedByReceiver;
    }

    public boolean isDeletedBySender() {
        return deletedBySender;
    }

    public void setDeletedBySender(boolean deletedBySender) {
        this.deletedBySender = deletedBySender;
    }

    @PrePersist
    public void prePersist() {
        this.active = true;
        this.isRead = false;
        this.sentDate = Instant.now();
        this.deletedByReceiver = false;
        this.deletedBySender = false;
    }
}
