package aor.project.innovationlab.dto;


import java.util.List;

public class PagAndUnreadResponse<T> extends PaginatedResponse<T>{

    private Long unreadCount;

    public PagAndUnreadResponse() {
    }

    public PagAndUnreadResponse(List<T> results, int totalPages, Long unreadCount) {
        super(results, totalPages);
        this.unreadCount = unreadCount;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }

}
