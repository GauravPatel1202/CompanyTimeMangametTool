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

/**
 * @author VIJAY GANESH
 *
 */
@Entity
@Table(name = "PERSON")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Person extends PersistentEntity implements Serializable {

	private static final long serialVersionUID = 1991715534518719527L;

	@Id
	@Column(name = "ID", unique = true, nullable = false, length = 20)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "EMP_ID", length = 20, unique = true, nullable = false, updatable = false)
	private String empId;

	@Column(name = "EMP_NAME", updatable = true, length = 255)
	private String empName;

	@ManyToOne
	@JoinColumn(name = "DESIGNATION", referencedColumnName = "ID", updatable = true, insertable = true, nullable = false)
	private Designation designation;

	@Column(name = "SKILL", updatable = true)
	private String skill;

	@Column(name = "EMAIL_ADDRESS", updatable = true, length = 100)
	private String emailAddress;

	@Column(name = "STATUS")
	private boolean status;

	@Column(name = "PASSWORD", updatable = true)
	private String password;
	
	private String newPassword;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_OF_JOINING", nullable = true)
    private Date dateOfJoining;

	@ManyToOne
	@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", updatable = true, insertable = true, nullable = false)
	private Role role;
	
	@Column(name = "ISFIRSTLOGIN")
	private boolean isFirstLogin;

	public boolean isFirstLogin() {
		return isFirstLogin;
	}

	public void setFirstLogin(boolean isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public Designation getDesignation() {
		return designation;
	}

	public void setDesignation(Designation designation) {
		this.designation = designation;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public Date getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(Date dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
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
