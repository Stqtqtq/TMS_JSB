package com.tms.assignment_jsb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_groups")
public class UserGroups {
  @EmbeddedId
  private UserGroupsId id;

  public UserGroups() {}

  public UserGroups(UserGroupsId id) {
    this.id = id;
  }

  public UserGroupsId getId() {
    return this.id;
  }

  public void setId(UserGroupsId id) {
    this.id = id;
  }
}