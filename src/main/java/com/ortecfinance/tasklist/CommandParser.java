package com.ortecfinance.tasklist;

import java.time.LocalDate;

public class CommandParser {

    //subfunction to parse the date given dd-mm-yyyy format
    private static LocalDate parseDate(String dateString) {
        String[] parts = dateString.split("-");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        return LocalDate.of(year, month, day);
    }

    public static Command parse(String commandLine) {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return new UnknownCommand("");
        }

        String[] commandRest = commandLine.split(" ", 2);
        String command = commandRest[0];

        switch (command) {
            case "show":
                return new ShowCommand();
            case "add":
                if (commandRest.length < 2) {
                    return new ErrorCommand("Please specify what to add (project/task).");
                }
                return parseAddCommand(commandRest[1]);
            case "check":
                if (commandRest.length < 2) {
                    return new ErrorCommand("Please provide a task ID");
                }
                return parseCheckCommand(commandRest[1], true);
            case "uncheck":
                if (commandRest.length < 2) {
                    return new ErrorCommand("Please provide a task ID");
                }
                return parseCheckCommand(commandRest[1], false);
            case "deadline":
                if (commandRest.length < 2) {
                    return new ErrorCommand("Please provide a task ID");
                }
                return parseDeadlineCommand(commandRest[1]);
            case "today":
                return new TodayCommand();
            case "view-by-deadline":
                return new ViewByDeadlineCommand();
            case "help":
                return new HelpCommand();
            default:
                return new UnknownCommand(command);
        }
    }

    //Command to add projects/tasks
    private static Command parseAddCommand(String arguments) {
        String[] subcommandRest = arguments.split(" ", 2);
        String subcommand = subcommandRest[0];

        if (subcommand.equals("project")) {
            if (subcommandRest.length < 2) {
                return new ErrorCommand("Please provide a project name.");
            }
            return new AddProjectCommand(subcommandRest[1]);
        }
        else if (subcommand.equals("task")) {
            if (subcommandRest.length < 2) {
                return new ErrorCommand("Please provide a project name and task description.");
            }
            String[] projectTask = subcommandRest[1].split(" ", 2);
            if (projectTask.length < 2) {
                return new ErrorCommand("Please provide both the project name and task description.");
            }
            return new AddTaskCommand(projectTask[0], projectTask[1]);
        }
        else {
            return new ErrorCommand("Unknown add subcommand. Use 'add project' or 'add task'.");
        }
    }

    // Command for (un)checking a given task
    private static Command parseCheckCommand(String idString, boolean done) {
        try {
            long id = Long.parseLong(idString);
            return new CheckCommand(id, done);
        } catch (NumberFormatException e) {
            return new ErrorCommand("Invalid task ID: " + idString);
        }
    }

    // Command for adding a deadline to a task
    private static Command parseDeadlineCommand(String arguments) {
        try {
            String[] parts = arguments.split(" ", 2);
            if (parts.length < 2) {
                return new ErrorCommand("Please provide both an ID and a deadline.");
            }
            long taskId = Long.parseLong(parts[0]);
            LocalDate deadline = parseDate(parts[1]);
            return new DeadlineCommand(taskId, deadline);
        } catch (NumberFormatException e) {
            return new ErrorCommand("Invalid task ID");
        } catch (Exception e) {
            return new ErrorCommand("Please use the format: deadline <task ID> <dd-mm-yyyy>");
        }
    }

    /* CLASSES FOR ALL COMMANDS */
    public interface Command {}

    public static class ShowCommand implements Command {}

    public static class TodayCommand implements Command {}

    public static class ViewByDeadlineCommand implements Command {}

    public static class HelpCommand implements Command {}

    public static class AddProjectCommand implements Command {
        public final String projectName;
        public AddProjectCommand(String projectName) { this.projectName = projectName; }
    }

    public static class AddTaskCommand implements Command {
        public final String projectName;
        public final String taskDescription;
        public AddTaskCommand(String projectName, String taskDescription) {
            this.projectName = projectName;
            this.taskDescription = taskDescription;
        }
    }

    public static class CheckCommand implements Command {
        public final long taskId;
        public final boolean done;
        public CheckCommand(long taskId, boolean done) {
            this.taskId = taskId;
            this.done = done;
        }
    }

    public static class DeadlineCommand implements Command {
        public final long taskId;
        public final LocalDate deadline;
        public DeadlineCommand(long taskId, LocalDate deadline) {
            this.taskId = taskId;
            this.deadline = deadline;
        }
    }

    public static class ErrorCommand implements Command {
        public final String message;
        public ErrorCommand(String message) { this.message = message; }
    }

    public static class UnknownCommand implements Command {
        public final String command;
        public UnknownCommand(String command) { this.command = command; }
    }
}
