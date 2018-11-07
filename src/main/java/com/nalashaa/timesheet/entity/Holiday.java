package com.nalashaa.timesheet.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nalashaa.timesheet.Exception.TimeSheetException;

@Entity
@Table(name = "HOLIDAYLIST")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Holiday extends PersistentEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1406373693415576237L;

	@Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "HOLIDAY_DATE", nullable = true)
    private Date holidayDate;

	@Column(name = "YEAR", updatable = true, nullable = false, length = 4)
    private String year;

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getholidayDate() {
		return holidayDate;
	}

	public void setholidayDate(Date holidayDate) {
		this.holidayDate = holidayDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Holiday other = (Holiday) obj;
		if (id != other.getId())
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
	
	@Override
	public int hashCode() {
		int hashCode = (int)this.getId();
		return hashCode;
	}

}
