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

import com.nalashaa.timesheet.Exception.TimeSheetException;

/**
 * @author siva
 *
 */
@Entity
@Table(name = "WORKLOG")
public class Worklog extends PersistentEntity implements Serializable {

	private static final long serialVersionUID = -2141332870433262980L;

	@Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "PERSON_ID", referencedColumnName = "ID", updatable = true, insertable = true, nullable = false)
    private Person person;

    @ManyToOne
    @JoinColumn(name = "PROJECT_ID", referencedColumnName = "ID", updatable = true, insertable = true, nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "TASK_ID", referencedColumnName = "ID", updatable = true, insertable = true, nullable = false)
    private Task task;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "HOURS_SPENT", updatable = true)
    private Float hoursSpent;

    @ManyToOne
    @JoinColumn(name = "WORKLOGSTATUS_ID", referencedColumnName = "ID", updatable = true, insertable = true, nullable = false)
    private WorklogStatus status;
    
    @Column(name = "INDEXNUM", updatable = true, length = 255)
    private int index;
    
    @Column(name = "REJECTCOMMENT", updatable = true, length = 255)
    private String rejectComment;
    
    @Column(name = "COMMENTS", updatable = true)
    private String comments;

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getHoursSpent() {
        return hoursSpent;
    }

    public void setHoursSpent(float hoursSpent) {
        this.hoursSpent = hoursSpent;
    }

    
    /**
     * @return the staus
     */
    public WorklogStatus getStatus() {
        return status;
    }

    
    /**
     * @param staus the staus to set
     */
    public void setStaus(WorklogStatus status) {
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    
    public void setIndex(int index) {
        this.index = index;
    }
    
    /**
	 * @return the rejectComment
	 */
	public String getRejectComment() {
		return rejectComment;
	}

	/**
	 * @param rejectComment the rejectComment to set
	 */
	public void setRejectComment(String rejectComment) {
		this.rejectComment = rejectComment;
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
        Worklog other = (Worklog) obj;
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
