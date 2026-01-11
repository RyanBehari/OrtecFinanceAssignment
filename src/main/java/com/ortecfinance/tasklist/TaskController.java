package com.ortecfinance.tasklist;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public void createProject(@RequestBody Map<String, String> request) {
        taskService.createProject(request.get("name"));
    }

}


