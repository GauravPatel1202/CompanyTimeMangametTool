
package com.nalashaa.timesheet.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.AssignResource;
import com.nalashaa.timesheet.entity.Project;

/**
 * 
 * Interface for CRUD operations on a repository for a AssignResource type.
 * 
 * @author vijay ganesh
 */
@Repository
public interface IAssignResourceDAO extends JpaRepository<AssignResource, Long> {

	/**
	 * A constant holding the query to get all assigned resources. QUERY: {@value #GET_ASSIGNED_RESOURCES}.
	 */
	String GET_ASSIGNED_RESOURCES = "SELECT  pp.projectId, pp.id, pp.personId, pp.roleId, p.empId, p.empName, r.roleName, "
			+ "pp.resourceAllocation  FROM Person p, Role r, AssignResource pp WHERE p.status = true AND "
			+ "p.id = pp.personId AND r.id = pp.roleId";
	
	/**
	 * A constant holding the query to get all assigned resources by person. QUERY: {@value #GET_ASSIGNED_RESOURCES_BY_PERSON}.
	 */
	String GET_ASSIGNED_RESOURCES_BY_PERSON = "SELECT pp FROM AssignResource pp WHERE pp.personId = ?1";
	
	/**
	 * A constant holding the query to get all assigned resources by person and role. QUERY: {@value #GET_ASSIGNED_RESOURCES_BY_PERSON_AND_ROLE}.
	 */
	String GET_ASSIGNED_RESOURCES_BY_PERSON_AND_ROLE = "SELECT pp FROM AssignResource pp WHERE pp.personId = ?1 AND (pp.roleId=1 OR pp.roleId=2 OR pp.roleId=3)";
	
	/**
	 * A constant holding the query to get all projects by created task based role. QUERY: {@value #GET_PROJECTS_BY_CREATED_TASK_ROLE}.
	 */
	String GET_PROJECTS_BY_CREATED_TASK_ROLE = "SELECT p FROM Project p WHERE id in (SELECT ppp.projectId FROM AssignResource ppp WHERE ppp.personId=?1 AND ppp.roleId in(1,2,3)) AND p.projectStatus = true AND p.endDate >= ?2";

	/**
	 * This method is used to return all instances of assigned resources.
	 * 
	 * @return It returns all required fields from the instances of assigned resources
	 */
	@Query(GET_ASSIGNED_RESOURCES)
	List<Object[]> getAssignedResource();
	
	/**
	 * This method is used to return all assigned resources by provided person.
	 * 
	 * @param personId represent the id of that person on which basis it return assigned resources
	 * @return the list of entities associated with the given id or {@literal null} if none found
	 */
	@Query(GET_ASSIGNED_RESOURCES_BY_PERSON)
	List<AssignResource> getAssignedResourcebyPerson(Long personId);
	
	/**
	 * This method is used to return all assigned resources by provided person and role.
	 * 
	 * @param personId represent the id of that person on which basis it return assigned resources
	 * @return the list of entities associated with the given id and matched role or {@literal null} if none found
	 */
	@Query(GET_ASSIGNED_RESOURCES_BY_PERSON_AND_ROLE)
	List<AssignResource> getAssignedResourcebyPersonAndRole(Long personId);
	
	/**
	 * This method is used to return all projects which are associated with provided person id.
	 * 
	 * @param personId represent the id of that person on which basis it return list of projects
	 * @return the list of entities associated with the given id or {@literal null} if none found
	 */
	@Query(GET_PROJECTS_BY_CREATED_TASK_ROLE)
	List<Project> getProjectsByCreateTaskRole(long personId, Date currDate);

}
