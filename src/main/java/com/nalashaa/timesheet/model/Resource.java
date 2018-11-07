package com.nalashaa.timesheet.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nalashaa.timesheet.entity.AssignResource;
import com.nalashaa.timesheet.entity.Designation;
import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Role;
import com.nalashaa.timesheet.util.GenericResponseDataBlock;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Resource {

	private String empId;

	private String empName;

	private List<String> skill;
	
	private String emailAddress;
	
	private Date dateOfJoining;
	
	private List<Designation> designationList;	

	private Map<Long, String> designation = new HashMap<Long, String>();
	
	private Map<String, List<AssignResource>> assignedResource = new HashMap<String, List<AssignResource>>();
	
	private List<Role> role;	
	
	private List<Project> project;
	
	@Transient
	private GenericResponseDataBlock genericResponseData;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<Person> person;	

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

	public Map<Long, String> getDesignation() {
		return designation;
	}

	public void setDesignation(Map<Long, String> designation) {
		this.designation = designation;
	}

	public List<String> getSkill() {
		return skill;
	}

	public void setSkill(List<String> skill) {
		this.skill = skill;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the dateOfJoining
	 */
	public Date getDateOfJoining() {
		return dateOfJoining;
	}

	/**
	 * @param dateOfJoining the dateOfJoining to set
	 */
	public void setDateOfJoining(Date dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	public List<Designation> getDesignationList() {
		return designationList;
	}

	public void setDesignationList(List<Designation> designationList) {
		this.designationList = designationList;
	}

	public List<Project> getProject() {
		return project;
	}

	public void setProject(List<Project> project) {
		this.project = project;
	}

	public List<Person> getPerson() {
		return person;
	}

	public void setPerson(List<Person> person) {
		this.person = person;
	}

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	public Map<String, List<AssignResource>> getAssignedResource() {
		return assignedResource;
	}

	public void setAssignedResource(Map<String, List<AssignResource>> assignedResource) {
		this.assignedResource = assignedResource;
	}

	/**
	 * @return the genericResponseData
	 */
	public GenericResponseDataBlock getGenericResponseData() {
		return genericResponseData;
	}

	/**
	 * @param genericResponseData the genericResponseData to set
	 */
	public void setGenericResponseData(GenericResponseDataBlock genericResponseData) {
		this.genericResponseData = genericResponseData;
	}
		
}
