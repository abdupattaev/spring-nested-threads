//package com.example.thread.util;
//
//import com.google.gson.*;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.*;
//import java.lang.reflect.Type;
//import java.nio.file.*;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class TaskStatusManager {
//    private final Gson gson = new Gson();
//    private final Path statusFilePath = Paths.get("taskStatus.json");
//    private Map<String, Map<Long, Status>> taskThreadStatusMap = new ConcurrentHashMap<>();
//
//    public void loadStatusFromFile() throws IOException {
//        if (Files.exists(statusFilePath)) {
//            String json = new String(Files.readAllBytes(statusFilePath));
//            Type type = new TypeToken<Map<String, Map<Long, Status>>>(){}.getType();
//            taskThreadStatusMap = gson.fromJson(json, type);
//        }
//    }
//
//    public void saveStatusToFile() throws IOException {
//        String json = gson.toJson(taskThreadStatusMap);
//        Files.write(statusFilePath, json.getBytes());
//    }
//
//    public void updateStatus(ApiTask task, Long threadId, Status status) {
//        Map<Long, Status> threadStatusMap = taskThreadStatusMap.get(task.toString());
//        if (threadStatusMap == null) {
//            threadStatusMap = new ConcurrentHashMap<>();
//            taskThreadStatusMap.put(task.toString(), threadStatusMap);
//        }
//        threadStatusMap.put(threadId, status);
//    }
//
//    public Status getStatus(ApiTask task, Long threadId) {
//        Map<Long, Status> threadStatusMap = taskThreadStatusMap.get(task.toString());
//        return threadStatusMap != null ? threadStatusMap.get(threadId) : Status.NOT_STARTED;
//    }
//}
//
