package com.nalashaa.timesheet.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author vijayganesh
 *
 */

@Entity
@Table(name = "PROJECTPERSON",uniqueConstraints=
@UniqueConstraint(columnNames = {"PROJECT_ID", "PERSON_ID"}))
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AssignResource implements Serializable {

	private static final long serialVersionUID = -48413465324670252L;

	@Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	@Column(name = "PROJECT_ID", updatable = true, length = 20)
    private Long projectId;
    
	@Column(name = "PERSON_ID", updatable = true, length = 20)
    private Long personId;
	
	@Column(name = "ROLE_ID", updatable = true, length = 20) 
    private Long roleId;
	
	@Column(name = "RESOURCE_ALLOCATION")
    private boolean resourceAllocation;
	
	@Transient
	private String empId;
	
	@Transient
	private String empName;
	
	@Transient
	private String roleName;
	
	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
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

	
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public boolean isResourceAllocation() {
		return resourceAllocation;
	}

	public void setResourceAllocation(boolean resourceAllocation) {
		this.resourceAllocation = resourceAllocation;
	}
}
