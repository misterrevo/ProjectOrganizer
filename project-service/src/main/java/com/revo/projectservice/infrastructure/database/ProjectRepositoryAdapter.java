package com.revo.projectservice.infrastructure.database;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.port.ProjectRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.revo.projectservice.infrastructure.database.EntityMapper.Mapper;

@Component
public
class ProjectRepositoryAdapter implements ProjectRepositoryPort {

    private final ProjectRepository projectRepository;

    ProjectRepositoryAdapter(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Flux<ProjectDto> getAllProjectsByOwner(String owner) {
        return projectRepository.getAllByOwner(owner)
                .map(Mapper::mapProjectEntityToDto);
    }

    @Override
    public Mono<ProjectDto> getProjectByOwner(String id, String owner) {
        return projectRepository.getByOwnerAndId(owner, id)
                .map(Mapper::mapProjectEntityToDto);
    }

    @Override
    public Mono<ProjectDto> saveProject(ProjectDto projectDto) {
        return projectRepository.save(Mapper.mapProjectDtoToEntity(projectDto))
                .map(Mapper::mapProjectEntityToDto);
    }

    @Override
    public Mono<ProjectDto> deleteProject(String id, String owner) {
        return projectRepository.deleteById(id)
                .then(getProjectByOwner(id, owner));
    }
}
