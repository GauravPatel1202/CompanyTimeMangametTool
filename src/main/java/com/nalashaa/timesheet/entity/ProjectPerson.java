
package com.nalashaa.timesheet.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(name = "PROJECTPERSON",uniqueConstraints=
@UniqueConstraint(columnNames = {"PROJECT_ID", "PERSON_ID"}))
public class ProjectPerson implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID", referencedColumnName = "ID", updatable = true, insertable = true, nullable = false)
    private Project project;
    
    @ManyToOne
    @JoinColumn(name = "PERSON_ID", referencedColumnName = "ID", updatable = true, insertable = true, nullable = false)
    private Person person;

    @Column(name = "ROLE_ID", updatable = true, length = 20) 
    private Long roleId;
    
    @Column(name = "RESOURCE_ALLOCATION")
    private boolean resourceAllocation;
    
    public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
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

	public boolean isResourceAllocation() {
		return resourceAllocation;
	}

	public void setResourceAllocation(boolean resourceAllocation) {
		this.resourceAllocation = resourceAllocation;
	}
	
	
}
