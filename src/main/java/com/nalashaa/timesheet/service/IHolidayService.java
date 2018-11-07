package com.nalashaa.timesheet.service;

import java.util.List;

import com.nalashaa.timesheet.entity.Holiday;

public interface IHolidayService {
	List<Holiday> fetchHolidaysOfYear(String year);
	
	List<Holiday> updateHolidaysOfYear(List<Holiday> lstHoliday, String year, String personId);
}
