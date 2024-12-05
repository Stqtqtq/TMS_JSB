package com.tms.assignment_jsb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tms.assignment_jsb.entity.UserGroups;
import com.tms.assignment_jsb.entity.UserGroupsId;

@Repository
public interface UserGroupsRepository extends CrudRepository<UserGroups, UserGroupsId>{
  Optional<UserGroups> findById_UsernameAndId_Groupname(String username, String groupname);

  // Retrieve all group names for a given username
  @Query("SELECT g.id.groupname FROM UserGroups g WHERE g.id.username = :username")
  List<String> findGroupsByUsername(String username);

  boolean existsById_Groupname(String groupname);

  void deleteById_Username(String username);

  // Dynamically check if the user belongs to a specific group
  @Query("SELECT g FROM UserGroups g WHERE g.id.username = :username AND g.id.groupname = :groupname")
  Optional<UserGroups> findByUsernameAndGroupname(String username, String groupname);

  // Fetch all groups for a specific username
  @Query("SELECT g FROM UserGroups g WHERE g.id.username = :username")
  List<UserGroups> findAllByUsername(String username);

  // New method to fetch all UserGroups by username
  @Query("SELECT g FROM UserGroups g WHERE g.id.username = :username")
  List<UserGroups> findById_Username(String username);
    
  @Query("SELECT DISTINCT g.id.groupname FROM UserGroups g")
  List<String> findDistinctGroups();

  @Query("SELECT g.id.username FROM UserGroups g WHERE g.id.groupname = :groupname")
  List<String> findUsernameByGroupname(String groupname);

  @Query("SELECT COUNT(g) > 0 FROM UserGroups g WHERE g.id.username = :username AND g.id.groupname = :groupname")
  boolean existsById_UsernameAndId_Groupname(String username, String groupname);
}
