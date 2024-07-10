package aor.project.innovationlab.dto.statistics;

import aor.project.innovationlab.dto.response.NameCountDto;

import java.util.List;

public class StatisticsDto {
    private long totalProjects;
    private double averageParticipants;
    private long readyProjects;
    private long inProgressProjects;
    private long finishedProjects;
    private long cancelledProjects;
    private double averageExecutionTime;
    private double percentageApproved;
    private double percentageFinished;
    private double percentageCancelled;

    public StatisticsDto(long totalProjects, double averageParticipants, long readyProjects, long inProgressProjects,
                         long finishedProjects, long cancelledProjects, double averageExecutionTime) {
        this.totalProjects = totalProjects;
        this.averageParticipants = averageParticipants;
        this.readyProjects = readyProjects;
        this.inProgressProjects = inProgressProjects;
        this.finishedProjects = finishedProjects;
        this.cancelledProjects = cancelledProjects;
        this.averageExecutionTime = averageExecutionTime;
        if (totalProjects > 0) {
            this.percentageApproved = ((double) (readyProjects + inProgressProjects) / totalProjects) * 100;
            this.percentageFinished = ((double) finishedProjects / totalProjects) * 100;
            this.percentageCancelled = ((double) cancelledProjects / totalProjects) * 100;
        } else {
            this.percentageApproved = 0;
            this.percentageFinished = 0;
            this.percentageCancelled = 0;
        }
    }


    public StatisticsDto() {
    }

    public double getPercentageApproved() {
        return percentageApproved;
    }

    public void setPercentageApproved(double percentageApproved) {
        this.percentageApproved = percentageApproved;
    }

    public double getPercentageFinished() {
        return percentageFinished;
    }

    public void setPercentageFinished(double percentageFinished) {
        this.percentageFinished = percentageFinished;
    }

    public double getPercentageCancelled() {
        return percentageCancelled;
    }

    public void setPercentageCancelled(double percentageCancelled) {
        this.percentageCancelled = percentageCancelled;
    }

    public double getAverageExecutionTime() {
        return averageExecutionTime;
    }

    public void setAverageExecutionTime(double averageExecutionTime) {
        this.averageExecutionTime = averageExecutionTime;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getReadyProjects() {
        return readyProjects;
    }

    public void setReadyProjects(long readyProjects) {
        this.readyProjects = readyProjects;
    }

    public long getInProgressProjects() {
        return inProgressProjects;
    }

    public void setInProgressProjects(long inProgressProjects) {
        this.inProgressProjects = inProgressProjects;
    }

    public long getCancelledProjects() {
        return cancelledProjects;
    }

    public void setCancelledProjects(long cancelledProjects) {
        this.cancelledProjects = cancelledProjects;
    }

    public long getFinishedProjects() {
        return finishedProjects;
    }

    public void setFinishedProjects(long finishedProjects) {
        this.finishedProjects = finishedProjects;
    }

    public double getAverageParticipants() {
        return averageParticipants;
    }

    public void setAverageParticipants(double averageParticipants) {
        this.averageParticipants = averageParticipants;
    }
}
