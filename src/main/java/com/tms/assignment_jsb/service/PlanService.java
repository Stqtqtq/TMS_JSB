package com.tms.assignment_jsb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.assignment_jsb.entity.Plan;
import com.tms.assignment_jsb.entity.PlanDTO;
import com.tms.assignment_jsb.entity.PlanId;
import com.tms.assignment_jsb.repository.PlanRepository;

@Service
public class PlanService {
 
  @Autowired
  private PlanRepository planRepository;

  // Fetch all plans for a specific app acronym
  public List<Plan> getPlansInfo(String appAcronym) {
    return planRepository.findByIdPlanAppAcronym(appAcronym);
  }

  // Create a new plan
  public Plan createPlan(String appAcronym, String planName, String planStartDate, String planEndDate, String colour) {
    PlanId planId = new PlanId(planName, appAcronym);

    // Check if the plan already exists
    if (planRepository.existsById(planId)) {
        throw new IllegalArgumentException("Plan already exists for the given app acronym.");
    }

    // Create a new plan
    Plan newPlan = new Plan(planId, planStartDate, planEndDate, colour);
    return planRepository.save(newPlan);
  }

  public List<PlanDTO> getFlattenedPlans(String appAcronym) {
    List<Plan> plans = planRepository.findByIdPlanAppAcronym(appAcronym);

    // Map the Plan entity to PlanDTO
    return plans.stream().map(plan -> new PlanDTO(
        plan.getId().getPlanMvpName(),
        plan.getId().getPlanAppAcronym(),
        plan.getPlanStartdate(),
        plan.getPlanEnddate(),
        plan.getPlanColour()
    )).toList();
}
}
