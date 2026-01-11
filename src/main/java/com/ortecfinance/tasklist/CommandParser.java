package com.ortecfinance.tasklist;

import java.time.LocalDate;

public class CommandParser {



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
