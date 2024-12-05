package com.tms.assignment_jsb.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class PlanId implements Serializable{
  
  @Column(name = "plan_mvp_name")
  private String planMvpName;
  @Column(name = "plan_app_acronym")
  private String planAppAcronym;

  public PlanId() {
  }

  public PlanId(String planMvpName, String planAppAcronym) {
    this.planMvpName = planMvpName;
    this.planAppAcronym = planAppAcronym;
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
 
  @Override
  public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      PlanId that = (PlanId) o;
      return Objects.equals(planMvpName, that.planMvpName) &&
              Objects.equals(planAppAcronym, that.planAppAcronym);
  }

  @Override
  public int hashCode() {
      return Objects.hash(planMvpName, planAppAcronym);
  }

  @Override
  public String toString() {
    return "PlanId{" +
            "planMvpName='" + planMvpName + '\'' +
            ", planAppAcronym='" + planAppAcronym + '\'' +
            '}';
  }
}
