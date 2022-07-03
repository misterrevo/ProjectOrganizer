package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.RequestProjectDto;
import com.revo.projectservice.domain.dto.RequestTaskDto;

class Mapper {
    public static Task mapTaskFromRestDto(RequestTaskDto requestTaskDto) {
        return Task.Builder.aTaskDto()
                .id(requestTaskDto.id)
                .name(requestTaskDto.name)
                .description(requestTaskDto.description)
                .startDate(requestTaskDto.startDate)
                .endDate(requestTaskDto.endDate)
                .build();
    }

    public static Project mapProjectFromRestDto(RequestProjectDto requestProjectDto) {
        return Project.Builder.aProjectDto()
                .id(requestProjectDto.id)
                .name(requestProjectDto.name)
                .startDate(requestProjectDto.startDate)
                .endDate(requestProjectDto.endDate)
                .build();
    }
}
