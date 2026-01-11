package com.ortecfinance.tasklist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TaskListApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(TaskListApplication.class, args);

        TaskService sharedService = context.getBean(TaskService.class);

        new Thread(() -> {
            System.out.println("Starting console application...");
            TaskList.startConsole(sharedService);
        }).start();

        System.out.println("REST API running at http://localhost:8080/projects");
    }

    @Bean
    public TaskStorage taskStorage() {
        return new TaskStorage();
    }

    @Bean
    public TaskService taskService(TaskStorage taskStorage) {
        return new TaskService(taskStorage);
    }

}
