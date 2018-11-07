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

/**
 * @author vijayganesh
 *
 */
@Entity
@Table(name = "DESIGNATION")
public class Designation implements Serializable {

	private static final long serialVersionUID = 5200316853017991816L;

	@Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "DESIGNATION_NAME", updatable = true, length = 30)
    private String designation;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

}
