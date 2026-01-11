package com.ortecfinance.tasklist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class TaskStorageTest {

    private TaskStorage taskStorage;

    @BeforeEach
    public void setup() {
        taskStorage = new TaskStorage();
    }

    @Test
    void testAddProject() {
        taskStorage.addProject("secrets");
        assertThat(taskStorage.projectExists("secrets"), is(true));
    }

    @Test
    void testAddDuplicateProjectThrowsException() {
        taskStorage.addProject("secrets");
        assertThrows(IllegalArgumentException.class, () -> {
            taskStorage.addProject("secrets");
        });
    }

    @Test
    void testProjectExistsReturnsFalse() {
        assertThat(taskStorage.projectExists("nonexistent"), is(false));
    }

    @Test
    void testAddTask() {
        taskStorage.addProject("training");
        taskStorage.addTask("training", "Learn Java");

        Map<String, List<Task>> projects = taskStorage.getAllProjects();
        assertThat(projects.get("training"), hasSize(1));
        assertThat(projects.get("training").get(0).getDescription(), is("Learn Java"));
    }

    @Test
    void testAddTaskToNonExistentProjectThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            taskStorage.addTask("nonexistent", "Some task");
        });
    }

    @Test
    void testTaskIdsAreUnique() {
        taskStorage.addProject("project1");
        taskStorage.addProject("project2");
        taskStorage.addTask("project1", "Task 1");
        taskStorage.addTask("project2", "Task 2");
        taskStorage.addTask("project1", "Task 3");

        Map<String, List<Task>> projects = taskStorage.getAllProjects();
        assertThat(projects.get("project1").get(0).getId(), is(1L));
        assertThat(projects.get("project2").get(0).getId(), is(2L));
        assertThat(projects.get("project1").get(1).getId(), is(3L));
    }

    @Test
    void testReturnTaskByID() {
        taskStorage.addProject("secrets");
        taskStorage.addTask("secrets", "Eat more donuts.");

        Task task = taskStorage.returnTaskByID(1);
        assertThat(task, is(notNullValue()));
        assertThat(task.getDescription(), is("Eat more donuts."));
    }

    @Test
    void testReturnTaskByIDReturnsNullForNonExistent() {
        Task task = taskStorage.returnTaskByID(999);
        assertThat(task, is(nullValue()));
    }

    @Test
    void testGetAllProjectsEmpty() {
        Map<String, List<Task>> projects = taskStorage.getAllProjects();
        assertThat(projects.isEmpty(), is(true));
    }

    @Test
    void testGetAllProjectsReturnsCopy() {
        taskStorage.addProject("secrets");
        taskStorage.addTask("secrets", "Task 1");

        Map<String, List<Task>> projects1 = taskStorage.getAllProjects();
        Map<String, List<Task>> projects2 = taskStorage.getAllProjects();

        assertThat(projects1, is(not(sameInstance(projects2))));
    }

    @Test
    void testGetTasksTodaysDeadline() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        taskStorage.addProject("work");
        taskStorage.addTask("work", "Task 1");
        taskStorage.addTask("work", "Task 2");
        taskStorage.addTask("work", "Task 3");

        taskStorage.returnTaskByID(1).setDeadline(today);
        taskStorage.returnTaskByID(2).setDeadline(tomorrow);
        taskStorage.returnTaskByID(3).setDeadline(today);

        Map<String, List<Task>> todaysTasks = taskStorage.getTasksTodaysDeadline();
        assertThat(todaysTasks.get("work"), hasSize(2));
        assertThat(todaysTasks.get("work").get(0).getId(), is(1L));
        assertThat(todaysTasks.get("work").get(1).getId(), is(3L));
    }

    @Test
    void testGetTasksTodaysDeadlineExcludesProjectsWithNoTasksToday() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        taskStorage.addProject("work");
        taskStorage.addTask("work", "Task 1");
        taskStorage.returnTaskByID(1).setDeadline(tomorrow);

        Map<String, List<Task>> todaysTasks = taskStorage.getTasksTodaysDeadline();
        assertThat(todaysTasks.isEmpty(), is(true));
    }

    @Test
    void testGetTasksSortedByDeadline() {
        LocalDate date1 = LocalDate.of(2021, 11, 11);
        LocalDate date2 = LocalDate.of(2021, 11, 13);

        taskStorage.addProject("Secrets");
        taskStorage.addProject("Training");
        taskStorage.addTask("Secrets", "Task 1");
        taskStorage.addTask("Training", "Task 2");
        taskStorage.addTask("Training", "Task 3");
        taskStorage.addTask("Training", "Task 4");

        taskStorage.returnTaskByID(1).setDeadline(date1);
        taskStorage.returnTaskByID(3).setDeadline(date2);
        taskStorage.returnTaskByID(4).setDeadline(date1);

        Map<LocalDate, Map<String, List<Task>>> sorted = taskStorage.getTasksSortedByDeadline();

        assertThat(sorted.get(date1).get("Secrets"), hasSize(1));
        assertThat(sorted.get(date1).get("Training"), hasSize(1));
        assertThat(sorted.get(date2).get("Training"), hasSize(1));
        assertThat(sorted.get(null).get("Training"), hasSize(1));
    }

    @Test
    void testGetTasksSortedByDeadlineIsChronological() {
        LocalDate laterDate = LocalDate.of(2026, 12, 31);
        LocalDate earlierDate = LocalDate.of(2026, 1, 1);

        taskStorage.addProject("work");
        taskStorage.addTask("work", "Task 1");
        taskStorage.addTask("work", "Task 2");

        taskStorage.returnTaskByID(1).setDeadline(laterDate);
        taskStorage.returnTaskByID(2).setDeadline(earlierDate);

        Map<LocalDate, Map<String, List<Task>>> sorted = taskStorage.getTasksSortedByDeadline();

        List<LocalDate> keys = sorted.keySet().stream()
                .filter(date -> date != null)
                .toList();

        assertThat(keys.get(0), is(earlierDate));
        assertThat(keys.get(1), is(laterDate));
    }

    @Test
    void testGetTasksSortedByDeadlineNoDeadlineComesLast() {
        LocalDate someDate = LocalDate.of(2026, 1, 15);

        taskStorage.addProject("work");
        taskStorage.addTask("work", "Task 1");
        taskStorage.addTask("work", "Task 2");

        taskStorage.returnTaskByID(1).setDeadline(someDate);

        Map<LocalDate, Map<String, List<Task>>> sorted =
                taskStorage.getTasksSortedByDeadline();

        List<LocalDate> keys = new ArrayList<>(sorted.keySet());
        assertThat(keys.get(keys.size() - 1), is(nullValue()));
    }


    @Test
    void testMultipleProjectsMultipleTasks() {
        taskStorage.addProject("secrets");
        taskStorage.addProject("training");
        taskStorage.addProject("shopping");

        taskStorage.addTask("secrets", "Task 1");
        taskStorage.addTask("secrets", "Task 2");
        taskStorage.addTask("training", "Task 3");
        taskStorage.addTask("shopping", "Task 4");

        Map<String, List<Task>> projects = taskStorage.getAllProjects();

        assertThat(projects.size(), is(3));
        assertThat(projects.get("secrets"), hasSize(2));
        assertThat(projects.get("training"), hasSize(1));
        assertThat(projects.get("shopping"), hasSize(1));
    }
}