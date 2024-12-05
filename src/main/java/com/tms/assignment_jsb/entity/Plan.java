package com.tms.assignment_jsb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "plan")
public class Plan {
  
  @EmbeddedId
  private PlanId id;

  @Column(name = "plan_startdate")
  private String planStartdate;
  @Column(name = "plan_enddate")
  private String planEnddate;
  @Column(name = "plan_colour")
  private String planColour;

  public Plan() {}

  public Plan(PlanId id, String planStartdate, String planEnddate, String planColour) {
    this.id = id;
    this.planStartdate = planStartdate;
    this.planEnddate = planEnddate;
    this.planColour = planColour;
  }

  public PlanId getId() {
    return this.id;
  }

  public void setId(PlanId id) {
    this.id = id;
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

  @Override
  public String toString() {
    return "Plan{" +
            "id=" + id +
            ", planStartdate='" + planStartdate + '\'' +
            ", planEnddate='" + planEnddate + '\'' +
            ", planColour='" + planColour + '\'' +
            '}';
  }
}
