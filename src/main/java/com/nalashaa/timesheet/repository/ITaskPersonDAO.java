
package com.nalashaa.timesheet.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.entity.TaskPerson;

/**
 * Interface for CRUD operations on a repository for a TaskPerson type.
 * 
 * @author ashwanikannojia
 */
@Repository
public interface ITaskPersonDAO extends JpaRepository<TaskPerson, Long> {

	/**
	 * A constant holding the query to get task by project id and person id. QUERY: {@value #GET_TASK_BY_PROJECT_AND_PERSON}.
	 */
	String GET_TASK_BY_PROJECT_AND_PERSON = "SELECT DISTINCT tp.task FROM TaskPerson tp WHERE  tp.task.project.id = ?1 AND tp.person.id = ?2 AND tp.task.status = true AND(tp.task.endDate IS NULL OR tp.task.endDate >= ?3)";
    
	/**
	 * A constant holding the query to get all persons by task id . QUERY: {@value #GET_PERSON_BY_TASK}.
	 */
	String GET_PERSON_BY_TASK = "SELECT DISTINCT(tp.person) FROM TaskPerson tp WHERE tp.task= ?1 AND tp.person.status = true";
	
	/**
	 * A constant holding the query to get taskperson by task id and person id. QUERY: {@value #TASKPERSON_BY_TASK_ID_AND_PERSON_ID}.
	 */
	String TASKPERSON_BY_TASK_ID_AND_PERSON_ID = "SELECT tp FROM TaskPerson tp where tp.task = ?1 AND tp.person = ?2";

	String DELETE_TASKPERSONS_FOR_TASK_BY_ID = "DELETE FROM TaskPerson tp WHERE tp.task.id = ?1"; 
	
	/**
	 * This method is used to get list of tasks by project id and person id.
	 * 
	 * @param projectId must not be {@literal null}.
	 * @param personId must not be {@literal null}.
	 * @param monthStartDate must not be {@literal null}.
	 * @return  list of tasks for given project id and person id or {@literal null} if none found
	 */
	@Query(GET_TASK_BY_PROJECT_AND_PERSON)
    List<Task> getTasksByProjectAndPerson(long projectId, long personId,Date monthStartDate);

	/**
	 * This method is used to get list of taskPerson by task.
	 * 
	 * @param task must not be {@literal null}.
	 * @return list of taskPerson for given task or {@literal null} if none found
	 */
    List<TaskPerson> findByTask(Task task);

    /**
     * This method is used to get all persons list by task.
     * 
     * @param task must not be {@literal null}.
     * @return list of persons for given task or {@literal null} if none found
     */
    @Query(GET_PERSON_BY_TASK)
    List<Person> getPersonsByTask(Task task);
    
    /**
     * This method is used to get taskPerson by task id and person id.
     * 
     * @param taskId must not be {@literal null}.
     * @param personId must not be {@literal null}.
     * @return taskPerson for given task id and person id or {@literal null} if none found
     */
    @Query(TASKPERSON_BY_TASK_ID_AND_PERSON_ID)
    TaskPerson findByTaskIdAndPersonId(Long taskId, Long personId);
    
    /**
     * This method is used to delete the task by task.
     * 
     * @param task must not be {@literal null}.
     */
    @Modifying
    @Transactional
    void deleteByTask(Task task);
    
    /**
     * This method is used to delete the taskPerson by task and person.
     * 
     * @param task  must not be {@literal null}.
     * @param person must not be {@literal null}.
     */
    @Modifying
    @Transactional
    void deleteByTaskAndPerson(Task task, Person person);

    /**
     * 
     * @param id
     */
    @Query(DELETE_TASKPERSONS_FOR_TASK_BY_ID)
	void deleteMappedPersonsForTaskById(Long id);
    
    @Modifying
    @Query(value="DELETE FROM orion.taskperson WHERE taskperson.task_id IN (?1)", nativeQuery=true)
    void purgeOldTaskAllocations(List<Long> taskids);
}
