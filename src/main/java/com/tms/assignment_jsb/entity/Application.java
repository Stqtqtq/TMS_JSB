package com.tms.assignment_jsb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "application")
public class Application {

  @Id
  @Column(name = "app_acronym")
  private String appAcronym;
  @Column(name = "app_description")
  private String appDescription;
  @Column(name = "app_rnumber")
  private Integer appRNumber;
  @Column(name = "app_startdate")
  private String appStartdate;
  @Column(name = "app_enddate")
  private String appEnddate;
  @Column(name = "app_permit_create")
  private String appPermitCreate;
  @Column(name = "app_permit_open")
  private String appPermitOpen;
  @Column(name = "app_permit_todolist")
  private String appPermitTodolist;
  @Column(name = "app_permit_doing")
  private String appPermitDoing;
  @Column(name = "app_permit_done")
  private String appPermitDone;

    
  public Application () {}


  public Application(String appAcronym, String appDescription, Integer appRNumber, String appStartdate, String appEnddate, String appPermitCreate, String appPermitOpen, String appPermitTodolist, String appPermitDoing, String appPermitDone) {
    this.appAcronym = appAcronym;
    this.appDescription = appDescription;
    this.appRNumber = appRNumber;
    this.appStartdate = appStartdate;
    this.appEnddate = appEnddate;
    this.appPermitCreate = appPermitCreate;
    this.appPermitOpen = appPermitOpen;
    this.appPermitTodolist = appPermitTodolist;
    this.appPermitDoing = appPermitDoing;
    this.appPermitDone = appPermitDone;
  }

  public String getAppAcronym() {
    return this.appAcronym;
  }

  public void setAppAcronym(String appAcronym) {
    this.appAcronym = appAcronym;
  }

  public String getAppDescription() {
    return this.appDescription;
  }

  public void setAppDescription(String appDescription) {
    this.appDescription = appDescription;
  }

  public Integer getAppRNumber() {
    return this.appRNumber;
  }

  public void setAppRNumber(Integer appRNumber) {
    this.appRNumber = appRNumber;
  }

  public String getAppStartdate() {
    return this.appStartdate;
  }

  public void setAppStartdate(String appStartdate) {
    this.appStartdate = appStartdate;
  }

  public String getAppEnddate() {
    return this.appEnddate;
  }

  public void setAppEnddate(String appEnddate) {
    this.appEnddate = appEnddate;
  }

  public String getAppPermitCreate() {
    return this.appPermitCreate;
  }

  public void setAppPermitCreate(String appPermitCreate) {
    this.appPermitCreate = appPermitCreate;
  }

  public String getAppPermitOpen() {
    return this.appPermitOpen;
  }

  public void setAppPermitOpen(String appPermitOpen) {
    this.appPermitOpen = appPermitOpen;
  }

  public String getAppPermitTodolist() {
    return this.appPermitTodolist;
  }

  public void setAppPermitTodolist(String appPermitTodolist) {
    this.appPermitTodolist = appPermitTodolist;
  }

  public String getAppPermitDoing() {
    return this.appPermitDoing;
  }

  public void setAppPermitDoing(String appPermitDoing) {
    this.appPermitDoing = appPermitDoing;
  }

  public String getAppPermitDone() {
    return this.appPermitDone;
  }

  public void setAppPermitDone(String appPermitDone) {
    this.appPermitDone = appPermitDone;
  }


}
