package aor.project.innovationlab.dto.response;

public class ContentUnreadResponse {
    private long unreadCount;
    private Object content;

    public ContentUnreadResponse() {
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
