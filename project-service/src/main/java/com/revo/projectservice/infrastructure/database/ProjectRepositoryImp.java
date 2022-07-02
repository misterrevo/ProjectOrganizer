package com.revo.projectservice.infrastructure.database;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.port.ProjectRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.revo.projectservice.infrastructure.database.EntityMapper.Mapper;

@Component
class ProjectRepositoryImp implements ProjectRepository {
    private final com.revo.projectservice.infrastructure.database.ProjectRepository projectRepository;

    ProjectRepositoryImp(com.revo.projectservice.infrastructure.database.ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Flux<ProjectDto> getAllProjectsByOwner(String owner) {
        return getAllByOwner(owner)
                .map(Mapper::mapProjectEntityToDto);
    }

    private Flux<ProjectEntity> getAllByOwner(String owner) {
        return projectRepository.getAllByOwner(owner);
    }

    @Override
    public Mono<ProjectDto> getProjectByOwner(String id, String owner) {
        return getByOwnerAndId(id, owner)
                .map(Mapper::mapProjectEntityToDto);
    }

    private Mono<ProjectEntity> getByOwnerAndId(String id, String owner) {
        return projectRepository.getByOwnerAndId(owner, id);
    }

    @Override
    public Mono<ProjectDto> saveProject(ProjectDto projectDto) {
        return getSavedProjectEntity(projectDto)
                .map(Mapper::mapProjectEntityToDto);
    }

    private Mono<ProjectEntity> getSavedProjectEntity(ProjectDto projectDto) {
        return projectRepository.save(Mapper.mapProjectDtoToEntity(projectDto));
    }

    @Override
    public Mono<ProjectDto> deleteProject(String id, String owner) {
        return deleteById(id)
                .then(getProjectByOwner(id, owner));
    }

    private Mono<Void> deleteById(String id) {
        return projectRepository.deleteById(id);
    }
}
