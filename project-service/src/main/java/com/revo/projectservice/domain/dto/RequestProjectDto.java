package com.revo.projectservice.domain.dto;

import java.time.LocalDateTime;

public class RequestProjectDto {

    private String id;
    private final String name;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public RequestProjectDto(String id, String name, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public RequestProjectDto setIdAndReturn(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
}
