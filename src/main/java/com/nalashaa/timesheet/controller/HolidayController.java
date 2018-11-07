package com.nalashaa.timesheet.controller;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nalashaa.timesheet.Exception.TimeSheetException;
import com.nalashaa.timesheet.constant.Endpoints;
import com.nalashaa.timesheet.entity.Holiday;
import com.nalashaa.timesheet.service.IHolidayService;

@RestController
@RequestMapping("/holidaylist")
public class HolidayController {
	private static Logger logger = LogManager.getLogger(ClientController.class);
	
	@Autowired
	IHolidayService holidayService;
	
	/**
	 * 
	 * @param year
	 * @return List<Holiday>
	 * 
	 * This method is used to fetch the list of all holidays in a given year.
	 */
	@GetMapping(value = Endpoints.GET_HOLIDAY_LIST)
	public ResponseEntity<List<Holiday>> fetchHolidayList(@PathVariable("year") String year) {
		logger.info("Entered : get holiday list");
		try {
			return new ResponseEntity<List<Holiday>>(holidayService.fetchHolidaysOfYear(year),HttpStatus.OK);
		}catch (Exception e) {
			throw new TimeSheetException("Could not fetch the holidays. Please try again later");
		}
	}
	
	/**
	 * 
	 * @param lstHoliday
	 * @param year
	 * @param personId
	 * @return List<Holiday>
	 * 
	 * This method replaces all the holidays of a given year with the new list of holidays and returns the updated list.
	 */
	@PostMapping(value = Endpoints.UPDATE_HOLIDAY_LIST)
	public ResponseEntity<List<Holiday>> updateHolidayList(@RequestBody List<Holiday> lstHoliday, @PathVariable("year") String year,  @PathVariable("personId") String personId){
		logger.info("Entered : update holiday list");
		try {
			return new ResponseEntity<List<Holiday>>(holidayService.updateHolidaysOfYear(lstHoliday, year, personId),HttpStatus.OK);
		}catch (Exception e) {
			throw new TimeSheetException("Could not update the holidays. Please try again later");
		}
	}
}
