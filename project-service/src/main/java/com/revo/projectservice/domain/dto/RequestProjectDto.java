package com.revo.projectservice.domain.dto;

import java.time.LocalDateTime;

public class RequestProjectDto {
    public String id;
    public String name;
    public LocalDateTime startDate;
    public LocalDateTime endDate;

    public RequestProjectDto(String id, String name, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
