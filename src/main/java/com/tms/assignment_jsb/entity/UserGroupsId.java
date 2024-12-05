package com.tms.assignment_jsb.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserGroupsId implements Serializable{
  private String groupname;
  private String username;

  public UserGroupsId() {}

  public UserGroupsId(String groupname, String username) {
    this.groupname = groupname;
    this.username = username;
  }

  public String getGroupname() {
    return this.groupname;
  }

  public void setGroupname(String groupname) {
    this.groupname = groupname;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      UserGroupsId that = (UserGroupsId) o;
      return Objects.equals(groupname, that.groupname) &&
              Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
      return Objects.hash(groupname, username);
  }
}
