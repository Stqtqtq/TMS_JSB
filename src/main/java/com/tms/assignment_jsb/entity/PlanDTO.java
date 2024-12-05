package com.tms.assignment_jsb.entity;

public class PlanDTO {
  
  private String planMvpName;
  private String planAppAcronym;
  private String planStartdate;
  private String planEnddate;
  private String planColour;

  public PlanDTO(String planMvpName, String planAppAcronym, String planStartdate, String planEnddate, String planColour) {
    this.planMvpName = planMvpName;
    this.planAppAcronym = planAppAcronym;
    this.planStartdate = planStartdate;
    this.planEnddate = planEnddate;
    this.planColour = planColour;
  }

  public String getPlanMvpName() {
    return this.planMvpName;
  }

  public void setPlanMvpName(String planMvpName) {
    this.planMvpName = planMvpName;
  }

  public String getPlanAppAcronym() {
    return this.planAppAcronym;
  }

  public void setPlanAppAcronym(String planAppAcronym) {
    this.planAppAcronym = planAppAcronym;
  }

  public String getPlanStartdate() {
    return this.planStartdate;
  }

  public void setPlanStartdate(String planStartdate) {
    this.planStartdate = planStartdate;
  }

  public String getPlanEnddate() {
    return this.planEnddate;
  }

  public void setPlanEnddate(String planEnddate) {
    this.planEnddate = planEnddate;
  }

  public String getPlanColour() {
    return this.planColour;
  }

  public void setPlanColour(String planColour) {
    this.planColour = planColour;
  }

}
