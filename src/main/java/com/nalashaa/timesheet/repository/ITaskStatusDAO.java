
package com.nalashaa.timesheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Task;

/**
 *  Interface for CRUD operations on a repository for a Task type.
 * 
 * @author ashwanikannojia
 */
@Repository
public interface ITaskStatusDAO extends JpaRepository<Task, Long> {

}
