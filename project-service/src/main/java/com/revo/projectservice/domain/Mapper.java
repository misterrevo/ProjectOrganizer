package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.dto.TaskDto;

import java.util.List;
import java.util.stream.Collectors;

class Mapper {

    static Project fromDto(ProjectDto projectDto){
        return Project.Builder.aProject()
                .id(projectDto.getId())
                .owner(projectDto.getOwner())
                .name(projectDto.getName())
                .startDate(projectDto.getStartDate())
                .endDate(projectDto.getEndDate())
                .tasks(fromDto(projectDto.getTasks()))
                .build();
    }

    private static List<Task> fromDto(List<TaskDto> tasks) {
        return tasks.stream()
                .map(Mapper::fromDto)
                .collect(Collectors.toList());
    }

    static Task fromDto(TaskDto taskDto) {
        return Task.Builder.aTask()
                .id(taskDto.getId())
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .startDate(taskDto.getStartDate())
                .endDate(taskDto.getEndDate())
                .build();
    }

    static ProjectDto toDto(Project project){
        return ProjectDto.Builder.aProjectDto()
                .id(project.getId())
                .owner(project.getOwner())
                .name(project.getName())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .tasks(toDto(project.getTasks()))
                .build();
    }

    private static List<TaskDto> toDto(List<Task> tasks) {
        return tasks.stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
    }

    static TaskDto toDto(Task task) {
        return TaskDto.Builder.aTaskDto()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .build();
    }
}
