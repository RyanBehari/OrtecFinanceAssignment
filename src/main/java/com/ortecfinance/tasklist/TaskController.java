package com.ortecfinance.tasklist;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.ortecfinance.tasklist.CommandParser.parseDate;
import static com.ortecfinance.tasklist.TaskFormatter.formatDate;

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

    @GetMapping
    public Map<String, List<Task>> getAllProjects() {
        return taskService.getAllProjects();
    }

    @PostMapping("/{projectName}/tasks")
    public void createTask(@PathVariable String projectName,
                           @RequestBody Map<String, String> request) {
        taskService.createTask(projectName, request.get("description"));
    }

    @PutMapping("/{projectName}/tasks/{taskId}")
    public void updateTaskDeadline(@PathVariable String projectName,
                                   @PathVariable long taskId,
                                   @RequestParam String deadline) {
        LocalDate date = parseDate(deadline);
        taskService.setTaskDeadline(taskId, date);
    }

    @GetMapping("/view_by_deadline")
    public Map<String, Map<String, List<Task>>> getTasksByDeadline() {
        Map<LocalDate, Map<String, List<Task>>> tasksByDeadline = taskService.getTasksSortedByDeadline();
        Map<String, Map<String, List<Task>>> result = new LinkedHashMap<>();

        for (Map.Entry<LocalDate, Map<String, List<Task>>> entry : tasksByDeadline.entrySet()) {
            String dateKey;
            if (entry.getKey() != null) {
                dateKey = formatDate(entry.getKey());
            } else {
                dateKey = "No deadline";
            }
            result.put(dateKey, entry.getValue());
        }

        return result;
    }



}


