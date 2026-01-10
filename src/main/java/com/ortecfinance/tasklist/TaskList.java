package com.ortecfinance.tasklist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TaskList implements Runnable {
    private static final String QUIT = "quit";

    private final Map<String, List<Task>> tasks = new LinkedHashMap<>();
    private final BufferedReader in;
    private final PrintWriter out;

    private long lastId = 0;

    public static void startConsole() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        new TaskList(in, out).run();
    }

    public TaskList(BufferedReader reader, PrintWriter writer) {
        this.in = reader;
        this.out = writer;
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
                add(commandRest[1]);
                break;
            case "check":
                check(commandRest[1]);
                break;
            case "uncheck":
                uncheck(commandRest[1]);
                break;
            case "help":
                help();
                break;
            case "deadline":
                deadline(commandRest[1]);
                break;
            case "today":
                today();
                break;
            default:
                error(command);
                break;
        }
    }

    private void show() {
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            out.println(project.getKey());
            for (Task task : project.getValue()) {
                out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription());
            }
            out.println();
        }
    }

    private void today(){
        LocalDate todaysDate = LocalDate.now();

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
                out.println(dateToString(todaysDate));
                out.println(project.getKey());
                for (Task task : todaysTasks) {
                    out.printf("    [%c] %d: %s%n",
                            (task.isDone() ? 'x' : ' '),
                            task.getId(),
                            task.getDescription());
                }
                out.println();
            }
        }
    }

    private void add(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);
        String subcommand = subcommandRest[0];
        if (subcommand.equals("project")) {
            addProject(subcommandRest[1]);
        } else if (subcommand.equals("task")) {
            String[] projectTask = subcommandRest[1].split(" ", 2);
            addTask(projectTask[0], projectTask[1]);
        }
    }

    private void addProject(String name) {
        tasks.put(name, new ArrayList<Task>());
    }

    private void addTask(String project, String description) {
        List<Task> projectTasks = tasks.get(project);
        if (projectTasks == null) {
            out.printf("Could not find a project with the name \"%s\".", project);
            out.println();
            return;
        }
        projectTasks.add(new Task(nextId(), description, false));
    }

    private void check(String idString) {
        setDone(idString, true);
    }

    private void uncheck(String idString) {
        setDone(idString, false);
    }

    private void setDone(String idString, boolean done) {
        int id = Integer.parseInt(idString);
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
                if (task.getId() == id) {
                    task.setDone(done);
                    return;
                }
            }
        }
        out.printf("Could not find a task with an ID of %d.", id);
        out.println();
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
        String[] arguments = commandLine.split(" ", 2);
        int taskID = Integer.parseInt(arguments[0]);
        LocalDate date = parseDate(arguments[1]);

        //search through tasks to find a corresponding task id, if found, set a deadline for it
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
                if (task.getId() == taskID) {
                    task.setDeadline(date);
                    return;
                }
            }
        }
        out.printf("Could not find a task with an ID of %d.", taskID);
        out.println();
    }

    private void help() {
        out.println("Commands:");
        out.println("  show");
        out.println("  today");
        out.println("  add project <project name>");
        out.println("  add task <project name> <task description>");
        out.println("  check <task ID>");
        out.println("  uncheck <task ID>");
        out.println("  deadline <task ID> <date>");
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
