package com.tms.assignment_jsb.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.tms.assignment_jsb.entity.Accounts;
import com.tms.assignment_jsb.service.EmailService;
import com.tms.assignment_jsb.service.JwtService;
import com.tms.assignment_jsb.service.UserGroupsService;
import com.tms.assignment_jsb.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthController {
  
  @Autowired
  private UserService userService;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserGroupsService userGroupsService;

  @Autowired
  private EmailService emailService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
    String username = loginRequest.get("username");
    String password = loginRequest.get("password");

    if (username == null || password == null) {
      return ResponseEntity.badRequest().body(Map.of("message", "Username and password are required.", "success", false));
    }

    // System.out.println("Checking if I get any response" + username + password);

    Accounts user = userService.findByUsername(username);
    if (user == null || !userService.verifyPassword(password, user.getPassword())) {
      return ResponseEntity.status(401).body(Map.of("message", "Invalid username or password."));
    }

    if (user.getIsActive() != 1) {
      return ResponseEntity.status(401).body(Map.of("message", "Invalid username or password"));
    }

    String ipAddress = loginRequest.getOrDefault("ipAddress", "unknown");
    String browserInfo = loginRequest.getOrDefault("browser", "unknown");

    String token = jwtService.generateToken(username, ipAddress, browserInfo);

    Cookie cookie = new Cookie("token", token);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(6 * 60 * 60);
    cookie.setPath("/");
    response.addCookie(cookie);

    return ResponseEntity.ok(Map.of("username", username, "message", "Login successful", "success", true));
  } 

  @GetMapping("/logout1")
  public void logout(HttpServletResponse response, HttpServletRequest request) {
    Cookie cookie = new Cookie("token", null);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);


    // emailService.sendEmail("testing@email.com", "testing", "testing");
    // response.setStatus(HttpServletResponse.SC_FOUND);
    // response.setHeader("Location", "/login");

    // request.getSession().invalidate();

    // try {
    //   response.sendRedirect("/login");
    //   System.out.println("redirected");
    // } catch (IOException e) {
    //   e.printStackTrace();
    // }
  } 

  @GetMapping("/landing")
  public ResponseEntity<?> landing(HttpServletResponse response, HttpServletRequest request, @RequestAttribute(name = "inactiveAccount", required = true) boolean inactiveAccount) {

    if (inactiveAccount) {
      logout(response, request);
      return ResponseEntity.status(401).body(Map.of("message", "Account is inactive", "inactiveAccount", inactiveAccount));
    }

    try {
      // Retrieve the token from the cookie
      String token = null;
      if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
          if ("token".equals(cookie.getName())) {
            token = cookie.getValue();
            break;
          }
        }
      }

      if (token == null || jwtService.isTokenExpired(token)) {
          return ResponseEntity.status(401).body(Map.of("isAuthenticated", false));
      }

      // Extract username from token
      String username = jwtService.extractClaims(token).getSubject();

      // Check if user is in "Admin" group
      boolean isAdmin = userGroupsService.isUserInGroup(username, "Admin");

      // Map<String, Object> response = new HashMap<>();
      // response.put("isAuthenticated", true);
      // response.put("isAdmin", isAdmin);
      // response.put("username", username);

      // return ResponseEntity.ok(response);

      return ResponseEntity.ok(Map.of("isAuthenticated", true, "isAdmin", isAdmin, "username", username, "success", true));

      } catch (Exception e) {
        return ResponseEntity.status(403).body(Map.of("isAuthenticated", false));
      }
    }
}
