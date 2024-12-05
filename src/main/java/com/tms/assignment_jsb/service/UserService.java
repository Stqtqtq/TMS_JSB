package com.tms.assignment_jsb.service;

import com.tms.assignment_jsb.entity.Accounts;
import com.tms.assignment_jsb.entity.UserGroups;
import com.tms.assignment_jsb.entity.UserGroupsId;
import com.tms.assignment_jsb.repository.AccountsRepository;
import com.tms.assignment_jsb.repository.ApplicationRepository;
import com.tms.assignment_jsb.repository.UserGroupsRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  private UserGroupsRepository userGroupsRepository;

  @Autowired
  ApplicationRepository applicationRepository;

  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public boolean isValidUsername(String username) {
    return username.matches("^[a-zA-Z0-9]+$");
  }

  public Accounts findByUsername(String username) {
    return accountsRepository.findByUsername(username);
  }

  public boolean verifyPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  public boolean isValidEmail(String email) {
    // return email.matches("^[a-zA-Z0-9!@#$%^&*()_+={}|[\\]\\\\:;\"'<>,.?/~`-]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$");
    return email.matches("^[a-zA-Z0-9!@#$%^&*()_+={}\\[\\]|\\\\:;\"'<>,.?/~`-]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$");

  }

  public boolean isValidPassword(String password) {
    // return password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?])[a-zA-Z\\d!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]{8,10}$");
    return password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])[a-zA-Z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,10}$");
  }

  public boolean updateEmail(String username, String email) {
    Accounts user = accountsRepository.findByUsername(username);
    if (user != null) {
      user.setEmail(email);
      accountsRepository.save(user);
      return true;
    }
    return false;
  }

  public boolean updatePassword(String username, String password) {
    Accounts user = accountsRepository.findByUsername(username);
    if (user != null) {
      String hashedPassword = passwordEncoder.encode(password);
      user.setPassword(hashedPassword);
      accountsRepository.save(user);
      return true;
    }
    return false;
  }

  public boolean createUser(String username, String rawPassword, String email, List<String> groups, Integer isActive) {
    // Validate username and password
    if (!isValidUsername(username) || !isValidPassword(rawPassword)) {
        return false;
    }

    // Validate email if provided (non-empty string)
    if (!email.isEmpty() && !isValidEmail(email)) {
        return false;
    }

    if (accountsRepository.findByUsername(username) != null) {
      return false;
    }

    String hashedPassword = passwordEncoder.encode(rawPassword);

    Accounts newUser = new Accounts();
    newUser.setUsername(username);
    newUser.setPassword(hashedPassword);
    newUser.setEmail(email);
    newUser.setIsActive(isActive);

    accountsRepository.save(newUser);

    if (groups != null) {
      for (String group : groups) {
        UserGroups userGroup = new UserGroups(new UserGroupsId(group, username));
        userGroupsRepository.save(userGroup);
      }
    }

    return true;
  }

  public boolean updateUser(String username, String password, String email, List<String> groups, Integer isActive) {
    
    Accounts user = accountsRepository.findByUsername(username);

    if(user == null) {
      return false;
    }

    if (username.equals("admin")) {
      if (isActive == 0 || (groups != null && !groups.contains("Admin"))) {
        return false;
      }
    }

    // Update email if provided and valid
    // if (!email.isEmpty() && isValidEmail(email)) {
    //     user.setEmail(email);
    // }

    // Update email if provided
    if (email != null && !email.isEmpty()) {
      if (isValidEmail(email)) {
        user.setEmail(email);
      } else {
        return false; // Invalid email format
      }
    }

    if (isActive != null) {
      user.setIsActive(isActive);
    }

    // if (password != null && isValidPassword(password)) {
    //   String hashedPassword = passwordEncoder.encode(password);
    //   user.setPassword(hashedPassword);
    // }

    // Update password if provided
    if (password != null && !password.isEmpty()) {
      if (isValidPassword(password)) {
        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);
      } else {
        return false; // Invalid password format
      }
    }

    accountsRepository.save(user);

    // if (groups != null) {
    //   userGroupsRepository.deleteById_Username(username);
    //   for (String group : groups) {
    //     UserGroups userGroup = new UserGroups(new UserGroupsId(group, username));
    //     userGroupsRepository.save(userGroup);
    //   }
    // }
    //  return true;

        // Update groups if provided
    if (groups != null) {
        // Fetch existing groups
        List<UserGroups> existingGroups = userGroupsRepository.findById_Username(username);

        // Prepare existing group names
        List<String> existingGroupNames = existingGroups.stream()
                .map(group -> group.getId().getGroupname())
                .collect(Collectors.toList());

        // Insert new groups
        for (String group : groups) {
            if (!existingGroupNames.contains(group)) {
                UserGroups userGroup = new UserGroups(new UserGroupsId(group, username));
                userGroupsRepository.save(userGroup);
            }
        }

        // Delete groups that are not in the provided list
        List<String> groupsToDelete = existingGroupNames.stream()
                .filter(existingGroup -> !groups.contains(existingGroup))
                .collect(Collectors.toList());

        for (String groupToDelete : groupsToDelete) {
            userGroupsRepository.deleteById(new UserGroupsId(groupToDelete, username));
        }
    }

    return true;
  }

  public String getCurrentUser() {
    // Retrieve the authentication object from the security context
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Return the username if authentication is valid
    if (authentication != null && authentication.isAuthenticated()) {
        return authentication.getName();
    }

    // Return null if no authenticated user is found
    return null;
  }


}
