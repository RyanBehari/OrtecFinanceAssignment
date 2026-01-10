package com.ortecfinance.tasklist;

import java.time.LocalDate;
import java.util.*;

public class TaskStorage {
    private final Map<String, List<Task>> tasks = new LinkedHashMap<>();
    private long lastId = 0;

    private void addProject(String name) {
        if (tasks.containsKey(name)) {
            throw new IllegalArgumentException("Project " + name + " already exists: ");
        }
        tasks.put(name, new ArrayList<Task>());
    }

    private long nextId() {
        return ++lastId;
    }

    private void addTask(String project, String description) {
        List<Task> projectTasks = tasks.get(project);
        if (projectTasks == null) {
            throw new IllegalArgumentException("Project not found: " + project);
        }
        projectTasks.add(new Task(nextId(), description, false));
    }

    public Task returnTaskByID(long ID) {

        //go through projects and check tasks within, return task if we find a matching id
        for (List<Task> projectTasks : tasks.values()) {
            for (Task task : projectTasks) {
                if (task.getId() == ID) {
                    return task;
                }
            }
        }
        return null;
    }

    public Map<String, List<Task>> getAllProjects() {
        //Since we dont want to change anything
        Map<String, List<Task>> copyOfProjects = new LinkedHashMap<>();
        for (Map.Entry<String, List<Task>> entry : tasks.entrySet()) {
            copyOfProjects.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copyOfProjects;
    }

    public Map<String, List<Task>> getTasksTodaysDeadline() {
        LocalDate todaysDate = LocalDate.now();
        Map<String, List<Task>> resultTasks = new LinkedHashMap<>();
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            //collect all the tasks that have a deadline for today
            List<Task> todaysTasks = new ArrayList<>();
            for (Task task : project.getValue()) {
                if (task.getDeadline() != null && task.getDeadline().equals(todaysDate)) {
                    todaysTasks.add(task);
                }
            }

            //check if there even are any tasks with deadlines for today
            if (!todaysTasks.isEmpty()) {
                resultTasks.put(project.getKey(), todaysTasks);
            }
        }
        return resultTasks;
    }

    public Map<LocalDate, Map<String, List<Task>>> getTasksSortedByDeadline(){
        //TreeMap to automatically sort added tasks chronologically
        Map<LocalDate, Map<String, List<Task>>> tasksDeadlineSorted = new TreeMap<>();
        //LinkedHashMap for guarranteed reproducability
        Map<String, List<Task>> tasksWithoutDeadline = new LinkedHashMap<>();

        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            String projName = project.getKey();
            for (Task task : project.getValue()) {
                //Add to the correct mapping depending on if tasks has a set deadline or not
                if (task.getDeadline() != null) {
                    tasksDeadlineSorted
                            .computeIfAbsent(task.getDeadline(), k -> new LinkedHashMap<>())
                            .computeIfAbsent(projName, k -> new ArrayList<>())
                            .add(task);
                } else {
                    tasksWithoutDeadline
                            .computeIfAbsent(projName, k -> new ArrayList<>())
                            .add(task);
                }
            }
        }

        //add tasks without a deadline at the end
        if (!tasksWithoutDeadline.isEmpty()) {
            tasksDeadlineSorted.put(null, tasksWithoutDeadline);
        }

        return tasksDeadlineSorted;

    }

}
