package com.ortecfinance.tasklist;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TaskService {
    private final TaskStorage taskStorage;

    public TaskService(TaskStorage taskStorage) {
        this.taskStorage = taskStorage;
    }

    public void createProject(String projectName) {
        taskStorage.addProject(projectName);
    }

    public boolean doesProjectExist(String projectName) {
        return taskStorage.projectExists(projectName);
    }

    public void createTask(String projectName, String taskDescription) {
        taskStorage.addTask(projectName, taskDescription);
    }

    public void markTask(long taskId, boolean done) {
        Task task = taskStorage.returnTaskByID(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Could not find task " + taskId);
        }
        task.setDone(done);
    }

    public void setTaskDeadline(long taskId, LocalDate deadline) {
        Task task = taskStorage.returnTaskByID(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Could not find task " + taskId);
        }
        task.setDeadline(deadline);
    }

    public Map<String, List<Task>> getAllProjects() {
        return taskStorage.getAllProjects();
    }

    public Map<String, List<Task>> getTasksTodaysDeadline() {
        return taskStorage.getTasksTodaysDeadline();
    }

    public Map<LocalDate, Map<String, List<Task>>> getTasksSortedByDeadline() {
        return taskStorage.getTasksSortedByDeadline();
    }












}
