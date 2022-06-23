package com.revo.projectservice.domain.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectDto {

    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<TaskDto> tasks;

    public ProjectDto(String name, LocalDateTime startDate, LocalDateTime endDate, List<TaskDto> tasks) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }

    public static final class Builder {
        private String name;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<TaskDto> tasks = new ArrayList<>();

        private Builder() {
        }

        public static Builder aProjectDto() {
            return new Builder();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder tasks(List<TaskDto> tasks) {
            this.tasks = tasks;
            return this;
        }

        public ProjectDto build() {
            return new ProjectDto(name, startDate, endDate, tasks);
        }
    }
}
