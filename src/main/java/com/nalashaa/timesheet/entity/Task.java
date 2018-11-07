/**
 * 
 */

package com.nalashaa.timesheet.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import com.nalashaa.timesheet.Exception.TimeSheetException;

/**
 * @author siva
 *
 */
@Entity
@Table(name = "TASK")
public class Task extends PersistentEntity {

	private static final long serialVersionUID = 9049842322738178710L;

	@Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "NAME", updatable = true, length = 100)
    private String taskName;

    @Column(name = "TASK_ID", updatable = true, length = 255, nullable = false)
    private String taskId;

    @Column(name = "ESTIMATED_HOURS")
    private double estimatedHours;
    
    @Transient
    private double hoursLeft;
    
//    @Column(name = "HOURSCONSUMED", updatable = true)
    private double hoursConsumed;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "STARTDATE", updatable = true)
    private Date startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "ENDDATE", updatable = true)
    private Date endDate;

    @Column(name = "ASSIGNTEAM")
    private boolean assignTeam;
    
    @Column(name = "STATUS")
    private boolean status;
    
    @Column(name = "JIRAIMPORT")
    private boolean isJiraImport;
    
    @Column(name = "DESCRIPTION" , updatable = true, length = 255)
    private String description;
    
    @Column(name="UPDATEREASON" , updatable=true, nullable=true, length=500)
    private String updateReason;

    @ManyToOne
    @JoinColumn(name="PROJECT_ID",referencedColumnName="ID",updatable=true,insertable=true,nullable=false)
    private Project project;

    /*@ManyToOne
    @JoinColumn(name="REPORTING_USER_ID",referencedColumnName="ID",updatable=true,insertable=true,nullable=true)
    private Person reportingUser;*/
    
    @Transient
    List<Person> assignedUsers = new ArrayList<>();
    
    /**
	 * @return the hoursConsumed
	 */
	public double getHoursConsumed() {
		return hoursConsumed;
	}

	/**
	 * @param hoursConsumed the hoursConsumed to set
	 */
	public void setHoursConsumed(double hoursConsumed) {
		this.hoursConsumed = hoursConsumed;
	}

	@Column(name = "ACTIVE")
    private boolean active; 
    
    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

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
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }
    
    

    
/*    *//**
     * @return the reportingUser
     *//*
    public Person getReportingUser() {
        return reportingUser;
    }

    
    *//**
     * @param reportingUser the reportingUser to set
     *//*
    public void setReportingUser(Person reportingUser) {
        this.reportingUser = reportingUser;
    }*/

    
    
    /**
     * @return the assignedUsers
     */
    public List<Person> getAssignedUsers() {
        return assignedUsers;
    }

    
    /**
     * @param assignedUsers the assignedUsers to set
     */
    public void setAssignedUsers(List<Person> assignedUsers) {
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

    
    /**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	
	/**
	 * @return the updateReason
	 */
	public String getUpdateReason() {
		return updateReason;
	}

	/**
	 * @param updateReason the updateReason to set
	 */
	public void setUpdateReason(String updateReason) {
		this.updateReason = updateReason;
	}

	
	/**
	 * @return the isJiraImport
	 */
	public boolean isJiraImport() {
		return isJiraImport;
	}

	/**
	 * @param isJiraImport the isJiraImport to set
	 */
	public void setJiraImport(boolean isJiraImport) {
		this.isJiraImport = isJiraImport;
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
        Task other = (Task) obj;
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
