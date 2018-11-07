/**
 * 
 */
package com.nalashaa.timesheet.controller;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nalashaa.timesheet.service.IPurgeOldDataService;
import com.nalashaa.timesheet.util.GenericResponseDataBlock;
import com.nalashaa.timesheet.util.GenericResponseGenerator;

/**
 * @author somadutta
 *
 */
@RestController
@RequestMapping("/purgeOldData")
public class PurgeOldDataController {
	
	@Autowired
	IPurgeOldDataService purgeDataService;
	
	private static Logger logger = LogManager.getLogger(PurgeOldDataController.class);

	@PostMapping(value="/")
	public ResponseEntity<GenericResponseDataBlock> deleteTimesheetEntries()
    {
    	logger.info("Entered : purgeOldData " );
        try {
        	purgeDataService.purgeOldData();
	        logger.info("Deleted Entries Successfully ");
	        return GenericResponseGenerator.getGenericResponse("Success", true, 200, HttpStatus.OK);
        }catch(Exception tse) {
        	logger.error("EXCEPTION: Failure in deleting entries and reason is: " + tse.getMessage());
        	tse.printStackTrace();
        	return GenericResponseGenerator.getGenericResponse(tse.getMessage(), false, 400, HttpStatus.BAD_REQUEST);
        }
    }
}
