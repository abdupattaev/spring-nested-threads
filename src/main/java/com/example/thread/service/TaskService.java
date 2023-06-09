package com.example.thread.service;

import com.example.thread.vo.Status;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
public class TaskService {

    private final ConcurrentMap<String, ConcurrentMap<String, Status>> statusMap;

    public TaskService() {
        statusMap = new ConcurrentHashMap<>();
    }

    @Async("taskExecutor")
    public CompletableFuture<String> performTask(String taskName, int i) {
        ConcurrentMap<String, Status> nestedTaskStatusMap = new ConcurrentHashMap<>();
        statusMap.put(taskName, nestedTaskStatusMap);

        return CompletableFuture.supplyAsync(() -> {
            nestedTaskStatusMap.put(taskName, Status.RUNNING);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            String threadName = Thread.currentThread().getName();
            nestedTaskStatusMap.put(taskName, Status.SUCCESS);
            return "Task completed by " + threadName + " Sequence: " + i;
        }).thenCompose(result->{
            CompletableFuture<String> future1 = performNestedTask("task1",result + " for task 1");
            CompletableFuture<String> future2 = performNestedTask("task2",result + " for task 2");
            CompletableFuture<String> future3 = performNestedTask("task3",result + " for task 3");

            return CompletableFuture.allOf(future1, future2, future3)
                    .thenApply(v -> {
                        String result1 = future1.join();
                        String result2 = future2.join();
                        String result3 = future3.join();

                        return result1 + ", " + result2 + ", " + result3;
                    });
        });
    }


    @Async("nestedTaskExecutor")
    public CompletableFuture<String> performNestedTask(String taskName, String resultFromPreviousTask) {
        return CompletableFuture.supplyAsync(() -> {
            synchronized (statusMap) {
                ConcurrentMap<String, Status> nestedTaskStatusMap = statusMap.get(taskName);
                String nestedTaskName = taskName + ":NestedTask";
                nestedTaskStatusMap.put(nestedTaskName, Status.RUNNING);
            }

            // Another long-running task
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            String threadName = Thread.currentThread().getName();

            synchronized (statusMap) {
                ConcurrentMap<String, Status> nestedTaskStatusMap = statusMap.get(taskName);
                String nestedTaskName = taskName + ":NestedTask";
                nestedTaskStatusMap.put(nestedTaskName, Status.SUCCESS);
            }

            return resultFromPreviousTask + ", Nested task result and Nested Thread Name: " + threadName;
        }).exceptionally(ex -> {
            synchronized (statusMap) {
                ConcurrentMap<String, Status> nestedTaskStatusMap = statusMap.get(taskName);
                String nestedTaskName = taskName + ":NestedTask";
                nestedTaskStatusMap.put(nestedTaskName, Status.FAIL_FOR_RETRY);
            }
            return resultFromPreviousTask + ", Nested task failed after retries";
        });
    }


//    @Async("nestedTaskExecutor")
//    public CompletableFuture<String> performNestedTask(String taskName, String resultFromPreviousTask) {
////        ConcurrentMap<String, Status> nestedTaskStatusMap = statusMap.get(taskName);
////        String nestedTaskName = taskName + ":NestedTask";
//
//        return CompletableFuture.supplyAsync(() -> {
//            synchronized (statusMap) {
//                ConcurrentMap<String, Status> nestedTaskStatusMap = statusMap.get(taskName);
//                String nestedTaskName = taskName + ":NestedTask";
//                nestedTaskStatusMap.put(nestedTaskName, Status.RUNNING);
//            }
////            nestedTaskStatusMap.put(nestedTaskName, Status.RUNNING);
//            // Simulate another long-running task
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//            String threadName = Thread.currentThread().getName();
//
////            nestedTaskStatusMap.put(nestedTaskName, Status.SUCCESS);
//            return resultFromPreviousTask + ", Nested task result and Nested Thread Name: " + threadName;
//        }).exceptionally(ex -> {
////            nestedTaskStatusMap.put(nestedTaskName, Status.FAIL_FOR_RETRY);
//            return resultFromPreviousTask + ", Nested task failed after retries";
//        });
//    }

}
