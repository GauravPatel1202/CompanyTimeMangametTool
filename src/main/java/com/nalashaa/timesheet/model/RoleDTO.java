/**
 * 
 */

package com.nalashaa.timesheet.model;

import java.io.Serializable;

public class RoleDTO implements Serializable{

	private static final long serialVersionUID = 7798773510711496289L;

	private long id;

    private String roleName;
   
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

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

    
}
