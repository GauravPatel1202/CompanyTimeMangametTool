
package com.nalashaa.timesheet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Task;

/**
 * 
 * Interface for CRUD operations on a repository for a Task type.
 * 
 * @author ashwanikannojia
 */
@Repository
public interface ITaskDAO extends JpaRepository<Task, Long> {

	/**
	 * A constant holding the query to update/delete the task by task id. QUERY: {@value #DELETE_TASK_BY_TASK_ID}.
	 */
	String DELETE_TASK_BY_TASK_ID = "UPDATE Task task SET task.status = false WHERE task.id = ?1";
	
	String FIND_OLD_TASKS_TO_BE_PURGED="SELECT t.id from orion.project p JOIN orion.task t ON p.id = t.project_id  \r\n" + 
			"AND p.project_status =0 \r\n" + 
			"AND p.enddate < DATE_SUB(CURDATE(), INTERVAL 13 MONTH) \r\n" + 
			"AND t.status = 0  ";
	
	String PURGE_OLD_TASKS="DELETE from orion.task where task.id IN(?1)";
	
	/**
	 * This method is used to get the all tasks by task name.
	 * 
	 * @param searchString must not be {@literal null}
	 * @return list of tasks for given task name or {@literal null} if none found
	 */
    List<Task> findByTaskName(String searchString);
    
    /**
     * 
     * @param searchString must not be {@literal null}
     * @return or {@literal null} if none found
     */
    Task findByTaskId(String searchString);
    
    /**
     * 
     * @param searchString
     * @param project
     * @return
     */
    Task findByTaskIdAndProject(String searchString, Project project);

    /**
     * This method is used to get the list of tasks by project
     * 
     * @param project must not be {@literal null}
     * @return list of tasks for given project or {@literal null} if none found
     */
    List<Task> findByProjectAndStatusTrue(Project project);

    /**
     * This method is used to get task by task id.
     * 
     * @param taskId must not be {@literal null}
     * @return task for given task id or {@literal null} if none found
     */
    Task findById(long taskId);
    
    /**
     * This method is used to get all active task list.
     * 
     * @return list of all active task or {@literal null} if none found
     */
    List<Task> findByStatusTrue();

    /**
     * This method is used to get the update/delete the task by task id.
     * 
     * @param taskId must not be {@literal null}
     */
    @Modifying
    @Query(DELETE_TASK_BY_TASK_ID)
    void deleteTask(long taskId);

    /**
     * This method is used to get list of tasks by projects.
     * 
     * @param project must not be {@literal null}
     * @return list of tasks for given projects or {@literal null} if none found
     */
    List<Task> findByProjectAndAssignTeamTrue(Project project);
    
    /**
     * 
     * @return
     */
    @Query(value = FIND_OLD_TASKS_TO_BE_PURGED, nativeQuery=true)
    List<Long> findOldTasksToBePurged();
    
    /**
     * 
     * @param taskids
     */
    @Modifying
    @Query(value=PURGE_OLD_TASKS, nativeQuery=true)
    void deleteOldTasks(List<Long> taskids);
    
}
