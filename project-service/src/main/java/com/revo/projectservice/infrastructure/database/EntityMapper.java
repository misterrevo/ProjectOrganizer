package com.revo.projectservice.infrastructure.database;

import com.revo.projectservice.domain.dto.ProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface EntityMapper {

    EntityMapper Mapper = Mappers.getMapper(EntityMapper.class);

    ProjectDto mapProjectEntityToDto(ProjectEntity projectEntity);
    ProjectEntity mapProjectDtoToEntity(ProjectDto projectDto);
}
