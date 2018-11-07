package com.nalashaa.timesheet.controller;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import com.nalashaa.timesheet.entity.AssignResource;
import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.model.PersonDTO;
import com.nalashaa.timesheet.model.Resource;
import com.nalashaa.timesheet.service.IUserService;
import com.nalashaa.timesheet.util.GenericResponseDataBlock;
import com.nalashaa.timesheet.util.GenericResponseGenerator;

/**
 * @author vijayganesh
 * User related services
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LogManager.getLogger(UserController.class);
    
    @Autowired 
    IUserService userService;
    
    @Autowired
    MessageSource messageSource;
    
    /**
     * @param person
     * @return
     * Service to create user
     */
    @PostMapping(value = Endpoints.CREATE_USER)
    public ResponseEntity<Person> createUser(@RequestBody Person person)
    {
        logger.info("Entered : createUser"  );
        return new ResponseEntity<Person>(userService.createUser(person),HttpStatus.OK);
    }
    
    
    /**
     * @return
     */
    @GetMapping(value = Endpoints.GET_LIST_OF_RESOURCES)
    public ResponseEntity<List<Person>> getListOfResources(){
    	
    	return new ResponseEntity<List<Person>>(userService.getListOfResources(),HttpStatus.OK);
    }
    
    /**
     * @param personId
     * @return
     */
    @PutMapping(value = Endpoints.UPDATE_USER)
    public ResponseEntity<GenericResponseDataBlock> updateUser(@RequestBody Person person)
    {
        logger.info("Entered : update User");
        userService.updateUser(person);
        return GenericResponseGenerator.getGenericResponse("Success", true, 200, HttpStatus.OK);
    }
    
    /**
     * @return
     */
    @GetMapping(value = Endpoints.GET_DESTINATION_AND_SKILL)
    public ResponseEntity<Resource> getDesignationAndSkill(){
    	
    	return new ResponseEntity<Resource>(userService.getDesignationAndSkill(),HttpStatus.OK);
    }
    
    /**
     * @return
     */
    @GetMapping(value = Endpoints.GET_PROJECTS_AND_RESOURCES)
    public ResponseEntity<Resource> getProjectAndResources(@PathVariable("userId") Long userId){
    	return new ResponseEntity<Resource>(userService.getProjectAndAssignedResources(userId),HttpStatus.OK);
    }
    
    /**
     * @param assignResource
     * @return
     */
    @PostMapping(value = Endpoints.ASSIGN_PROJECT_AND_RESOURCE)
    public ResponseEntity<Resource> assignProjectAndResource(@PathVariable("userId") Long userId,@RequestBody List<AssignResource> assignResource){
    	
    	logger.info("Entered : assignresource"  );
    	for(AssignResource assgnresfromserver:assignResource) {
    		if(assgnresfromserver.isResourceAllocation()) {
    			if(!userService.getAssignResourceByPerson(assgnresfromserver.getPersonId()).isEmpty()) {
    				Resource res = new Resource();
    				GenericResponseDataBlock response = new GenericResponseDataBlock();
    				response.setMessage(messageSource.getMessage("assignresource.allocated.partial", null, null));
    				res.setGenericResponseData(response);
    				return new ResponseEntity<Resource>(res,HttpStatus.BAD_REQUEST);
    			}
    		}
    	}
    	return new ResponseEntity<Resource>(userService.assignProjectAndResource(userId,assignResource),HttpStatus.OK);
    }
    
    /**
     * @param assignResource
     * @return
     */
    @PutMapping(value = Endpoints.UPDATE_ASSIGN_PROJECT_AND_RESOURCE)
    public ResponseEntity<Resource> updateAssignProjectAndResource(@PathVariable("userId") Long userId,@RequestBody List<AssignResource> assignResource){
    	
    	logger.info("Entered : modify assignresource"  );
    	for(AssignResource assgnresfromserver:assignResource) {
    		for(AssignResource as : userService.getAssignResourceByPerson(assgnresfromserver.getPersonId()))    			
    			if((!assgnresfromserver.getProjectId().equals(as.getProjectId())) && 
    				assgnresfromserver.isResourceAllocation())
    				{
    					Resource res = new Resource();
    					GenericResponseDataBlock response = new GenericResponseDataBlock();
    					response.setMessage(messageSource.getMessage("assignresource.allocated.partial", null, null));
    					res.setGenericResponseData(response);
    					return new ResponseEntity<Resource>(res,HttpStatus.BAD_REQUEST);
    				}
    	}
    	return new ResponseEntity<Resource>(userService.assignProjectAndResource(userId,assignResource),HttpStatus.OK);
    }
    
    /**
     * @param assignResource
     * @return
     */
    @DeleteMapping(value = Endpoints.DELETE_ASSIGNED_RESOURCE)
    public ResponseEntity<Resource> unAssignProjectAndResource(@PathVariable("userId") Long userId,@RequestBody List<AssignResource> assignResource){
    	
    	return new ResponseEntity<Resource>(userService.unAssignProjectAndResource(userId,assignResource),HttpStatus.OK);
    }

    /**
     * @return
     * Service to fetch all users by logged in user role and projects mapped 
     */
    @GetMapping(value = Endpoints.GET_USERS_BY_LOGGED_IN_USER_ROLE)
    public ResponseEntity<List<PersonDTO>> getUsersByLoggedInUserRole(@PathVariable("userId") long userId)
    {
        logger.info("Entered : getUsersByProjectAndRole ");
        return new ResponseEntity<List<PersonDTO>>(userService.getUsersByProjectAndRole(userId),HttpStatus.OK);
    }

}
