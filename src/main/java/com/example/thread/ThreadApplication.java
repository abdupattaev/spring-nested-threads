package com.example.thread;

import com.example.thread.service.TaskService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class ThreadApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(ThreadApplication.class, args);

        System.out.println("---------------------------------------Hello Spring!!!---------------------------------------");

        TaskService taskService = context.getBean(TaskService.class);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            CompletableFuture<String> future = taskService.performTask("Task" + i,i);
            futures.add(future);
        }

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            combinedFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        futures.forEach(future -> {
            future.handle((result, ex) -> {
                if (ex == null) {
                    System.out.println("Task result: " + result);
                } else {
                    System.out.println("Task error: " + ex.getMessage());
                }
                return null;
            });
        });




    }


}
