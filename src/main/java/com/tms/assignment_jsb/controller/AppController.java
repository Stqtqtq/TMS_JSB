package com.tms.assignment_jsb.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import com.tms.assignment_jsb.entity.Application;
import com.tms.assignment_jsb.service.ApplicationService;
import com.tms.assignment_jsb.service.UserGroupsService;

@Controller
public class AppController {
  
  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private UserGroupsService userGroupsService;

  @GetMapping("/getAppsInfo")
  public ResponseEntity<?> getAppsInfo(@RequestAttribute("username") String currentUser, @RequestAttribute("isPL") boolean isPL) {
    try {
      // Fetch distinct groups as List<Map<String, String>>
      List<Map<String, String>> distinctGroups = userGroupsService.getAllDistinctGroups();

      // Fetch all applications
      List<Application> allApplications = applicationService.getAllApplications();

      // Build response object
      Map<String, Object> response = new HashMap<>();
      response.put("apps", allApplications);
      response.put("groups", distinctGroups);
      response.put("currentUser", currentUser);
      response.put("isPL", isPL);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("message", "Server error"));
    }
  }

  @PostMapping("/createApp")
  public ResponseEntity<?> createApp(@RequestBody Map<String, Object> request, @RequestAttribute("isPL") boolean isPL) {
      if (!isPL) {
          return ResponseEntity.status(403).body(Map.of("message", "Forbidden", "success", false));
      }

      try {

        // request.get key value must match frontend naming convention.
          String appAcronym = (String) request.get("appAcronym");
        //   Integer appRNumber = (Integer) request.get("appRNumber");
            String appRNumberStr = (String) request.get("appRNumber");
            Integer appRNumber = null;

            if (appRNumberStr != null && !appRNumberStr.isEmpty()) {
                try {
                    appRNumber = Integer.parseInt(appRNumberStr); // Parse the String into an Integer
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Invalid RNumber format", "success", false));
                }
            }
          String appStartDate = (String) request.get("appStartDate");
          String appEndDate = (String) request.get("appEndDate");
          String appPermitCreate = (String) request.get("appCreate");
          String appPermitOpen = (String) request.get("appOpen");
          String appPermitTodoList = (String) request.get("appTodo");
          String appPermitDoing = (String) request.get("appDoing");
          String appPermitDone = (String) request.get("appDone");
          String appDescription = (String) request.get("description");

          // Validate inputs
          if (appAcronym == null || !applicationService.isValidAppAcronym(appAcronym)) {
              return ResponseEntity.badRequest().body(Map.of("message", "Invalid App Acronym", "success", false));
          }
          if (appRNumber == null || appRNumber < 0 || !applicationService.isValidAppRNumber(appRNumber.toString())) {
              return ResponseEntity.badRequest().body(Map.of("message", "Invalid Rnumber", "success", false));
          }
          if (appStartDate == null) {
              return ResponseEntity.badRequest().body(Map.of("message", "Invalid start date", "success", false));
          }
          if (appEndDate == null) {
              return ResponseEntity.badRequest().body(Map.of("message", "Invalid end date", "success", false));
          }
          if (appDescription != null && appDescription.length() > 255) {
              return ResponseEntity.badRequest().body(Map.of("message", "Description too long", "success", false));
          }

          applicationService.createApp(
                appAcronym,
                appRNumber,
                appStartDate,
                appEndDate,
                appPermitCreate,
                appPermitOpen,
                appPermitTodoList,
                appPermitDoing,
                appPermitDone,
                appDescription
            );

          return ResponseEntity.status(201).body(Map.of("message", "App created successfully.", "success", true));
      } catch (IllegalArgumentException e) {
          return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", false));
      } catch (Exception e) {
          e.printStackTrace();
          return ResponseEntity.status(500).body(Map.of("message", "An error occurred while creating the app.", "success", false));
      }
  }

  @PostMapping("/updateApp")
  public ResponseEntity<?> updateApp(@RequestBody Map<String, Object> request, @RequestAttribute("isPL") boolean isPL) {
    if (!isPL) {
        return ResponseEntity.status(403).body(Map.of("message", "Forbidden", "success", false));
    }

    try {
        // request.get key value must match frontend naming convention.
        String appAcronym = (String) request.get("appAcronym");
        //   Integer appRNumber = (Integer) request.get("appRNumber");
        // Handle appRNumber dynamically as Integer or String
        Integer appRNumber = null;
        Object appRNumberObj = request.get("appRNumber");
        if (appRNumberObj instanceof Integer) {
            appRNumber = (Integer) appRNumberObj;
        } else if (appRNumberObj instanceof String) {
            appRNumber = Integer.parseInt((String) appRNumberObj);
        }
        String appStartDate = (String) request.get("appStartDate");
        String appEndDate = (String) request.get("appEndDate");
        String appPermitCreate = (String) request.get("appCreate");
        String appPermitOpen = (String) request.get("appOpen");
        String appPermitTodoList = (String) request.get("appTodo");
        String appPermitDoing = (String) request.get("appDoing");
        String appPermitDone = (String) request.get("appDone");
        String appDescription = (String) request.get("description");
        // Validate inputs
        if (appDescription != null && appDescription.length() > 255) {
            return ResponseEntity.badRequest().body(Map.of("message", "Description too long", "success", false));
        }
        
        applicationService.updateApp(
            appAcronym,
            appRNumber,
            appStartDate,
            appEndDate,
            appPermitCreate,
            appPermitOpen,
            appPermitTodoList,
            appPermitDoing,
            appPermitDone,
            appDescription
        );

        return ResponseEntity.ok(Map.of("message", "App updated successfully.", "success", true));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", false));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(Map.of("message", "An error occurred while updating the app.", "success", false));
    }
  }  
}
