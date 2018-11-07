/**
 * 
 */

package com.nalashaa.timesheet.model;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.WorklogStatus;

/**
 * @author siva
 *
 */

public class TimeSheet {

    private long personId;
    
    private long loggedUserId;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date date;

    private Person updatedBy;

    private Date updatedDate;

    private WorklogStatus status;
    
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private List<Date> dates;
    
    public long getPersonId() {
        return personId;
    }

    
    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the updatedBy
     */
    public Person getUpdatedBy() {
        return updatedBy;
    }


    
    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Person updatedBy) {
        this.updatedBy = updatedBy;
    }


    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * @return the status
     */
    public WorklogStatus getStatus() {
        return status;
    }


    
    /**
     * @param status the status to set
     */
    public void setStatus(WorklogStatus status) {
        this.status = status;
    }


    public List<Date> getDates() {
        return dates;
    }


    
    public void setDates(List<Date> dates) {
        this.dates = dates;
    }
    
    /**
     * @return the loggedUserId
     */
    public long getLoggedUserId() {
        return loggedUserId;
    }


    
    /**
     * @param loggedUserId the loggedUserId to set
     */
    public void setLoggedUserId(long loggedUserId) {
        this.loggedUserId = loggedUserId;
    }

    
}