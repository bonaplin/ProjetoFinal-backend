package aor.project.innovationlab.dto.response;

public class ResponseYesNoInviteDto {
    private boolean accept;
    private Long userId;

    public ResponseYesNoInviteDto() {
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
