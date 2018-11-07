package com.nalashaa.timesheet.controller;

import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nalashaa.timesheet.Exception.TimeSheetException;
import com.nalashaa.timesheet.constant.Endpoints;
import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Role;
import com.nalashaa.timesheet.service.IProjectService;
import com.nalashaa.timesheet.util.GenericResponseDataBlock;
import com.nalashaa.timesheet.util.GenericResponseGenerator;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private static Logger logger = LogManager.getLogger(ProjectController.class);
      
    @Autowired 
    IProjectService projectService;
    
    /**
     * @param personId
     * @return
     * Service to fetch all projects that the person is mapped
     */
    @GetMapping(value = Endpoints.GET_PROJECTS_BY_PERSON)
    public ResponseEntity<List<Project>> getProjectsByPerson(@PathVariable("personId") long personId )
    {
        logger.info("Entered : getProjectsByPerson " + personId );
        return new ResponseEntity<List<Project>>(projectService.getProjectsByPerson(personId),HttpStatus.OK);
    }
    
    /**
     * @param personId
     * @return
     * Service to fetch all projects that the person is mapped
     */
    @GetMapping(value = Endpoints.GET_PROJECT_BY_PERSON_AS_MANAGER)
    public ResponseEntity<List<Project>> getProjectsByPersonAndRole(@PathVariable("personId") long personId )
    {
        logger.info("Entered : getProjectsByPerson " + personId );
        return new ResponseEntity<List<Project>>(projectService.getProjectsByPersonAndRole(personId),HttpStatus.OK);
    }
    
    /**
     * @param projectSetup
     * @return
     */
    @PostMapping(value = Endpoints.CREATE_PROJECT)
    public ResponseEntity<GenericResponseDataBlock> createProject(@RequestBody Project project)
    {
        logger.info("Entered : createProject " );
        projectService.createProject(project);
        return GenericResponseGenerator.getGenericResponse("Success", true, 200, HttpStatus.OK);
    }
    
    
    /**
     * @param personId
     * @return
     */
    @PutMapping(value = Endpoints.UPDATE_PROJECT)
    public ResponseEntity<GenericResponseDataBlock> updateProject(@RequestBody Project project)
    {
        logger.info("Entered : update project");
        try {
        	projectService.updateProject(project);
        	logger.info("Entered : Project updated successfully");
        	return GenericResponseGenerator.getGenericResponse("Success", true, 200, HttpStatus.OK);
        } catch (Exception e){
        	logger.error("EXCEPTION : Failure to update project details. Reason :"+e.getMessage());
        	return GenericResponseGenerator.getGenericResponse(e.getMessage(), true, 500, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * @param personId
     * @return
     */
    @GetMapping(value = Endpoints.GET_PROJECT_BY_PERSON_ACTIVE_WITH_TASKS)
    public ResponseEntity<List<Project>> getProjectsByPersonActiveWithTasks(@PathVariable("personId") long personId ,@PathVariable("monthStartDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date monthStartDate)
    {
        logger.info("Entered : getProjectsByPersonActiveWithTasks " + personId );
        return new ResponseEntity<List<Project>>(projectService.getProjectsByPersonActiveWithTasks(personId,monthStartDate),HttpStatus.OK);
    }
    
    /**
     * 
     * @param personId
     * @return
     */
    @GetMapping(value = Endpoints.GET_TIMESHEET_APPROVABLE_PROJECTS)
    public ResponseEntity<List<Project>> getApprovableProjects(@PathVariable("personId") long personId){
    	logger.info("Entered : getProjectsByPersonActiveWithTasks " + personId );
    	return new ResponseEntity<List<Project>>(projectService.getTimesheetApprovableProjects(personId),HttpStatus.OK);
    }
    
    @GetMapping(value = Endpoints.GET_ROLE_IN_PROJECT)
    public long getRoleInProject(@PathVariable long projectId, @PathVariable long personId){
    	return projectService.getRoleInProject(projectId, personId);
    }
}
