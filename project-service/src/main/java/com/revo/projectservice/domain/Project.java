package com.revo.projectservice.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class Project {
    private String id;
    private String owner;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Task> tasks;

    public Project(String id, String owner, String name, LocalDateTime startDate, LocalDateTime endDate, List<Task> tasks) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tasks = tasks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public static final class Builder {
        private String id;
        private String owner;
        private String name;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<Task> tasks = new ArrayList<>();

        private Builder() {
        }

        public static Builder aProject() {
            return new Builder();
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder owner(String owner){
            this.owner = owner;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder tasks(List<Task> tasks) {
            this.tasks = tasks;
            return this;
        }

        public Project build() {
            return new Project(id, owner, name, startDate, endDate, tasks);
        }
    }
}
