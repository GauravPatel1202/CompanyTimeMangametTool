
package com.nalashaa.timesheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Role;

/**
 *Interface for CRUD operations on a repository for a Role type.
 * 
 * @author ashwanikannojia
 */
@Repository
public interface IRoleDAO extends JpaRepository<Role, Long> {


 /*   @Query("SELCT r from Role r where index > (SELECT role.index from Role role where role.id=?1) order by index ASC")
    public List<Role> getRolesByParentRole(long roleId);*/
	
	Role findById(long id);
}
