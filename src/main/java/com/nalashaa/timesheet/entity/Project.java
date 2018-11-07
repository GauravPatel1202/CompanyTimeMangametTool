/**
 * 
 */

package com.nalashaa.timesheet.entity;

import java.io.Serializable;
import java.util.Date;

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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nalashaa.timesheet.Exception.TimeSheetException;

@Entity
@Table(name = "PROJECT")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Project extends PersistentEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
    @Column(name = "PROJECT_ID", updatable = true, length = 20, unique = true, nullable = false)
    private String projectId;
    
    @Column(name = "PROJECT_NAME", updatable = true, length = 255)
    private String projectName;
    
    @Column(name = "BILLING_TYPE", updatable = true, length = 255)
    private String billingType;

    @Column(name = "PROJECT_STATUS")
    private boolean projectStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "STARTDATE", updatable = true)
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "ENDDATE", updatable = true)
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "CLIENT_ID", referencedColumnName = "ID", updatable = true, insertable = true, nullable = true)
    private Client client;
    
    @Column(name = "GLOBALPROJECT")
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
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
        Project other = (Project) obj;
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
