package aor.project.innovationlab.dto.task;

import java.time.LocalDate;

public class TaskDateUpdateDto {
    private LocalDate initialDate;
    private LocalDate finalDate;

    public TaskDateUpdateDto() {
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public LocalDate getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(LocalDate finalDate) {
        this.finalDate = finalDate;
    }
}
