/**
 * 
 */
package com.nalashaa.timesheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Designation;

/**
 * Interface for CRUD operations on a repository for a Designation type.
 * 
 * @author vijayganesh
 */
@Repository
public interface IDesignationDAO extends JpaRepository<Designation, Long> {

}
