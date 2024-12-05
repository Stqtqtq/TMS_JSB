package com.tms.assignment_jsb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Accounts {
  @Id
  private String username;
  private String password;
  private String email;

  @Column(name = "isActive")
  private int isActive;

  public Accounts() {}

  public Accounts(String username, String password, String email, int isActive) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.isActive = isActive;
  }

  public String getUsername() {
      return username;
  }

  public void setUsername(String username) {
      this.username = username;
  }

  public String getPassword() {
      return password;
  }

  public void setPassword(String password) {
      this.password = password;
  }

  public String getEmail() {
      return email;
  }

  public void setEmail(String email) {
      this.email = email;
  }

  public int getIsActive() {
      return isActive;
  }

  public void setIsActive(int isActive) {
      this.isActive = isActive;
  } 
}