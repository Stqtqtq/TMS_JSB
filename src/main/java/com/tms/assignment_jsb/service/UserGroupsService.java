package com.tms.assignment_jsb.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.assignment_jsb.entity.UserGroups;
import com.tms.assignment_jsb.entity.UserGroupsId;
import com.tms.assignment_jsb.repository.UserGroupsRepository;

@Service
public class UserGroupsService {
 
  @Autowired
  private UserGroupsRepository userGroupsRepository;

  public boolean isUserInGroup(String username, String Groupname) {
    return userGroupsRepository.findById_UsernameAndId_Groupname(username, Groupname).isPresent();
  }

  public boolean isValidGroupName(String groupName) {
    return groupName.matches("^[a-zA-Z0-9_]+$");
  }

  // Create a global group (not tied to a specific user)
  public boolean createGroup(String groupName) {
      boolean exists = userGroupsRepository.existsById_Groupname(groupName);
      if (exists) {
          return false;
      }

      try {
          UserGroups newGroup = new UserGroups();
          UserGroupsId newGroupId = new UserGroupsId(groupName, ""); // null username for global group
          newGroup.setId(newGroupId);
          userGroupsRepository.save(newGroup);
          return true;
      } catch (Exception e) {
          System.err.println("Error creating group: " + e.getMessage());
          return false;
      }
  }

  // Add a user to a group
  public boolean addUserToGroup(String username, String groupName) {
      try {
          UserGroups newGroup = new UserGroups();
          UserGroupsId newGroupId = new UserGroupsId(groupName, username); // username included
          newGroup.setId(newGroupId);
          userGroupsRepository.save(newGroup);
          return true;
      } catch (Exception e) {
          System.err.println("Error adding user to group: " + e.getMessage());
          return false;
      }
  }
 
    public List<Map<String, String>> getAllDistinctGroups() {
        return userGroupsRepository.findDistinctGroups().stream()
            .map(groupname -> Map.of("groupname", groupname))
            .toList();
    }
}
