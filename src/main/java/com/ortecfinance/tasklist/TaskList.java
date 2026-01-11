package com.ortecfinance.tasklist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

public final class TaskList implements Runnable {
    private static final String QUIT = "quit";

    private final TaskService taskService;
    private final BufferedReader in;
    private final PrintWriter out;

    private long lastId = 0;

    public static void startConsole() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);

        TaskStorage taskStorage = new TaskStorage();
        TaskService ts = new TaskService(taskStorage);
        new TaskList(in, out, ts).run();
    }

    public TaskList(BufferedReader reader, PrintWriter writer, TaskService taskService) {
        this.in = reader;
        this.out = writer;
        this.taskService = taskService;
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
                break;
            }
            execute(command);
        }
    }

    private void execute(String commandLine) {
        String[] commandRest = commandLine.split(" ", 2);
        String command = commandRest[0];
        switch (command) {
            case "show":
                show();
                break;
            case "add":
                if (commandRest.length < 2) {
                    out.println("Please specify what to add (project/task).");
                    break;
                }
                add(commandRest[1]);
                break;
            case "check":
                if (commandRest.length < 2) {
                    out.println("Please provide a task ID");
                    break;
                }
                check(commandRest[1]);
                break;
            case "uncheck":
                if (commandRest.length < 2) {
                    out.println("Please provide a task ID");
                    break;
                }
                uncheck(commandRest[1]);
                break;
            case "help":
                help();
                break;
            case "deadline":
                if (commandRest.length < 2) {
                    out.println("Please provide a task ID");
                    break;
                }
                deadline(commandRest[1]);
                break;
            case "today":
                today();
                break;
            case "view-by-deadline":
                viewByDeadline();
                break;
            default:
                error(command);
                break;
        }
    }

    private void show() {
        for (Map.Entry<String, List<Task>> project : taskService.getAllProjects().entrySet()) {
            out.println(project.getKey());
            for (Task task : project.getValue()) {
                out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription());
            }
            out.println();
        }
    }

    private void today(){
        LocalDate todaysDate = LocalDate.now();
        out.println(dateToString(todaysDate));
        for (Map.Entry<String, List<Task>> project : taskService.getTasksTodaysDeadline().entrySet()) {
            out.println(project.getKey());
            for (Task task : project.getValue()) {
                out.printf("    [%c] %d: %s%n",
                        (task.isDone() ? 'x' : ' '),
                        task.getId(),
                        task.getDescription());
            }
            out.println();
        }
    }


    private void add(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);
        String subcommand = subcommandRest[0];
        if (subcommand.equals("project")) {
            if (subcommandRest.length < 2) {
                out.println("Please provide a project name.");
                return;
            }
            addProject(subcommandRest[1]);
        } else if (subcommand.equals("task")) {
            if (subcommandRest.length < 2) {
                out.println("Please provide a project name and task description.");
                return;
            }
            String[] projectTask = subcommandRest[1].split(" ", 2);
            if (projectTask.length < 2) {
                out.println("Please provide both the project name and task description.");
                return;
            }
            addTask(projectTask[0], projectTask[1]);
        }
    }

    private void addProject(String name) {
        try {
            taskService.createProject(name);
        } catch (IllegalArgumentException e) {
            out.printf("A project with the name of %s already exists.", name);
            out.println();
        }
    }

    private void addTask(String project, String description) {
        try {
            taskService.createTask(project, description);
        } catch (IllegalArgumentException e) {
            out.printf("Could not find a project with the name \"%s\".", project);
            out.println();
        }
    }

    private void check(String idString) {
        setDone(idString, true);
    }

    private void uncheck(String idString) {
        setDone(idString, false);
    }

    private void setDone(String idString, boolean done) {

        try {
            long id = Long.parseLong(idString);
            taskService.markTask(id, done);
        } catch (NumberFormatException e) {
            out.printf("Invalid task ID: %s.", idString);
            out.println();
        } catch (IllegalArgumentException e) {
            out.printf("Could not find a task with an ID of %s.", idString);
            out.println();
        }
    }

    //subfunction to parse the date given dd-mm-yyyy format
    private LocalDate parseDate(String dateString) {
        String[] parts = dateString.split("-");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        return LocalDate.of(year, month, day);
    }

    private String dateToString(LocalDate date) {
        //first two digits for day then -,
        //second two digits for month then -
        //last 4 digits for year
        return String.format("%02d-%02d-%04d",
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear());
    }

    //function to set the deadline
    private void deadline(String commandLine){
        try{
            String[] arguments = commandLine.split(" ", 2);
            if (arguments.length < 2) {
                out.println("Please provide both an ID and a deadline.");
                return;
            }
            long taskID = Long.parseLong(arguments[0]);
            LocalDate date = parseDate(arguments[1]);
            taskService.setTaskDeadline(taskID, date);
        }
        catch (NumberFormatException e) {
            out.println("Invalid task ID");
            out.println();
        }
        catch (IllegalArgumentException e) {
            out.println(e.getMessage());
            out.println();
        }
        catch (Exception e) {
            out.println("Please use the format: deadline <task ID> <dd-mm-yyyy>.");
            out.println();
        }

    }

    private void viewByDeadline() {

        Map<LocalDate, Map<String, List<Task>>> tasksByDeadline = taskService.getTasksSortedByDeadline();
        for (Map.Entry<LocalDate, Map<String, List<Task>>> deadlineEntry : tasksByDeadline.entrySet()) {
            if (deadlineEntry.getKey() == null) {
                // Tasks without deadline
                out.println("No deadline:");
                for (Map.Entry<String, List<Task>> projectEntry : deadlineEntry.getValue().entrySet()) {
                    out.println("  " + projectEntry.getKey() + ":");
                    for (Task task : projectEntry.getValue()) {
                        out.printf("    [%c] %d: %s%n",
                                (task.isDone() ? 'x' : ' '),
                                task.getId(),
                                task.getDescription());
                    }
                }
            } else {
                // Tasks with deadline
                out.println(dateToString(deadlineEntry.getKey()) + ":");
                for (Map.Entry<String, List<Task>> projectEntry : deadlineEntry.getValue().entrySet()) {
                    out.println("  " + projectEntry.getKey() + ":");
                    for (Task task : projectEntry.getValue()) {
                        out.printf("    [%c] %d: %s%n",
                                (task.isDone() ? 'x' : ' '),
                                task.getId(),
                                task.getDescription());
                    }
                }
            }
            out.println();
        }
    }

    private void help() {
        out.println("Commands:");
        out.println("  show");
        out.println("  today");
        out.println("  view-by-deadline");
        out.println("  add project <project name>");
        out.println("  add task <project name> <task description>");
        out.println("  check <task ID>");
        out.println("  uncheck <task ID>");
        out.println("  deadline <task ID> <date>");
        out.println("  quit");
        out.println();
    }

    private void error(String command) {
        out.printf("I don't know what the command \"%s\" is.", command);
        out.println();
    }

    private long nextId() {
        return ++lastId;
    }
}
