package com.revo.projectservice.infrastructure.database;

import com.revo.projectservice.domain.Project;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface EntityMapper {
    EntityMapper Mapper = Mappers.getMapper(EntityMapper.class);

    Project mapProjectEntityToDto(ProjectEntity projectEntity);
    ProjectEntity mapProjectDtoToEntity(Project project);
}
