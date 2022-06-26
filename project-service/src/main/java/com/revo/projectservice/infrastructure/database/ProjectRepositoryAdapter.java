package com.revo.projectservice.infrastructure.database;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.port.ProjectRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.revo.projectservice.infrastructure.database.EntityMapper.Mapper;

@Component
class ProjectRepositoryAdapter implements ProjectRepositoryPort {

    private final ProjectRepository projectRepository;

    ProjectRepositoryAdapter(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Flux<ProjectDto> getAllProjects(String owner) {
        return projectRepository.getAllByOwner(owner)
                .map(Mapper::toDto);
    }

    @Override
    public Mono<ProjectDto> getProject(String id, String owner) {
        return projectRepository.getByOwnerAndId(owner, id)
                .map(Mapper::toDto);
    }

    @Override
    public Mono<ProjectDto> save(ProjectDto projectDto) {
        return projectRepository.save(Mapper.toEntity(projectDto))
                .map(Mapper::toDto);
    }

    @Override
    public Mono<ProjectDto> delete(String id, String owner) {
        return projectRepository.deleteById(id)
                .then(getProject(id, owner));
    }

    @Override
    public Mono<ProjectDto> editProject(String id, ProjectDto projectDto) {
        projectDto.setId(id);
        return projectRepository.save(Mapper.toEntity(projectDto))
                .map(Mapper::toDto);
    }
}
