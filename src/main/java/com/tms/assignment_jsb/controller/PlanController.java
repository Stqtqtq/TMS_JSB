package com.tms.assignment_jsb.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import com.tms.assignment_jsb.entity.Plan;
import com.tms.assignment_jsb.entity.PlanDTO;
import com.tms.assignment_jsb.service.PlanService;

@Controller
public class PlanController {
  
  @Autowired
  private PlanService planService;

  // Get all plans for an app
  @PostMapping("/getPlansInfo")
  public ResponseEntity<?> getPlansInfo(@RequestBody Map<String, String> request, @RequestAttribute("isPM") boolean isPM) {
      String appAcronym = request.get("appAcronym");

      if (appAcronym == null || appAcronym.isEmpty()) {
          return ResponseEntity.badRequest().body(Map.of("message", "Invalid app acronym", "success", false));
      }

      try {
        //   List<Plan> plans = planService.getPlansInfo(appAcronym);
        List<PlanDTO> plans = planService.getFlattenedPlans(appAcronym);
          return ResponseEntity.ok(Map.of("plans", plans, "success", true, "isPM", isPM));
      } catch (Exception e) {
          e.printStackTrace();
          return ResponseEntity.status(500).body(Map.of("message", "Server error", "success", false));
      }
  }

  // Create a new plan
  @PostMapping("/createPlan")
  public ResponseEntity<?> createPlan(@RequestBody Map<String, String> request, @RequestAttribute("isPM") boolean isPM) {
      if (!isPM) {
          return ResponseEntity.status(403).body(Map.of("message", "Forbidden", "success", false));
      }

      String appAcronym = request.get("appAcronym");
      String planName = request.get("planName");
      String planStartDate = request.get("planStartDate");
      String planEndDate = request.get("planEndDate");
      String colour = request.get("colour");

      if (planName == null || !planName.matches("^[a-zA-Z0-9\\s]{1,50}$")) {
          return ResponseEntity.badRequest().body(Map.of("message", "Invalid plan name", "success", false));
      }
      if (planStartDate == null || planStartDate.isEmpty()) {
          return ResponseEntity.badRequest().body(Map.of("message", "Invalid start date", "success", false));
      }
      if (planEndDate == null || planEndDate.isEmpty()) {
          return ResponseEntity.badRequest().body(Map.of("message", "Invalid end date", "success", false));
      }
      if (colour == null || colour.isEmpty()) {
          return ResponseEntity.badRequest().body(Map.of("message", "Invalid colour selected", "success", false));
      }

      try {
          Plan newPlan = planService.createPlan(appAcronym, planName, planStartDate, planEndDate, colour);
          return ResponseEntity.status(201).body(Map.of("message", "Plan created successfully.", "plan", newPlan, "success", true));
      } catch (IllegalArgumentException e) {
          return ResponseEntity.status(409).body(Map.of("message", e.getMessage(), "success", false));
      } catch (Exception e) {
          e.printStackTrace();
          return ResponseEntity.status(500).body(Map.of("message", "An error occurred while creating the plan.", "success", false));
      }
  }
}
