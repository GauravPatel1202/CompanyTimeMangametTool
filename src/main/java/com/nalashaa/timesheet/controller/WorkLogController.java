package com.nalashaa.timesheet.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nalashaa.timesheet.Exception.TimeSheetException;
import com.nalashaa.timesheet.constant.Endpoints;
import com.nalashaa.timesheet.entity.Worklog;
import com.nalashaa.timesheet.entity.WorklogStatus;
import com.nalashaa.timesheet.model.PersonDTO;
import com.nalashaa.timesheet.model.TimeSheet;
import com.nalashaa.timesheet.model.WorkLog;
import com.nalashaa.timesheet.service.IUserService;
import com.nalashaa.timesheet.service.IWorkLogService;
import com.nalashaa.timesheet.util.GenericResponseDataBlock;
import com.nalashaa.timesheet.util.GenericResponseGenerator;


/**
 * @author siva
 * work log related services
 */
@RestController
@RequestMapping("/worklog")
public class WorkLogController {

    private static Logger logger = LogManager.getLogger(WorkLogController.class);
   
    @Autowired 
    IWorkLogService workLogService;
    
    @Autowired
    IUserService userService;
    
    
    /**
     * @param personId
     * @param startDate
     * @param endDate
     * @return
     * 
     * Service to fetch the timesheet for a person
     */
    @GetMapping(value = Endpoints.GET_TIMESHEET_BY_PERSON)
    public ResponseEntity<WorkLog> getTimesheetByPerson(@PathVariable("loggedInUserId") long loggedInUserId,@PathVariable("personId") long personId, @PathVariable("startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @PathVariable("endDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate )
    {
        logger.info("Entered : getTimesheetByPerson " + personId );
        if(0>loggedInUserId ||0>personId || startDate == null || endDate == null){
            throw new TimeSheetException("Illegal arguements passed");
        }
        return new ResponseEntity<WorkLog>(workLogService.getTimeSheetByPerson(loggedInUserId,personId,startDate,endDate),HttpStatus.OK);
    }
    
    /**
     * @param timesheet
     * @return
     * Service to save the timesheet
     */
    @PostMapping(value = Endpoints.SAVE_TIMESHEET)
    public ResponseEntity<List<Worklog>> saveTimeSheet(@RequestBody List<Worklog> timesheet,@PathVariable("loggedInUserId") String personId)
    {
        logger.info("Entered : saveTimeSheet"  );
        return new ResponseEntity<List<Worklog>>(workLogService.saveTimeSheet(timesheet,personId),HttpStatus.OK);
    }
    
    /**
     * @param timesheet
     * @return
     * Service to approve the timesheet
     */
    @PostMapping(value = Endpoints.APPROVE_TIMESHEET)
    public ResponseEntity<GenericResponseDataBlock> approveTimeSheet(@RequestBody TimeSheet timesheet)
    {
        logger.info("Entered : approveTimeSheet"  );
        workLogService.approveTimeSheet(timesheet);
        logger.info("Approved timesheet successfully");
        return GenericResponseGenerator.getGenericResponse("Success", true, 200, HttpStatus.OK);
    }
    
    /**
     * @param timesheet
     * @return
     * Service to approve the timesheet
     */
    @PostMapping(value = Endpoints.TIMESHEET_STATUS)
    public ResponseEntity<GenericResponseDataBlock> timeSheetStatus(@RequestBody TimeSheet timesheet)
    {
        logger.info("Entered : timeSheetStatus"  );
        workLogService.timeSheetStatus(timesheet);
        logger.info("Approved timesheet successfully");
        return GenericResponseGenerator.getGenericResponse("Success", true, 200, HttpStatus.OK);
    }
    
    /**
     * @param 
     * @return
     * Service to get worklog status
     */
    @GetMapping(value = Endpoints.GET_WORKLOG_STATUS)
    public ResponseEntity<List<WorklogStatus>> getWoklogStatus()
    {
        logger.info("Entered : getWoklogStatus"  );
        return new ResponseEntity<List<WorklogStatus>>(workLogService.getWoklogStatus(),HttpStatus.OK);
    }

    /**
     * @param loggedInUserId
     * @param startDate
     * @param endDate
     * @return
     * 
     * Service to fetch the timesheet for all the resources reporting to the approver.
     */
    @GetMapping(value = Endpoints.GET_TIMESHEET_FOR_BULK_APPROVAL)
    public ResponseEntity<List<WorkLog>> getTimesheetForBulkApproval(@PathVariable("loggedInUserId") long loggedInUserId, @PathVariable("startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @PathVariable("endDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate )
    {
        logger.info("Entered : getTimesheetForBulkApproval " + loggedInUserId);
        if(0>loggedInUserId || startDate == null || endDate == null){
            throw new TimeSheetException("Illegal arguements passed");
        }
        List<PersonDTO> reporteeList = userService.getUsersByProjectAndRole(loggedInUserId);
        return new ResponseEntity<List<WorkLog>>(workLogService.getWorkLogforUsers(loggedInUserId, reporteeList, startDate, endDate),HttpStatus.OK);
    }
    
    /**
     * @param loggedInUserId
     * @param startDate
     * @param endDate
     * @return
     * 
     * Service to fetch the timesheet for all the resources reporting to the approver.
     */
    @GetMapping(value = Endpoints.GET_REPORT_SUMMARY)
    public ResponseEntity<List<WorkLog>> getReportSummary(@PathVariable("loggedInUserId") long loggedInUserId, @PathVariable("projectId") long projectId, @PathVariable("startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @PathVariable("endDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate )
    {
    	logger.info("Entered : getTimesheetForBulkApproval " + loggedInUserId);
        if(0>loggedInUserId || startDate == null || endDate == null){
            throw new TimeSheetException("Illegal arguements passed");
        }
        List<PersonDTO> reporteeList = userService.getUsersForReportSummary(loggedInUserId, projectId, startDate, endDate);
        return new ResponseEntity<List<WorkLog>>(workLogService.getWorkLogforUsers(loggedInUserId, reporteeList, startDate, endDate),HttpStatus.OK);
    }
    
    @GetMapping(value = Endpoints.GET_PMO_REPORT)
    public ResponseEntity<List<Worklog>> getPMOReport(@PathVariable("loggedInUserId") long loggedInUserId, @PathVariable("projectId") long projectId, @PathVariable("startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @PathVariable("endDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate ){
    	List<PersonDTO> reporteeList = userService.getUsersForReportSummary(loggedInUserId, projectId, startDate, endDate);
    	List<Long> userIdList = reporteeList.stream().map(user -> user.getId()).collect(Collectors.toList());
    	return new ResponseEntity<List<Worklog>>(workLogService.getPMOReport(userIdList, startDate, endDate),HttpStatus.OK);
    }
    
    @PostMapping(value = Endpoints.DELETE_TIMESHEET_ENTRIES)
    public ResponseEntity<GenericResponseDataBlock> deleteTimesheetEntries(@RequestBody List<Long> entryIdList)
    {
    	logger.info("Entered : deleteTimesheetEntries " );
        try {
	        workLogService.deleteEntries(entryIdList);
	        logger.info("Deleted Entries Successfully ");
	        return GenericResponseGenerator.getGenericResponse("Success", true, 200, HttpStatus.OK);
        }catch(Exception tse) {
        	logger.error("EXCEPTION: Failure in deleting entries and reason is: " + tse.getMessage());
        	return GenericResponseGenerator.getGenericResponse(tse.getMessage(), false, 400, HttpStatus.BAD_REQUEST);
        }
    }
}
