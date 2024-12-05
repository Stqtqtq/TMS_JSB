package com.tms.assignment_jsb.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.assignment_jsb.entity.Application;
import com.tms.assignment_jsb.entity.Task;
import com.tms.assignment_jsb.entity.UserGroupsId;
import com.tms.assignment_jsb.repository.AccountsRepository;
import com.tms.assignment_jsb.repository.ApplicationRepository;
import com.tms.assignment_jsb.repository.TaskRepository;
import com.tms.assignment_jsb.repository.UserGroupsRepository;

@Service
public class TaskService {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private ApplicationService applicationService;

  @Autowired
  UserGroupsRepository userGroupsRepository;

  @Autowired
  AccountsRepository accountsRepository;

  @Autowired
  EmailService emailService;

  @Autowired
  ApplicationRepository applicationRepository;

  public List<Task> getTasksInfo(String appAcronym) {
      return taskRepository.findByTaskAppAcronym(appAcronym);
  }

  @Transactional
  public Task createTask(String appAcronym, String taskName, String creator, String owner, String description, String notes, String planName) {
      // Validate task name and description
      if (taskName == null || taskName.length() > 50 || !taskName.matches("^[a-zA-Z0-9\\s]{1,50}$")) {
          throw new IllegalArgumentException("Invalid task name");
      }
      if (description != null && description.length() > 255) {
          throw new IllegalArgumentException("Description too long");
      }

      // Generate formatted timestamp and note
      LocalDateTime now = LocalDateTime.now();
      String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      String formattedNote = String.format(
              "\n**********\n[%s, %s]\n%s\n\n'Task created and is in the Open state'\n",
              creator, timestamp, notes != null ? notes : "No notes provided"
      );

      // Get the latest RNumber for the application
      Integer maxRNumber = applicationService.getMaxRNumber(appAcronym);
      if (maxRNumber == null) maxRNumber = 0;

      String taskId = appAcronym + "_" + (maxRNumber + 1);

      // Create and save the task
      Task task = new Task();
      task.setTaskId(taskId);
      task.setTaskName(taskName);
      task.setTaskDescription(description);
      task.setTaskNotes(formattedNote);
      task.setTaskPlan(planName);
      task.setTaskAppAcronym(appAcronym);
      task.setTaskState("Open");
      task.setTaskCreator(creator);
      task.setTaskOwner(owner);
      task.setTaskCreatedate(now.format(DateTimeFormatter.ISO_DATE));

      taskRepository.save(task);

      // Update the RNumber in the application
      applicationService.updateRNumber(appAcronym, maxRNumber + 1);

      return task;
  }

  @Transactional
  public Task updateTask(String taskId, String planName, String taskState, String notes, String action, String currentUser) {
      // Fetch the task
      Optional<Task> optionalTask = taskRepository.findByTaskId(taskId);
      if (optionalTask.isEmpty()) {
          throw new IllegalArgumentException("Task not found");
      }
      Task task = optionalTask.get();

      // Handle promotion/demotion logic
      String newTaskState = determineNextState(task.getTaskState(), action);
      if (newTaskState == null) {
          throw new IllegalArgumentException("Invalid state transition");
      }

      // Generate formatted note
      LocalDateTime now = LocalDateTime.now();
      String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

      String stateChangeNote = "";

      if(newTaskState.equals(taskState)) {
        stateChangeNote = String.format(
                "\n**********\n[%s, %s, %s]\n",
                currentUser, taskState, timestamp 
        );
      } else {
        stateChangeNote = String.format(
                "\n**********\n[%s, %s, %s]\nState changed from '%s' to '%s'\n",
                currentUser, taskState, timestamp, task.getTaskState(), newTaskState
        );
      }
    //   String stateChangeNote = String.format(
    //           "\n**********\n[%s, %s, %s]\nState changed from '%s' to '%s'\n",
    //           currentUser, taskState, timestamp, task.getTaskState(), newTaskState
    //   );

      String combinedNotes = stateChangeNote + (notes != null ? notes : "");

        // String formattedNote = notes != null ? notes : "";
        // String combinedNotes = stateChangeNote + formattedNote + task.getTaskNotes();

      // Update task fields
      task.setTaskPlan(planName);
      task.setTaskNotes(combinedNotes + task.getTaskNotes());
    //   task.setTaskNotes(combinedNotes);
      task.setTaskOwner(currentUser);
      task.setTaskState(newTaskState);

    //   return taskRepository.save(task);
        // Save task
    Task updatedTask = taskRepository.save(task);

    // Check if updating from 'Doing' -> 'Done' state, send email
    if ("promote".equals(action) && "Done".equals(newTaskState)) {
        sendApprovalEmail(taskId, newTaskState, currentUser);
    }

        return updatedTask;
    }

