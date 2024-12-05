package com.tms.assignment_jsb.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import com.tms.assignment_jsb.entity.Accounts;
import com.tms.assignment_jsb.service.UserService;

@Controller
public class ProfileController {
  
  @Autowired
  private UserService userService;

  @GetMapping("/profile")
  public ResponseEntity<?> profile(@RequestAttribute("username") String username) {
    try {
      Accounts user = userService.findByUsername(username);
      if (user != null) {
        // Map<String, String> response = new HashMap<>();
        // response.put("username", user.getUsername());
        // response.put("email", user.getEmail());
        // return ResponseEntity.ok(response);
        return ResponseEntity.ok(Map.of("username", user.getUsername(), "email", user.getEmail(), "success", true));
      } else {
        return ResponseEntity.status(404).body(Map.of("message", "User not found.", "success", false));
      }
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("message", "Server error.", "success", false));
    }
  }  

  @PutMapping("/updateEmail")
  public ResponseEntity<?> updateEmail(@RequestBody Map<String, String> request, @RequestAttribute("username") String username) {
    String email = request.get("email");
   
    if (email == null || email.isEmpty() || !userService.isValidEmail(email)) {
      return ResponseEntity.badRequest().body(Map.of("message", "Invalid Email format.", "success", false));
    }

    try {
      boolean updated = userService.updateEmail(username, email);
      if (updated) {
        return ResponseEntity.ok(Map.of("message", "Email updated successfully", "success", true)); 
      } else {
        return ResponseEntity.status(400).body(Map.of("message", "User not found.", "success", false));
      }
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("message", "An error occured while updating the email.", "success", false));
    }
  }

  @PutMapping("/updatePw")
  public ResponseEntity<?> updatePw(@RequestBody Map<String, String> request, @RequestAttribute("username") String username) {
    String password = request.get("password");

    if (password == null || password.isEmpty() || !userService.isValidPassword(password)) {
      return ResponseEntity.badRequest().body(Map.of("message", "Invalid password format.", "success", false));
    }

    try {
      boolean updated = userService.updatePassword(username, password);
      if (updated) {
        return ResponseEntity.ok(Map.of("message", "Password updated successfully.", "success", true));
      } else {
        return ResponseEntity.status(400).body(Map.of("message", "User not found.", "success", false));
      }
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("message", "An error occured while updating the password.", "success", false));
    }
  }
}
