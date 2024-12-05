package com.tms.assignment_jsb.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.assignment_jsb.entity.Application;
import com.tms.assignment_jsb.repository.ApplicationRepository;
import com.tms.assignment_jsb.repository.UserGroupsRepository;

@Service
public class ApplicationService {

  @Autowired
  ApplicationRepository applicationRepository;

  @Autowired
  UserGroupsRepository userGroupsRepository;
  
  public boolean isValidAppAcronym(String app_acronym) {
    return app_acronym.matches("^[a-zA-Z0-9]{1,50}$");
  }

  public boolean isValidAppRNumber(String app_rnumber) {
    return app_rnumber.matches("^(0|[1-9]\\d*)$");
  }

  public List<Application> getAllApplications() {
    return applicationRepository.findAll();
  }

  public Map<String, Object> getAppsInfo(String currentUser, boolean isPL) {
    List<String> distinctGroups = userGroupsRepository.findDistinctGroups();
    List<Application> allApps = applicationRepository.findAll();
    return Map.of("apps", allApps, "groups", distinctGroups, "currentUser", currentUser, "isPL", isPL);
  }

  public void createApp(
    String appAcronym, 
    Integer appRNumber, 
    String appStartDate, 
    String appEndDate, 
    String appPermitCreate, 
    String appPermitOpen, 
    String appPermitTodoList, 
    String appPermitDoing, 
    String appPermitDone, 
    String appDescription
  ) {
      // Validation
      if (appAcronym == null || appAcronym.isEmpty()) {
          throw new IllegalArgumentException("App acronym is required.");
      }
      if (appRNumber == null || appRNumber < 0) {
          throw new IllegalArgumentException("Invalid RNumber.");
      }
      if (appStartDate == null || appStartDate.isEmpty()) {
          throw new IllegalArgumentException("Start date is required.");
      }
      if (appEndDate == null || appEndDate.isEmpty()) {
          throw new IllegalArgumentException("End date is required.");
      }
      if (appDescription != null && appDescription.length() > 255) {
          throw new IllegalArgumentException("Description too long.");
      }

      // Check if application already exists
      Optional<Application> existingApp = applicationRepository.findByAppAcronym(appAcronym);
      if (existingApp.isPresent()) {
          throw new IllegalArgumentException("App acronym already exists.");
      }

      // Create and save the application
      Application newApp = new Application(
          appAcronym, appDescription, appRNumber, appStartDate, appEndDate, 
          appPermitCreate, appPermitOpen, appPermitTodoList, appPermitDoing, appPermitDone
      );

      applicationRepository.save(newApp);
  }

  public void updateApp(
    String appAcronym, 
    Integer appRNumber, 
    String appStartDate, 
    String appEndDate, 
    String appPermitCreate, 
    String appPermitOpen, 
    String appPermitTodoList, 
    String appPermitDoing, 
    String appPermitDone, 
    String appDescription
  ) {
      // Validation
      if (appDescription != null && appDescription.length() > 255) {
          throw new IllegalArgumentException("Description too long.");
      }

      // Find existing application
      Optional<Application> existingApp = applicationRepository.findByAppAcronymAndAppRNumber(appAcronym, appRNumber);
      if (existingApp.isEmpty()) {
          throw new IllegalArgumentException("App not found or no changes made.");
      }

      // Update fields
      Application appToUpdate = existingApp.get();
      appToUpdate.setAppStartdate(appStartDate);
      appToUpdate.setAppEnddate(appEndDate);
      appToUpdate.setAppPermitCreate(appPermitCreate);
      appToUpdate.setAppPermitOpen(appPermitOpen);
      appToUpdate.setAppPermitTodolist(appPermitTodoList);
      appToUpdate.setAppPermitDoing(appPermitDoing);
      appToUpdate.setAppPermitDone(appPermitDone);
      appToUpdate.setAppDescription(appDescription);

      // Save updated application
      applicationRepository.save(appToUpdate);
  }

  public Integer getMaxRNumber(String appAcronym) {
    Application application = applicationRepository.findByAppAcronym(appAcronym)
      .orElseThrow(() -> new IllegalArgumentException("Application not found"));
    return application.getAppRNumber();
  }

  @Transactional
  public void updateRNumber(String appAcronym, Integer newRNumber) {
    Application application = applicationRepository.findByAppAcronym(appAcronym)
        .orElseThrow(() -> new IllegalArgumentException("Application not found"));
    application.setAppRNumber(newRNumber);
    applicationRepository.save(application);
  }

  public Optional<Application> findByAppAcronym(String appAcronym) {
    // Fetch the application entity based on the appAcronym
    return applicationRepository.findByAppAcronym(appAcronym);
  }
}

