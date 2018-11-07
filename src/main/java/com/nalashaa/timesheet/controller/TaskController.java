package com.nalashaa.timesheet.controller;

import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nalashaa.timesheet.constant.Endpoints;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.model.TaskSetup;
import com.nalashaa.timesheet.service.ITaskService;
import com.nalashaa.timesheet.util.GenericResponseDataBlock;
import com.nalashaa.timesheet.util.GenericResponseGenerator;

/**
 * @author siva
 * Task related services
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    private static Logger logger = LogManager.getLogger(TaskController.class);
    
    @Autowired 
    ITaskService taskService;
    
    @GetMapping(value = Endpoints.GET_TASKS_BY_PROJECT)
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable("projectId") long projectId)
    {
        logger.info("Entered : getTasksByProject " );
        return new ResponseEntity<List<Task>>(taskService.getTasksByProject(projectId),HttpStatus.OK);
    }
        
    @GetMapping(value = Endpoints.GET_TASK_SETUP)
    public ResponseEntity<TaskSetup> getTaskSetup(@PathVariable long taskId)
    {
        logger.info("Entered : getTaskSetup " );
        return new ResponseEntity<TaskSetup>(taskService.getTaskSetup(taskId),HttpStatus.OK);
    }
    
    /*Below methods are functional as of now according to the FSD*/
    
    /**
     * 
     */
    @DeleteMapping(value = Endpoints.DELETE_TASK)
    public ResponseEntity<GenericResponseDataBlock> deleteTask(@PathVariable long taskId)
    {
        logger.info("Entered : deleteTask " );
        try {
        taskService.deleteTask(taskId);
        logger.info("Deleted Task Successfully ");
        return GenericResponseGenerator.getGenericResponse("Success", true, 200, HttpStatus.OK);
        }catch(Exception tse) {
        	logger.error("EXCEPTION: Failure in deleting task and reason is: " + tse.getMessage());
        	return GenericResponseGenerator.getGenericResponse(tse.getMessage(), false, 400, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 
     * @param taskId
     * @return
     */
    @GetMapping(value = Endpoints.GET_TASK_BY_TASK_ID)
    public ResponseEntity<Task> getTask(@PathVariable("id") long taskId)
    {
        logger.info("Entered : getTask " );
        return new ResponseEntity<Task>(taskService.getTask(taskId),HttpStatus.OK);
    }
        
    /**
     * @return
     * Service to fetch list of tasks
     */
    @GetMapping(value = Endpoints.GET_TASKS_BY_PERSON_ID)
    public ResponseEntity<List<Task>> getTasks(@PathVariable("personId") long personId)
    {
        logger.info("Entered : getTasks By Person" );
        return new ResponseEntity<List<Task>>(taskService.getTasks(personId),HttpStatus.OK);
    }
    
    /**
     * 
     * @param taskSetup
     * @return
     */
    @PostMapping(value = Endpoints.CREATE_TASK_SETUP)
    public ResponseEntity<GenericResponseDataBlock> taskSetup(@RequestBody Task task)
    {
        logger.info("Entered : taskSetup " );
        try {
        	taskService.taskSetup(task);
        	logger.info("TaskSetup is done successfully." );
        	return GenericResponseGenerator.getGenericResponse("Success", true, 200, HttpStatus.OK);
        }
        catch (Exception e){
        	logger.error("EXCEPTION: Failure in taskSetup and reason is: " + e.getMessage());
        	return GenericResponseGenerator.getGenericResponse(e.getMessage(), false, 400, HttpStatus.BAD_REQUEST);
        }
      
    }
    
    /**
     * @param personId
     * @return
     */
    @PutMapping(value = Endpoints.UPDATE_TASK)
    public ResponseEntity<GenericResponseDataBlock> updateTask(@RequestBody Task task)
    {
        logger.info("Entered : update Task");
        try {
        	taskService.updateTask(task);
        	logger.info("Task is updated successfully." );
        	return GenericResponseGenerator.getGenericResponse("Success", true, 200, HttpStatus.OK);
        } catch (Exception e){
        	return GenericResponseGenerator.getGenericResponse(e.getMessage(), false, 400, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 
     * @param projectId
     * @param personId
     * @param monthStartDate
     * @return
     */
    @GetMapping(value = Endpoints.GET_TASKS_BY_PROJECT_AND_PERSON_AND_MONTH_START_DATE)
    public ResponseEntity<List<Task>> getTasksByProjectAndPerson(@PathVariable("projectId") long projectId,@PathVariable("personId") long personId,@PathVariable("monthStartDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date monthStartDate)
    {
        logger.info("Entered : getTasksByProjectAndPerson " );
        return new ResponseEntity<List<Task>>(taskService.getTasksByProjectAndPerson(projectId,personId,monthStartDate),HttpStatus.OK);
    } 
     
    /**
     * 
     * @param personId
     * @return
     */
    @GetMapping(value = Endpoints.GET_PROJECT_BY_ROLE)
    public ResponseEntity<List<Project>> getProjectByRole(@PathVariable("personId") long personId)
    {
        logger.info("Entered : getProjectByPersonAndRole " );
        return new ResponseEntity<List<Project>>(taskService.getProjectBycreateTaskRole(personId),HttpStatus.OK);
    }
}
