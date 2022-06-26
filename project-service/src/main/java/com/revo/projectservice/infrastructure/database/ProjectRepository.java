package com.revo.projectservice.infrastructure.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface ProjectRepository extends ReactiveMongoRepository<ProjectEntity, String> {
    Flux<ProjectEntity> getAllByOwner(String owner);
    Mono<ProjectEntity> getByOwnerAndId(String owner, String id);
}
