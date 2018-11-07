/**
 * 
 */

package com.nalashaa.timesheet.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.nalashaa.timesheet.Exception.TimeSheetException;

/**
 * @author siva
 *
 */
@Entity
@Table(name = "WORKLOGSTATUS")
public class WorklogStatus implements Serializable {

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "STATUS", updatable = true, length = 20)
    private String status;

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
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        WorklogStatus other = (WorklogStatus) obj;
        if (id != other.getId()) return false;
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
