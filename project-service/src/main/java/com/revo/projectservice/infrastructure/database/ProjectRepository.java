package com.revo.projectservice.infrastructure.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

interface ProjectRepository extends ReactiveMongoRepository<ProjectEntity, String> {
    Flux<ProjectEntity> getAllByOwner(String owner);
}
