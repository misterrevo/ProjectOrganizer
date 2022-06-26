package com.revo.projectservice.infrastructure.database;

import com.revo.projectservice.domain.dto.ProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.Mapping;

@Mapper
interface EntityMapper {

    EntityMapper Mapper = Mappers.getMapper(EntityMapper.class);

    ProjectDto toDto(ProjectEntity projectEntity);
    ProjectEntity toEntity(ProjectDto projectDto);
}
