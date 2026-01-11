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
        String result = "";
        for (Map.Entry<String, List<Task>> project : projects.entrySet()) {
            result += project.getKey() + LINE_SEP;
            for (Task task : project.getValue()) {
                result += formatTask(task) + LINE_SEP;
            }
            result += LINE_SEP;
        }
        return result;
    }

    private static String formatProjectsWithIndent(Map<String, List<Task>> projects) {
        String result = "";
        //loop through projects
        for (Map.Entry<String, List<Task>> projectEntry : projects.entrySet()) {
            //add projects name with two spaces and a :
            result += "  " + projectEntry.getKey() + ":" + LINE_SEP;

            //add each task
            for (Task task : projectEntry.getValue()) {
                result += formatTask(task) + LINE_SEP;
            }
        }
        return result;
    }

    public static String formatTasksByDeadline(Map<LocalDate, Map<String, List<Task>>> tasksByDeadline) {
        String result = "";

        for (Map.Entry<LocalDate, Map<String, List<Task>>> deadlineEntry : tasksByDeadline.entrySet()) {
            //Check if we are in the no deadline section
            if (deadlineEntry.getKey() == null) {
                result += "No deadline:" + LINE_SEP;
                result += formatProjectsWithIndent(deadlineEntry.getValue());
            } else {
                //add deadline at top then format the projects with this deadline
                result += formatDate(deadlineEntry.getKey()) + ":" + LINE_SEP;
                result += formatProjectsWithIndent(deadlineEntry.getValue());
            }
            result += LINE_SEP;
        }

        return result;
    }

    public static String formatTodaysTasks(Map<String, List<Task>> projects) {
        String result = "";
        //add todays date at top
        result += formatDate(LocalDate.now()) + LINE_SEP;
        //loop through projects with tasks that have a deadline for today
        for (Map.Entry<String, List<Task>> project : projects.entrySet()) {
            //project name added
            result += project.getKey() + LINE_SEP;
            for (Task task : project.getValue()) {
                //add the task from this project
                result += formatTask(task) + LINE_SEP;
            }
            //empty line after every project
            result += LINE_SEP;
        }
        return result;

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