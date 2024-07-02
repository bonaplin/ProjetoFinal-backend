package aor.project.innovationlab.dto.project.notes;

public class NoteIdNoteDto {
    private Long id;
    private String note;

    public NoteIdNoteDto() {
    }

    public NoteIdNoteDto(Long id, String note) {
        this.id = id;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
