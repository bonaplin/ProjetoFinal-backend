package aor.project.innovationlab.dto.project;

public class ProjectInviteDto {

    private long id;
    private String invitedUserEmail;

    public ProjectInviteDto() {
    }

    public ProjectInviteDto(long id, String invitedUserEmail) {
        this.id = id;
        this.invitedUserEmail = invitedUserEmail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInvitedUserEmail() {
        return invitedUserEmail;
    }

    public void setInvitedUserEmail(String invitedUserEmail) {
        this.invitedUserEmail = invitedUserEmail;
    }

    @Override
    public String toString() {
        return "ProjectInviteDto{" +
                "id=" + id +
                ", invitedUserEmail='" + invitedUserEmail + '\'' +
                '}';
    }
}
