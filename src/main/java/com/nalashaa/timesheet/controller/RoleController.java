package com.nalashaa.timesheet.controller;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nalashaa.timesheet.Exception.TimeSheetException;
import com.nalashaa.timesheet.constant.Endpoints;
import com.nalashaa.timesheet.entity.Role;
import com.nalashaa.timesheet.service.IRoleService;

/**
 * @author siva
 *Services for roles
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private static Logger logger = LogManager.getLogger(RoleController.class);
    
    @Autowired 
    IRoleService roleService;
    
    /**
     * @param roleId
     * @return
     * Service to featch roles
     */
    @GetMapping(value = Endpoints.GET_ROLES_BY_PARENT_ROLE)
    public ResponseEntity<List<Role>> getRoles(@PathVariable("roleId") long roleId )
    {
        logger.info("Entered : findProject " + roleId );
        if(roleId <= 0){
            throw new TimeSheetException("Illegal arguements passed");
        }
        
        return new ResponseEntity<List<Role>>(roleService.getRolesByParentRole(roleId),HttpStatus.OK);
    }
    
    /**
     * @return
     * Service to fetch all roles
     */
    @GetMapping(value = Endpoints.GET_ALL_ROLES)
    public ResponseEntity<List<Role>> getAllRoles()
    {
        logger.info("Entered : getAllRoles " );
        return new ResponseEntity<List<Role>>(roleService.getAllRoles(),HttpStatus.OK);
    }
     
}
