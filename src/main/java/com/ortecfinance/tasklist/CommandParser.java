package com.ortecfinance.tasklist;

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
}
