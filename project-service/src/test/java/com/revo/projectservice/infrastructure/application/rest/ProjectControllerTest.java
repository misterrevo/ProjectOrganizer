package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.domain.Project;
import com.revo.projectservice.domain.dto.RequestProjectDto;
import com.revo.projectservice.domain.exception.NoPermissionException;
import com.revo.projectservice.domain.port.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ProjectController.class)
@RunWith(SpringRunner.class)
class ProjectControllerTest {
    private static final String PROJECT_NAME = "Test";
    private static final String OWNER_NAME = "Owner";
    private static final String PROJECTS_END_POINT = "/projects";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String EXAMPLE_TOKEN = "token";
    private static final String SINGLE_PROJECT_END_POINT = "/projects/1";
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProjectService projectService;

    private final Project project = Project.Builder.aProjectDto()
            .name(PROJECT_NAME)
            .owner(OWNER_NAME)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now())
            .build();

    @Test
    void shouldReturn200WhileGettingAllProjects() {
        //given
        //when
        when(projectService.getAllProjectsByToken(anyString())).thenReturn(Flux.just(project));
        //then
        webTestClient
                .get()
                .uri(PROJECTS_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileGettingAllProjects(){
        //given
        //when
        when(projectService.getAllProjectsByToken(anyString())).thenReturn(Flux.error(new NoPermissionException()));
        //then
        webTestClient
                .get()
                .uri(PROJECTS_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn200WhileGettingProject() {
        //given
        //when
        when(projectService.getProjectByTokenAndId(anyString(), anyString())).thenReturn(Mono.just(project));
        //then
        webTestClient
                .get()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileGettingProject(){
        //given
        //when
        when(projectService.getProjectByTokenAndId(anyString(), anyString())).thenReturn(Mono.error(new NoPermissionException()));
        //then
        webTestClient
                .get()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn201WhileCreatingProject() {
        //given
        //when
        when(projectService.createProjectByToken(anyString(), any(RequestProjectDto.class))).thenReturn(Mono.just(project));
        //then
        webTestClient
                .post()
                .uri(PROJECTS_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .bodyValue(mapOnRequestProjectDto(project))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void shouldReturn401WhileCreatingProject(){
        //given
        //when
        when(projectService.createProjectByToken(anyString(), any(RequestProjectDto.class))).thenReturn(Mono.error(new NoPermissionException()));
        //then
        webTestClient
                .post()
                .uri(PROJECTS_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .bodyValue(mapOnRequestProjectDto(project))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn200WhileDeletingProject() {
        //given
        //when
        when(projectService.deleteProjectByTokenAndId(anyString(), anyString())).thenReturn(Mono.just(project));
        //then
        webTestClient
                .delete()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileDeletingProject() {
        //given
        //when
        when(projectService.deleteProjectByTokenAndId(anyString(), anyString())).thenReturn(Mono.error(new NoPermissionException()));
        //then
        webTestClient
                .delete()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn200WhileEditingProject() {
        //given
        //when
        when(projectService.editProjectByTokenAndId(anyString(), any(RequestProjectDto.class))).thenReturn(Mono.just(project));
        //then
        webTestClient
                .patch()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .bodyValue(mapOnRequestProjectDto(project))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileEditingProject() {
        //given
        //when
        when(projectService.editProjectByTokenAndId(anyString(), any(RequestProjectDto.class))).thenReturn(Mono.error(new NoPermissionException()));
        //then
        webTestClient
                .patch()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .bodyValue(mapOnRequestProjectDto(project))
                .exchange()
                .expectStatus().isUnauthorized();
    }
    
    private RequestProjectDto mapOnRequestProjectDto(Project project){
        return new RequestProjectDto(project.getId(), project.getName(), project.getStartDate(), project.getEndDate());
    }
}