package com.tms.assignment_jsb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tms.assignment_jsb.entity.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String>{

  Optional<Application> findByAppAcronym(String appAcronym);

  Optional<Application> findByAppAcronymAndAppRNumber(String appAcronym, Integer appRNumber);

  @Query("SELECT a FROM Application a WHERE a.appAcronym IN (SELECT t.taskAppAcronym FROM Task t WHERE t.taskId = :taskId)")
  Optional<Application> findByTaskId(String taskId);
}
