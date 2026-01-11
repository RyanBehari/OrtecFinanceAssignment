package com.ortecfinance.tasklist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    public void setup() {
        TaskStorage taskStorage = new TaskStorage();
        taskService = new TaskService(taskStorage);
    }

    @Test
    void testCreateProject() {
        taskService.createProject("secrets");
        assertThat(taskService.doesProjectExist("secrets"), is(true));
    }

    @Test
    void testCreateDuplicateProjectThrowsException() {
        taskService.createProject("secrets");
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createProject("secrets");
        });
    }

    @Test
    void testProjectExistsReturnsFalseForNonExistent() {
        assertThat(taskService.doesProjectExist("nonexistent"), is(false));
    }

    @Test
    void testCreateTask() {
        taskService.createProject("training");
        taskService.createTask("training", "Learn Spring Boot");

        Map<String, List<Task>> projects = taskService.getAllProjects();
        assertThat(projects.get("training"), hasSize(1));
        assertThat(projects.get("training").getFirst().getDescription(), is("Learn Spring Boot"));
    }

    @Test
    void testCreateTaskForNonExistentProjectThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask("nonexistent", "Some task");
        });
    }

    @Test
    void testMarkTaskAsDone() {
        taskService.createProject("secrets");
        taskService.createTask("secrets", "Eat more donuts.");

        taskService.markTask(1, true);

        Map<String, List<Task>> projects = taskService.getAllProjects();
        assertThat(projects.get("secrets").getFirst().isDone(), is(true));
    }

    @Test
    void testMarkTaskAsUndone() {
        taskService.createProject("secrets");
        taskService.createTask("secrets", "Eat more donuts.");

        taskService.markTask(1, true);
        taskService.markTask(1, false);

        Map<String, List<Task>> projects = taskService.getAllProjects();
        assertThat(projects.get("secrets").getFirst().isDone(), is(false));
    }

    @Test
    void testMarkNonExistentTaskThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.markTask(999, true);
        });
    }

    @Test
    void testSetTaskDeadline() {
        taskService.createProject("secrets");
        taskService.createTask("secrets", "Eat more donuts.");

        LocalDate deadline = LocalDate.of(2026, 1, 15);
        taskService.setTaskDeadline(1, deadline);

        Map<String, List<Task>> projects = taskService.getAllProjects();
        assertThat(projects.get("secrets").getFirst().getDeadline(), is(deadline));
    }

    @Test
    void testSetDeadlineForNonExistentTaskThrowsException() {
        LocalDate deadline = LocalDate.of(2026, 1, 15);
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.setTaskDeadline(999, deadline);
        });
    }

    @Test
    void testGetAllProjectsEmpty() {
        Map<String, List<Task>> projects = taskService.getAllProjects();
        assertThat(projects.isEmpty(), is(true));
    }

    @Test
    void testGetAllProjectsWithMultipleProjects() {
        taskService.createProject("secrets");
        taskService.createProject("training");
        taskService.createTask("secrets", "Task 1");
        taskService.createTask("training", "Task 2");

        Map<String, List<Task>> projects = taskService.getAllProjects();
        assertThat(projects.size(), is(2));
        assertThat(projects.get("secrets"), hasSize(1));
        assertThat(projects.get("training"), hasSize(1));
    }

    @Test
    void testGetTasksTodaysDeadlineEmpty() {
        Map<String, List<Task>> todaysTasks = taskService.getTasksTodaysDeadline();
        assertThat(todaysTasks.isEmpty(), is(true));
    }

    @Test
    void testGetTasksTodaysDeadline() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        taskService.createProject("work");
        taskService.createTask("work", "Task today");
        taskService.createTask("work", "Task tomorrow");
        taskService.createTask("work", "Task no deadline");

        taskService.setTaskDeadline(1, today);
        taskService.setTaskDeadline(2, tomorrow);

        Map<String, List<Task>> todaysTasks = taskService.getTasksTodaysDeadline();
        assertThat(todaysTasks.get("work"), hasSize(1));
        assertThat(todaysTasks.get("work").getFirst().getDescription(), is("Task today"));
    }

    @Test
    void testGetTasksSortedByDeadline() {
        LocalDate date1 = LocalDate.of(2026, 1, 15);
        LocalDate date2 = LocalDate.of(2026, 1, 20);

        taskService.createProject("work");
        taskService.createTask("work", "Task 1");
        taskService.createTask("work", "Task 2");
        taskService.createTask("work", "Task 3");

        taskService.setTaskDeadline(1, date1);
        taskService.setTaskDeadline(3, date2);

        Map<LocalDate, Map<String, List<Task>>> sorted = taskService.getTasksSortedByDeadline();

        assertThat(sorted.containsKey(date1), is(true));
        assertThat(sorted.containsKey(date2), is(true));
        assertThat(sorted.containsKey(null), is(true));
        assertThat(sorted.get(date1).get("work"), hasSize(1));
        assertThat(sorted.get(date2).get("work"), hasSize(1));
        assertThat(sorted.get(null).get("work"), hasSize(1));
    }
}