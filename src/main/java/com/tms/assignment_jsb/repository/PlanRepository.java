package com.tms.assignment_jsb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.assignment_jsb.entity.Plan;
import com.tms.assignment_jsb.entity.PlanId;

@Repository
public interface PlanRepository extends JpaRepository<Plan, PlanId>{

  List<Plan> findByIdPlanAppAcronym(String planAppAcronym);

  boolean existsById(PlanId id);
}
