package com.tms.assignment_jsb.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tms.assignment_jsb.entity.Task;
import com.tms.assignment_jsb.service.TaskService;
import com.tms.assignment_jsb.service.UserService;

@Controller
public class TaskContorller {
 
  @Autowired
  private TaskService taskService;

  @Autowired
  private UserService userService;

  @PostMapping("/getTasksInfo")
  public ResponseEntity<?> getTasksInfo(@RequestBody Map<String, String> request) {
      String appAcronym = request.get("appAcronym");
      String currentUser = userService.getCurrentUser();

      try {
          // Fetch tasks and permissions
          var tasks = taskService.getTasksInfo(appAcronym);
          var permissions = taskService.getUserAppPermissions(currentUser, appAcronym);

          System.out.println("checking tasks for getTasksInfo " + tasks);
          System.out.println("checking for permissions in getTasksInfo " + permissions);

          return ResponseEntity.ok(Map.of("tasks", tasks, "permissions", permissions));
      } catch (Exception e) {
          return ResponseEntity.status(500).body(Map.of("message", "Server error", "success", false));
      }
  }

  @PostMapping("/taskCreation")
  public ResponseEntity<?> taskCreation(@RequestBody Map<String, Object> request) {
      // Extract required fields from request body
      String appAcronym = (String) request.get("appAcronym");
      String taskName = (String) request.get("taskName");
      String creator = (String) request.get("creator");
      String owner = (String) request.get("owner");
      String description = (String) request.get("description");
      String notes = (String) request.get("notes");
      String planName = Optional.ofNullable((String) request.get("planName")).orElse("");

      try {
          // Call service to create the task
          Task createdTask = taskService.createTask(appAcronym, taskName, creator, owner, description, notes, planName);
          return ResponseEntity.status(201).body(Map.of("message", "Task created successfully", "task", createdTask, "success", true));
      } catch (IllegalArgumentException e) {
          return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", false));
      } catch (Exception e) {
          return ResponseEntity.status(500).body(Map.of("message", "An error occurred while creating the task.", "success", false));
      }
  }

  @PutMapping("/updateTask")
  public ResponseEntity<?> updateTask(@RequestBody Map<String, Object> request) {
      // Extract required fields from request body
      String taskId = (String) request.get("taskId");
      String planName = (String) request.get("planName");
      String taskState = (String) request.get("taskState");
      String notes = (String) request.get("notes");
      String action = (String) request.get("action");
      String currentUser = userService.getCurrentUser();

      try {
          System.out.println("checking update task request: " + request);

          // Call service to update the task
          Task updatedTask = taskService.updateTask(taskId, planName, taskState, notes, action, currentUser);
        //   return ResponseEntity.ok(Map.of("message", "Task updated successfully", "task", updatedTask, "success", true));
          return ResponseEntity.ok(Map.of("message", "Task updated successfully", "state", updatedTask.getTaskState(), "taskOwner", updatedTask.getTaskOwner(), "updatedNotes", updatedTask.getTaskNotes(), "success", true));
      } catch (IllegalArgumentException e) {
          return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", false));
      } catch (Exception e) {
        System.out.println("e  " + e);
          return ResponseEntity.status(500).body(Map.of("message", "An error occurred while updating the task.", "success", false));
      }
  }
}
