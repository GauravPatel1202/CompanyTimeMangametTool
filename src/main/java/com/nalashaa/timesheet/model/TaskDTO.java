/**
 * 
 */

package com.nalashaa.timesheet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.nalashaa.timesheet.Exception.TimeSheetException;

public class TaskDTO extends BaseDTO implements Serializable{

	private static final long serialVersionUID = 4948654102780031367L;

	private long id;

    private String taskName;

    private String taskId;

    private String code;

    private double estimatedHours;
    
    private double hoursLeft;

    @Temporal(TemporalType.DATE)
    private Date startDate;
    
    @Temporal(TemporalType.DATE)
    private Date endDate;

    private boolean assignTeam;
    
    private boolean status;    

    private ProjectDTO project;

    private PersonDTO reportingUser;
    
    @Transient
    List<PersonDTO> assignedUsers = new ArrayList<>();
    
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    
    /**
     * @param taskName the taskName to set
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    
    /**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }
    
    /**
     * @return the hoursLeft
     */
    public double getHoursLeft() {
        return hoursLeft;
    }
    
    /**
     * @param hoursLeft the hoursLeft to set
     */
    public void setHoursLeft(double hoursLeft) {
        this.hoursLeft = hoursLeft;
    }

    /**
	 * @return the estimatedHours
	 */
	public double getEstimatedHours() {
		return estimatedHours;
	}

	/**
	 * @param estimatedHours the estimatedHours to set
	 */
	public void setEstimatedHours(double estimatedHours) {
		this.estimatedHours = estimatedHours;
	}

	/**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public boolean isStatus() {
        return status;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * @return the project
     */
    public ProjectDTO getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(ProjectDTO project) {
        this.project = project;
    }
    
    /**
     * @return the reportingUser
     */
    public PersonDTO getReportingUser() {
        return reportingUser;
    }

    /**
     * @param reportingUser the reportingUser to set
     */
    public void setReportingUser(PersonDTO reportingUser) {
        this.reportingUser = reportingUser;
    }
    
    /**
     * @return the assignedUsers
     */
    public List<PersonDTO> getAssignedUsers() {
        return assignedUsers;
    }
    
    /**
     * @param assignedUsers the assignedUsers to set
     */
    public void setAssignedUsers(List<PersonDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
    
    /**
     * @return the assignToTeam
     */
    public boolean getAssignTeam() {
        return assignTeam;
    }

    
    /**
     * @param assignToTeam the assignToTeam to set
     */
    public void setAssignTeam(boolean assignTeam) {
        this.assignTeam = assignTeam;
    }

    @Override
    public int hashCode() {
        return (int)this.id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskDTO other = (TaskDTO) obj;
        if(id != other.getId())
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        String result = new String();
        try {
            result = ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
        } catch (UnsupportedOperationException ex) {
            throw new TimeSheetException("Exception in tostring()");
        }
        return result;
    }
}