    private void sendApprovalEmail(String taskId, String taskState, String currentUser) {
        try {
            // Fetch the application record to determine the permit group dynamically
            Optional<Application> optionalApp = applicationRepository.findByTaskId(taskId);
            if (optionalApp.isEmpty()) {
                throw new IllegalArgumentException("Application not found for task ID: " + taskId);
            }
            Application application = optionalApp.get();

            // Determine the permit group field dynamically
            String permitGroup = getPermitGroupByState(application, taskState);
            if (permitGroup == null || permitGroup.isEmpty()) {
                throw new IllegalArgumentException("No permit group defined for state: " + taskState);
            }

            // Check if the current user belongs to the permit group
            boolean userHasPermission = userGroupsRepository.existsById_UsernameAndId_Groupname(currentUser, permitGroup);
            if (!userHasPermission) {
                throw new IllegalArgumentException("Current user does not have permission for state: " + taskState);
            }

            // Fetch users in the permit group
            List<String> recipientUsernames = userGroupsRepository.findUsernameByGroupname(permitGroup);
            if (recipientUsernames.isEmpty()) {
                throw new IllegalArgumentException("No users found in the approval group");
            }

            // Fetch email addresses for the recipients
            List<String> recipientEmails = new ArrayList<>();
            for (String username : recipientUsernames) {
                accountsRepository.findEmailByUsername(username)
                    .ifPresent(recipientEmails::add);
            }

            if (recipientEmails.isEmpty()) {
                throw new IllegalArgumentException("No email addresses found for recipients");
            }

            // Create the email content
            String subject = "Task Pending Approval";
            String body = String.format("Task ID: %s is pending approval. Please review and take action.", taskId);

            // Send the email
            for (String email : recipientEmails) {
                new Thread(() -> {
                    emailService.sendEmail(email, subject, body);
                    }
                ).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error while sending approval emails: " + e.getMessage());
        }
    }

    private String getPermitGroupByState(Application application, String taskState) {
        switch (taskState) {
            case "Open":
                return application.getAppPermitOpen();
            case "Todo":
                return application.getAppPermitTodolist();
            case "Doing":
                return application.getAppPermitDoing();
            case "Done":
                return application.getAppPermitDone();
            default:
                return null; // No approval group for this state
        }
    }


    // private void sendApprovalEmail(String taskState, String taskId, String currentUser) {
    //     try {
    //         // Determine the approval group based on task state
    //         String approvalGroup = determineApprovalGroup(taskState);
    //         if (approvalGroup == null || approvalGroup.isEmpty()) {
    //             throw new IllegalArgumentException("Approval group not defined for state: " + taskState);
    //         }

    //     // Fetch users from the group
    //     List<String> recipientUsernames = userGroupsRepository.findUsernameByGroupname(approvalGroup);

    //     if (recipientUsernames.isEmpty()) {
    //         throw new IllegalArgumentException("No users found in the approval group");
    //     }

    //     // Fetch email addresses for the recipients
    //     List<String> recipientEmails = new ArrayList<>();
    //     for (String username : recipientUsernames) {
    //         accountsRepository.findEmailByUsername(username)
    //             .ifPresent(recipientEmails::add);
    //     }

    //     if (recipientEmails.isEmpty()) {
    //         throw new IllegalArgumentException("No email addresses found for recipients");
    //     }

    //     // Create the email content
    //     String subject = "Task Pending Approval";
    //     String body = String.format("Task ID: %s is pending approval. Please review and take action.", taskId);

    //     // Send the email
    //     for (String email : recipientEmails) {
    //         emailService.sendEmail(email, subject, body);
    //     }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         throw new IllegalArgumentException("Error while sending approval emails: " + e.getMessage());
    //     }
    // }

    // Helper method to map task states to approval groups
    // private String determineApprovalGroup(String taskState) {
    //     switch (taskState) {
    //         case "Open":
    //             return "app_permit_open";
    //         case "Todo":
    //             return "app_permit_todolist";
    //         case "Doing":
    //             return "app_permit_doing";
    //         case "Done":
    //             return "app_permit_done";
    //         default:
    //             return null; // No approval group for this state
    //     }
    // }

  private String determineNextState(String currentState, String action) {
    if(action == null) {
        return currentState;
    }
      else if (action.equals("promote")) {
          switch (currentState) {
              case "Open": return "Todo";
              case "Todo": return "Doing";
              case "Doing": return "Done";
              case "Done": return "Close";
              default: return null;
          }
      } else if (action.equals("demote")) {
          switch (currentState) {
              case "Todo": return "Open";
              case "Doing": return "Todo";
              case "Done": return "Doing";
              default: return null;
          }
      }
      return null;
  }

  public Map<String, Object> getUserAppPermissions(String username, String appAcronym) {
    Application application = applicationService.findByAppAcronym(appAcronym)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));

    Map<String, Boolean> permissionStatus = new HashMap<>();
    permissionStatus.put("app_permit_create", checkUserPermission(username, application.getAppPermitCreate()));
    permissionStatus.put("app_permit_open", checkUserPermission(username, application.getAppPermitOpen()));
    permissionStatus.put("app_permit_todolist", checkUserPermission(username, application.getAppPermitTodolist()));
    permissionStatus.put("app_permit_doing", checkUserPermission(username, application.getAppPermitDoing()));
    permissionStatus.put("app_permit_done", checkUserPermission(username, application.getAppPermitDone()));

    Map<String, Object> permissions = new HashMap<>();
    permissions.put("permissionStatus", permissionStatus);
    permissions.put("success", true); // Add success key for consistency

    return permissions;
  }

  private boolean checkUserPermission(String username, String group) {
    if (group == null || group.isEmpty()) {
        return false;
    }
    return userGroupsRepository.existsById(new UserGroupsId(group, username));
  }
}
