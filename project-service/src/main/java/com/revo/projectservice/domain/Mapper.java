package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.RequestProjectDto;
import com.revo.projectservice.domain.dto.RequestTaskDto;

class Mapper {
    public static Task mapTaskFromRestDto(RequestTaskDto requestTaskDto) {
        return Task.Builder.aTaskDto()
                .id(requestTaskDto.getId())
                .name(requestTaskDto.getName())
                .description(requestTaskDto.getDescription())
                .startDate(requestTaskDto.getStartDate())
                .endDate(requestTaskDto.getEndDate())
                .build();
    }

    public static Project mapProjectFromRestDto(RequestProjectDto requestProjectDto) {
        return Project.Builder.aProjectDto()
                .id(requestProjectDto.getId())
                .name(requestProjectDto.getName())
                .startDate(requestProjectDto.getStartDate())
                .endDate(requestProjectDto.getEndDate())
                .build();
    }
}
