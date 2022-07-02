package com.revo.projectservice.domain.dto;

import java.time.LocalDateTime;

public class RequestTaskDto {
    private String projectId;
    private String id;
    private final String name;
    private final String description;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public RequestTaskDto(String projectId, String id, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.projectId = projectId;
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getProjectId() {
        return projectId;
    }

    public RequestTaskDto setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getId() {
        return id;
    }

    public RequestTaskDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }


    public static final class Builder {
        private String projectId;
        private String id;
        private String name;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        private Builder() {
        }

        public static Builder aRequestTaskDto() {
            return new Builder();
        }

        public Builder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
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

        public RequestTaskDto build() {
            return new RequestTaskDto(projectId, id, name, description, startDate, endDate);
        }
    }
}
