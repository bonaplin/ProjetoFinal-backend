package aor.project.innovationlab.dto;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> results;
    private int totalPages;


    public PaginatedResponse() {
    }

    public PaginatedResponse(List<T> results, int totalPages) {
        this.results = results;
        this.totalPages = totalPages;
    }

    public List<T> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
