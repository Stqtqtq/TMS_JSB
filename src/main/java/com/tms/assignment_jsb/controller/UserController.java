package com.tms.assignment_jsb.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import com.tms.assignment_jsb.entity.Accounts;
import com.tms.assignment_jsb.entity.UserGroups;
import com.tms.assignment_jsb.repository.AccountsRepository;
import com.tms.assignment_jsb.repository.UserGroupsRepository;
import com.tms.assignment_jsb.service.UserGroupsService;
import com.tms.assignment_jsb.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {
  
  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  private UserGroupsRepository userGroupsRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private UserGroupsService userGroupsService;

  @Autowired
  private AuthController authController;

  @GetMapping("/getUsersInfo")
  public ResponseEntity<?> getUsersInfo(@RequestAttribute(name = "isAdmin", required = false) boolean isAdmin, @RequestAttribute(name = "inactiveAccount", required = true) boolean inactiveAccount, HttpServletResponse response, HttpServletRequest request) {
    if (!isAdmin) {
      return ResponseEntity.status(403).body(Map.of("message", "Forbidden", "success", false));
    }

    System.out.println("inactive account " + inactiveAccount);
    if (inactiveAccount) {
      authController.logout(response, request);
      return ResponseEntity.status(401).body(Map.of("message", "Account is inactive", "inactiveAccount", inactiveAccount));
    }

    try {
        List<Accounts> users = accountsRepository.findAll();
        List<UserGroups> userGroups = new ArrayList<>();
        userGroupsRepository.findAll().forEach(userGroups::add);
        // List<UserGroups> userGroups = userGroupsRepository.findAll();

        Map<String, List<String>> groupMapping = userGroups.stream()
                .collect(Collectors.groupingBy(
                        group -> group.getId().getUsername(),
                        Collectors.mapping(group -> group.getId().getGroupname(), Collectors.toList())
                ));

        List<Map<String, Object>> userInfo = users.stream()
                .map(user -> Map.of(
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "isActive", user.getIsActive(),
                        "groups", groupMapping.getOrDefault(user.getUsername(), Collections.emptyList())
                ))
                .collect(Collectors.toList());

        // Map groups to desired format
        List<Map<String, String>> distinctGroups = userGroups.stream()
                .map(group -> Map.of("groupname", group.getId().getGroupname()))
                .distinct()
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(Map.of("users", userInfo, "groups", distinctGroups));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("message", "Server error", "success", false));
    }
  }

  @PostMapping("/createGrp")
  public ResponseEntity<?> createGroup(@RequestBody Map<String, String> request, 
                                        HttpServletResponse res,
                                        HttpServletRequest req,
                                        @RequestAttribute("isAdmin") boolean isAdmin,
                                        @RequestAttribute(name = "inactiveAccount", required = true) boolean inactiveAccount) 
  {
  
    if (!isAdmin) {
      return ResponseEntity.status(403).body(Map.of("message", "Forbidden", "success", false));
    }

    if (inactiveAccount) {
      authController.logout(res, req);
      return ResponseEntity.status(401).body(Map.of("message", "Account is inactive", "inactiveAccount", inactiveAccount));
    }

    String groupName = request.get("groupname");
    String username = request.get("username");

    if (groupName == null || groupName.isEmpty() || !userGroupsService.isValidGroupName(groupName)) {
      return ResponseEntity.badRequest().body(Map.of("message", "Invalid group name format.", "success", false));
    }

        try {
        // Use the username if provided; otherwise, insert a default or global group
        if (username != null && !username.isEmpty()) {
            boolean created = userGroupsService.addUserToGroup(username, groupName);
            if (created) {
                return ResponseEntity.status(201).body(Map.of("message", "Group created successfully.", "success", true));
            }
        } else {
            boolean created = userGroupsService.createGroup(groupName);
            if (created) {
                return ResponseEntity.status(201).body(Map.of("message", "Group created successfully.", "success", true));
            }
        }
        return ResponseEntity.status(400).body(Map.of("message", "Failed to create group.", "success", false));
    } catch (Exception e) {
        return ResponseEntity.status(500).body(Map.of("message", "An error has occurred while creating the group.", "success", false));
    }
  }

  @PostMapping("/createUser")
  public ResponseEntity<?> createUser(@RequestBody Map<String, Object> request,
                                        HttpServletResponse res,
                                        HttpServletRequest req,
                                        @RequestAttribute("isAdmin") boolean isAdmin,
                                        @RequestAttribute(name = "inactiveAccount", required = true) boolean inactiveAccount) {

    if (!isAdmin) {
      return ResponseEntity.status(403).body(Map.of("message", "Forbidden", "success", false));
    }

    if (inactiveAccount) {
      authController.logout(res, req);
      return ResponseEntity.status(401).body(Map.of("message", "Account is inactive", "inactiveAccount", inactiveAccount));
    }

    // Extract request fields
    String username = (String) request.get("username");
    String password = (String) request.get("password");
    String email = (String) request.getOrDefault("email", "");
    List<String> groups = (List<String>) request.get("groups");
    Integer isActive = (Integer) request.get("isActive");

    // Validate fields
    if (!userService.isValidUsername(username)) {
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid username. It must be alphanumeric.", "success", false));
    }
    if (!userService.isValidPassword(password)) {
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid password. It must be 8-10 characters long, alphanumeric, and include special characters.", "success", false));
    }
    if (!email.isEmpty() && !userService.isValidEmail(email)) { // Validate only if email is non-empty
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid email format.", "success", false));
    }

    // Check if the username already exists
    if (userService.findByUsername(username) != null) {
        return ResponseEntity.badRequest().body(Map.of("message", "Username already exists.", "success", false));
    }

    try {
      boolean created = userService.createUser(username, password, email, groups, isActive);
      if (created) {
        return ResponseEntity.status(201).body(Map.of("message", "User created successfully.", "success", true));
      } else {
        return ResponseEntity.status(400).body(Map.of("message", "An error occured while creating the user.", "success", false));
      }
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("message", "An error occurred while creating the user.", "success", false));
    }
  }

  @PutMapping("/update")
  public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> request, 
                                        HttpServletResponse res,
                                        HttpServletRequest req,
                                        @RequestAttribute("isAdmin") boolean isAdmin,
                                        @RequestAttribute(name = "inactiveAccount", required = true) boolean inactiveAccount) 
  {

    if (!isAdmin) {
      return ResponseEntity.status(403).body(Map.of("message", "Forbidden", "success", false));
    }

    if (inactiveAccount) {
      authController.logout(res, req);
      return ResponseEntity.status(401).body(Map.of("message", "Account is inactive", "inactiveAccount", inactiveAccount));
    }

    // Extract request fields
    String username = (String) request.get("username");
    String password = (String) request.get("password");
    String email = (String) request.getOrDefault("email", "");
    List<String> groups = (List<String>) request.get("groups");
    Integer isActive = (Integer) request.get("isActive");

    try {

      // Validate other fields
      if (!password.isEmpty() && !userService.isValidPassword(password)) {
          return ResponseEntity.badRequest().body(Map.of("message", "Invalid password. It must be 8-10 characters long, alphanumeric, and include special characters.", "success", false));
      }
      if (!email.isEmpty() && !userService.isValidEmail(email)) { // Validate email only if it's non-empty
          return ResponseEntity.badRequest().body(Map.of("message", "Invalid email format.", "success", false));
      }
      if (username.equals("admin") && isActive != null && isActive == 0) {
          return ResponseEntity.badRequest().body(Map.of("message", "'admin' username cannot be set to inactive.", "success", false));
      }
      if (username.equals("admin") && groups != null && !groups.contains("Admin")) {
          return ResponseEntity.badRequest().body(Map.of("message", "Cannot remove 'Admin' group from 'admin' username.", "success", false));
      }

      boolean updated = userService.updateUser(username, password, email, groups, isActive);
      if (updated) {
        return ResponseEntity.ok(Map.of("message", "User updated successfully.", "success", true));
      } else {
        return ResponseEntity.status(400).body(Map.of("message", "An error occured while updating the user.", "success", false));
      }
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("message", "An error occurred while updating the user.", "success", false));
    }
  }
}
