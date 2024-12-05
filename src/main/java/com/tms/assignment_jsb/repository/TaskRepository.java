package com.tms.assignment_jsb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.assignment_jsb.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, String>{

  List<Task> findByTaskAppAcronym(String appAcronym);
  Optional<Task> findByTaskId(String taskId);
}
