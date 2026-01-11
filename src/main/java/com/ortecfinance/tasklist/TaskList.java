package com.ortecfinance.tasklist;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public final class TaskList implements Runnable {
    private static final String QUIT = "quit";

    private final TaskService taskService;
    private final ConfigurableApplicationContext context;
    private final BufferedReader in;
    private final PrintWriter out;

    public static void startConsole(TaskService ts, ConfigurableApplicationContext context) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);

        new TaskList(in, out, ts, context).run();
    }

    public TaskList(BufferedReader reader, PrintWriter writer, TaskService taskService, ConfigurableApplicationContext context) {
        this.in = reader;
        this.out = writer;
        this.taskService = taskService;
        this.context = context;
    }

    public void run() {
        out.println("Welcome to TaskList! Type 'help' for available commands.");
        while (true) {
            out.print("> ");
            out.flush();
            String command;
            try {
                command = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (command.equals(QUIT)) {
                //no context if we are in a unit test
                if (context != null) {
                    SpringApplication.exit(context, () -> 0);
                }
                break;
            }
            execute(command);
        }
    }

    private void handleShow() {
        out.print(TaskFormatter.formatAllProjects(taskService.getAllProjects()));
    }

    private void handleToday() {
        out.print(TaskFormatter.formatTodaysTasks(taskService.getTasksTodaysDeadline()));
    }

    private void handleViewByDeadline() {
        out.print(TaskFormatter.formatTasksByDeadline(taskService.getTasksSortedByDeadline()));
    }

    private void handleHelp() {
        out.print(TaskFormatter.formatHelp());
    }

    private void handleAddProject(CommandParser.AddProjectCommand command) {
        try {
            taskService.createProject(command.projectName);
        } catch (IllegalArgumentException e) {
            out.println("A project with the name of " + command.projectName + " already exists.");
            out.println();
        }
    }

    private void handleAddTask(CommandParser.AddTaskCommand command) {
        try {
            taskService.createTask(command.projectName, command.taskDescription);
        } catch (IllegalArgumentException e) {
            out.println("Could not find a project with the name \"" + command.projectName + "\".");
            out.println();
        }
    }

    private void handleCheck(CommandParser.CheckCommand command) {
        try {
            taskService.markTask(command.taskId, command.done);
        } catch (IllegalArgumentException e) {
            out.println("Could not find a task with an ID of " + command.taskId + ".");
            out.println();
        }
    }

    private void handleDeadline(CommandParser.DeadlineCommand command) {
        try {
            taskService.setTaskDeadline(command.taskId, command.deadline);
        } catch (IllegalArgumentException e) {
            out.println("Could not find a task with an ID of " + command.taskId + ".");
            out.println();
        }
    }

    private void handleError(CommandParser.ErrorCommand command) {
        out.println(command.message);
    }

    private void handleUnknown(CommandParser.UnknownCommand command) {
        out.println("I don't know what the command \"" + command.command + "\" is.");
        out.println();
    }

    private void execute(String commandLine) {
        CommandParser.Command command = CommandParser.parse(commandLine);
        if (command instanceof CommandParser.ShowCommand) {
            handleShow();
        } else if (command instanceof CommandParser.TodayCommand) {
            handleToday();
        } else if (command instanceof CommandParser.ViewByDeadlineCommand) {
            handleViewByDeadline();
        } else if (command instanceof CommandParser.HelpCommand) {
            handleHelp();
        } else if (command instanceof CommandParser.AddProjectCommand) {
            handleAddProject((CommandParser.AddProjectCommand) command);
        } else if (command instanceof CommandParser.AddTaskCommand) {
            handleAddTask((CommandParser.AddTaskCommand) command);
        } else if (command instanceof CommandParser.CheckCommand) {
            handleCheck((CommandParser.CheckCommand) command);
        } else if (command instanceof CommandParser.DeadlineCommand) {
            handleDeadline((CommandParser.DeadlineCommand) command);
        } else if (command instanceof CommandParser.ErrorCommand) {
            handleError((CommandParser.ErrorCommand) command);
        } else if (command instanceof CommandParser.UnknownCommand) {
            handleUnknown((CommandParser.UnknownCommand) command);
        }
    }

}
