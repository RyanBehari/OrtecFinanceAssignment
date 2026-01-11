package com.ortecfinance.tasklist;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TaskFormatter {

    private static final String LINE_SEP = System.lineSeparator();

    //Returns date back to string format
    public static String formatDate(LocalDate date) {
        return String.format("%02d-%02d-%04d",
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear());
    }

    //returns task as correct display format
    public static String formatTask(Task task) {
        return String.format("    [%c] %d: %s",
                (task.isDone() ? 'x' : ' '),
                task.getId(),
                task.getDescription());
    }

    public static String formatAllProjects(Map<String, List<Task>> projects) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, List<Task>> project : projects.entrySet()) {
            result.append(project.getKey()).append(LINE_SEP);
            for (Task task : project.getValue()) {
                result.append(formatTask(task)).append(LINE_SEP);
            }
            result.append(LINE_SEP);
        }
        return result.toString();
    }

    private static String formatProjectsWithIndent(Map<String, List<Task>> projects) {
        StringBuilder result = new StringBuilder();
        //loop through projects
        for (Map.Entry<String, List<Task>> projectEntry : projects.entrySet()) {
            //add projects name with two spaces and a :
            result.append("  ").append(projectEntry.getKey()).append(":").append(LINE_SEP);

            //add each task
            for (Task task : projectEntry.getValue()) {
                result.append(formatTask(task)).append(LINE_SEP);
            }
        }
        return result.toString();
    }

    public static String formatTasksByDeadline(Map<LocalDate, Map<String, List<Task>>> tasksByDeadline) {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<LocalDate, Map<String, List<Task>>> deadlineEntry : tasksByDeadline.entrySet()) {
            //Check if we are in the no deadline section
            if (deadlineEntry.getKey() == null) {
                result.append("No deadline:").append(LINE_SEP);
                result.append(formatProjectsWithIndent(deadlineEntry.getValue()));
            } else {
                //add deadline at top then format the projects with this deadline
                result.append(formatDate(deadlineEntry.getKey())).append(":").append(LINE_SEP);
                result.append(formatProjectsWithIndent(deadlineEntry.getValue()));
            }
            result.append(LINE_SEP);
        }

        return result.toString();
    }

    public static String formatTodaysTasks(Map<String, List<Task>> projects) {
        StringBuilder result = new StringBuilder();
        //add todays date at top
        result.append(formatDate(LocalDate.now())).append(LINE_SEP);
        //loop through projects with tasks that have a deadline for today
        for (Map.Entry<String, List<Task>> project : projects.entrySet()) {
            //project name added
            result.append(project.getKey()).append(LINE_SEP);
            for (Task task : project.getValue()) {
                //add the task from this project
                result.append(formatTask(task)).append(LINE_SEP);
            }
            //empty line after every project
            result.append(LINE_SEP);
        }
        return result.toString();

    }

    //Help menu display
    public static String formatHelp() {
        return "Commands:" + LINE_SEP +
                "  show" + LINE_SEP +
                "  today" + LINE_SEP +
                "  view-by-deadline" + LINE_SEP +
                "  add project <project name>" + LINE_SEP +
                "  add task <project name> <task description>" + LINE_SEP +
                "  check <task ID>" + LINE_SEP +
                "  uncheck <task ID>" + LINE_SEP +
                "  deadline <task ID> <date>" + LINE_SEP +
                "  quit" + LINE_SEP + LINE_SEP;
    }


}