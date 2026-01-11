package com.ortecfinance.tasklist;

public class TaskService {
    private final TaskStorage taskStorage;

    public TaskService(TaskStorage taskStorage) {
        this.taskStorage = taskStorage;
    }
}
