package com.nalashaa.timesheet.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nalashaa.timesheet.entity.Holiday;
import com.nalashaa.timesheet.repository.IHolidayDAO;
import com.nalashaa.timesheet.service.IHolidayService;

/**
 * This class is used to provide the implementations of all Holiday's services.
 * @author nandeeshp
 *
 */
@Service
@Transactional
public class HolidayServiceImpl implements IHolidayService {
	private static final Logger logger = LogManager.getLogger(ClientServiceImpl.class);
	
	@Autowired
	IHolidayDAO holidayRepo;

	/**
	 * @param String year
	 * @return List<Holiday>
	 * 
	 * This method is used to fetch the list of all holidays in a given year.
	 */
	@Override
	public List<Holiday> fetchHolidaysOfYear(String year) {
		logger.info("Entered : fetchHolidaysOfYear service");
		try {
			List<Holiday> lstHoliday = holidayRepo.findByYearOrderByHolidayDate(year);
			return lstHoliday;
		}catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * @param List<Holiday> lstHoliday
	 * @param String year
	 * @param String personId
	 * @return List<Holiday>
	 * 
	 * This method replaces all the holidays of a given year with the new list of holidays and returns the updated list.
	 */
	@Override
	public List<Holiday> updateHolidaysOfYear(List<Holiday> lstHoliday, String year, String personId) {
		logger.info("entered : updateHolidaysOfYear");
		try {
			List<Holiday> updatedHolidayLst = new ArrayList<Holiday>();
			holidayRepo.deleteByYear(year);
			for (Holiday holiday : lstHoliday) {
				holiday.setCreatedDate(new Date());
				holiday.setLastUpdatedTime(new Date());
				holiday.setCreatedBy(personId);
				holiday.setLastUpdatedBy(personId);
				holiday = holidayRepo.save(holiday);
				updatedHolidayLst.add(holiday);
			}
			logger.info("holiday list saved.");
			return updatedHolidayLst;
		}catch (Exception e) {
			throw e;
		}
	}

}
