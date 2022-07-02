package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.dto.RequestProjectDto;
import com.revo.projectservice.domain.dto.RequestTaskDto;
import com.revo.projectservice.domain.dto.TaskDto;

import java.util.List;
import java.util.stream.Collectors;

class Mapper {
    static Project mapProjectFromDto(ProjectDto projectDto){
        return Project.Builder.aProject()
                .id(projectDto.getId())
                .owner(projectDto.getOwner())
                .name(projectDto.getName())
                .startDate(projectDto.getStartDate())
                .endDate(projectDto.getEndDate())
                .tasks(mapTaskListFromDto(projectDto.getTasks()))
                .build();
    }

    private static List<Task> mapTaskListFromDto(List<TaskDto> tasks) {
        return tasks.stream()
                .map(Mapper::mapTaskFromDto)
                .collect(Collectors.toList());
    }

    static Task mapTaskFromDto(TaskDto taskDto) {
        return Task.Builder.aTask()
                .id(taskDto.getId())
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .startDate(taskDto.getStartDate())
                .endDate(taskDto.getEndDate())
                .build();
    }

    static ProjectDto mapProjectToDto(Project project){
        return ProjectDto.Builder.aProjectDto()
                .id(project.getId())
                .owner(project.getOwner())
                .name(project.getName())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .tasks(mapTaskListToDto(project.getTasks()))
                .build();
    }

    private static List<TaskDto> mapTaskListToDto(List<Task> tasks) {
        return tasks.stream()
                .map(Mapper::mapTaskToDto)
                .collect(Collectors.toList());
    }

    static TaskDto mapTaskToDto(Task task) {
        return TaskDto.Builder.aTaskDto()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .build();
    }

    public static ProjectDto mapProjectDtoFromRestDto(RequestProjectDto projectDto) {
        return ProjectDto.Builder.aProjectDto()
                .id(projectDto.getId())
                .name(projectDto.getName())
                .startDate(projectDto.getStartDate())
                .endDate(projectDto.getEndDate())
                .build();
    }

    public static TaskDto mapTaskDtoFromRestDto(RequestTaskDto taskDto) {
        return TaskDto.Builder.aTaskDto()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .startDate(taskDto.getStartDate())
                .endDate(taskDto.getEndDate())
                .build();
    }
}
