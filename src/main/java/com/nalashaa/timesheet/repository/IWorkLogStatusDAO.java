
package com.nalashaa.timesheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.WorklogStatus;

/**
 * Interface for CRUD operations on a repository for a WorklogStatus type. 
 * 
 * @author ashwanikannojia
 */
@Repository
public interface IWorkLogStatusDAO extends JpaRepository<WorklogStatus, Long> {

	/**
	 * This method is used to get the worklogStatus by its status.
	 * 
	 * @param upperCase must not be {@literal null}.
	 * @return WorklogStatus with the given status or {@literal null} if none found
	 */
    WorklogStatus findByStatusIgnoreCase(String upperCase);

}
