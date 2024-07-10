package aor.project.innovationlab.dto.statistics;

import aor.project.innovationlab.dto.lab.LabDto;

import java.util.List;

public class UserSettingsDto {
    private Integer timeout;
    private List<LabDto> labs;
    private StatisticsDto statistics;

    public UserSettingsDto() {
    }

    public List<LabDto> getLabs() {
        return labs;
    }

    public void setLabs(List<LabDto> labs) {
        this.labs = labs;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public StatisticsDto getStatistics() {
        return statistics;
    }

    public void setStatistics(StatisticsDto statistics) {
        this.statistics = statistics;
    }
}
