package aor.project.innovationlab.dto.response;

public class LabelValueDto {
    private String label;
    private Long value;


    public LabelValueDto() {
    }

    public LabelValueDto(String label, Long value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
