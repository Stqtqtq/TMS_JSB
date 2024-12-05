package com.tms.assignment_jsb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tms.assignment_jsb.entity.Accounts;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, String>{
  // Boolean findyIsActiveByUsername(String username);
  Accounts findByUsername(String username);

  @Query("SELECT a.email FROM Accounts a WHERE a.username = :username")
  Optional<String> findEmailByUsername(String username);
}
