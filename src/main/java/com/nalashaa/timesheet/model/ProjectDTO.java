/**
 * 
 */

package com.nalashaa.timesheet.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nalashaa.timesheet.Exception.TimeSheetException;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProjectDTO extends BaseDTO implements Serializable {


	private static final long serialVersionUID = -2112162287114684678L;

	private long id;
	
    private String projectId;
    
    private String projectName;
    
    private String billingType;

    private boolean projectStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    private ClientDTO client;
    
    private boolean globalProject;
        
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getProjectStatus() {
        return projectStatus;
    }

    
    public void setProjectStatus(boolean projectStatus) {
        this.projectStatus = projectStatus;
    }


    /**
	 * @return the client
	 */
	public ClientDTO getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(ClientDTO client) {
		this.client = client;
	}

	public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    
    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    
    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the globalProject
     */
    public boolean isGlobalProject() {
        return globalProject;
    }

    
    /**
     * @param globalProject the globalProject to set
     */
    public void setGlobalProject(boolean globalProject) {
        this.globalProject = globalProject;
    }
    
    public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getBillingType() {
		return billingType;
	}

	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}

	@Override
    public int hashCode() {
        return (int) this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ProjectDTO other = (ProjectDTO) obj;
        if (id != other.getId()) return false;
        return true;
    }

    @Override
    public String toString() {
        String result = new String();
        try {
            result = ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE).replaceAll("\\\"", "\"");
        } catch (UnsupportedOperationException ex) {
            throw new TimeSheetException("Exception in tostring()");
        }
        return result;
    }
}
