package com.revo.projectservice.domain.dto;

import java.time.LocalDateTime;

public class RequestProjectDto {

    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public RequestProjectDto(String name, LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
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
