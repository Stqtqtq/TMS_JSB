package com.tms.assignment_jsb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "task")
public class Task {
  
  @Id
  @Column(name = "task_id")
  private String taskId;
  @Column(name = "task_name")
  private String taskName;
  @Column(name = "task_description")
  private String taskDescription;
  @Column(name = "task_notes")
  private String taskNotes;
  @Column(name = "task_plan")
  private String taskPlan;
  @Column(name = "task_app_acronym")
  private String taskAppAcronym;
  @Column(name = "task_state")
  private String taskState;
  @Column(name = "task_creator")
  private String taskCreator;
  @Column(name = "task_owner")
  private String taskOwner;
  @Column(name = "task_createdate")
  private String taskCreatedate;

  public Task() {}

  public Task(String taskId, String taskName, String taskDescription, String taskNotes, String taskPlan, String taskAppAcronym, String taskState, String taskCreator, String taskOwner, String taskCreatedate) {
    this.taskId = taskId;
    this.taskName = taskName;
    this.taskDescription = taskDescription;
    this.taskNotes = taskNotes;
    this.taskPlan = taskPlan;
    this.taskAppAcronym = taskAppAcronym;
    this.taskState = taskState;
    this.taskCreator = taskCreator;
    this.taskOwner = taskOwner;
    this.taskCreatedate = taskCreatedate;
  }

  public String getTaskId() {
    return this.taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getTaskName() {
    return this.taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public String getTaskDescription() {
    return this.taskDescription;
  }

  public void setTaskDescription(String taskDescription) {
    this.taskDescription = taskDescription;
  }

  public String getTaskNotes() {
    return this.taskNotes;
  }

  public void setTaskNotes(String taskNotes) {
    this.taskNotes = taskNotes;
  }

  public String getTaskPlan() {
    return this.taskPlan;
  }

  public void setTaskPlan(String taskPlan) {
    this.taskPlan = taskPlan;
  }

  public String getTaskAppAcronym() {
    return this.taskAppAcronym;
  }

  public void setTaskAppAcronym(String taskAppAcronym) {
    this.taskAppAcronym = taskAppAcronym;
  }

  public String getTaskState() {
    return this.taskState;
  }

  public void setTaskState(String taskState) {
    this.taskState = taskState;
  }

  public String getTaskCreator() {
    return this.taskCreator;
  }

  public void setTaskCreator(String taskCreator) {
    this.taskCreator = taskCreator;
  }

  public String getTaskOwner() {
    return this.taskOwner;
  }

  public void setTaskOwner(String taskOwner) {
    this.taskOwner = taskOwner;
  }

  public String getTaskCreatedate() {
    return this.taskCreatedate;
  }

  public void setTaskCreatedate(String taskCreatedate) {
    this.taskCreatedate = taskCreatedate;
  }

  // @Override
  // public String toString() {
  //   return "{" +
  //     " taskId='" + getTaskId() + "'" +
  //     ", taskName='" + getTaskName() + "'" +
  //     ", taskDescription='" + getTaskDescription() + "'" +
  //     ", taskNotes='" + getTaskNotes() + "'" +
  //     ", taskPlan='" + getTaskPlan() + "'" +
  //     ", taskAppAcronym='" + getTaskAppAcronym() + "'" +
  //     ", taskState='" + getTaskState() + "'" +
  //     ", taskCreator='" + getTaskCreator() + "'" +
  //     ", taskOwner='" + getTaskOwner() + "'" +
  //     ", taskCreatedate='" + getTaskCreatedate() + "'" +
  //     "}";
  // }

}
