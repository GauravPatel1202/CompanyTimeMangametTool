package com.nalashaa.timesheet.controller;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nalashaa.timesheet.constant.Endpoints;
import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.model.PersonDTO;
import com.nalashaa.timesheet.service.IUserService;
import com.nalashaa.timesheet.util.GenericResponseDataBlock;
import com.nalashaa.timesheet.util.GenericResponseGenerator;

/**
 * Services for login
 */
@RestController
@RequestMapping("/timesheetLogin")
public class LoginController {

    private static Logger logger = LogManager.getLogger(LoginController.class);
    
    @Autowired
    private IUserService userService;
    
    /**
     * This method is used to do login
     * @param person
     * @return
     */
   // @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = Endpoints.LOGIN)
    public ResponseEntity<PersonDTO> login(@RequestBody PersonDTO personDTO)
    {
        logger.info("Entered : login " );
        return new ResponseEntity<PersonDTO>(userService.login(personDTO.getEmpId(), personDTO.getPassword()),HttpStatus.OK);
    }
    
    /**
     *  This method is used to change the password for provided empId.
     * @param person
     * @return
     */
    @PostMapping(value = Endpoints.CHANGE_PASSWORD)
    public ResponseEntity<GenericResponseDataBlock> changePassword(@RequestBody Person person)
    {
        logger.info("Entered : change password");
        userService.changePassword(person.getEmpId(),person.getPassword(),person.getNewPassword());
        return GenericResponseGenerator.getGenericResponse("Success", true, 200,HttpStatus.OK);
    }

    /**
	 * This method is responsible to generate the new password,in case user forget his/her password.
	 * @param empId`
	 *        	It represents the user id, who has forgotton his/her password. 
	 */
    @GetMapping(value = Endpoints.FORGET_PASSWORD)
    public ResponseEntity<GenericResponseDataBlock> forgetPassword(@RequestParam("empId") String empId)
    {
        logger.info("Entered : forget password : EMPID: "+empId );
        userService.forgetPassword(empId);
        return GenericResponseGenerator.getGenericResponse("Your password reset request sent successfully to you registered mail Id.", true, 200, HttpStatus.OK);
    }
    
}
