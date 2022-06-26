package com.revo.projectservice.domain.dto;

import java.time.LocalDateTime;

public class RestTaskDto {

    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public RestTaskDto(String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
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
}
