package com.nalashaa.timesheet.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Client;
import com.nalashaa.timesheet.entity.Holiday;

/**
 * 
 * Interface for CRUD operations on a repository for a Holidays.
 * 
 * @author nandeeshp
 */
@Repository
public interface IHolidayDAO extends JpaRepository<Holiday, Long> {
	
	String GET_HOLIDAY_DATES="Select hol.holidayDate from Holiday hol ORDER BY hol.holidayDate";
	/**
	 * 
	 * @param year
	 * @return List<Holiday>
	 * 
	 * This method is used to fetch the list of all holidays of given year
	 */
	List<Holiday> findByYearOrderByHolidayDate(String year);
	
	/**
     * This method is used to delete all the holidays of given year.
     * 
     * @param task must not be {@literal null}.
     */
    @Modifying
    @Transactional
    void deleteByYear(String year);
    
    @Query(GET_HOLIDAY_DATES)
    List<Date> getHolidayDates();
}
