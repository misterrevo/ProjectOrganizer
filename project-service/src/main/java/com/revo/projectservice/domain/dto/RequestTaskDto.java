package com.revo.projectservice.domain.dto;

import java.time.LocalDateTime;

public class RequestTaskDto {
    public String projectId;
    public String id;
    public String name;
    public String description;
    public LocalDateTime startDate;
    public LocalDateTime endDate;

    public RequestTaskDto(String projectId, String id, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.projectId = projectId;
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
