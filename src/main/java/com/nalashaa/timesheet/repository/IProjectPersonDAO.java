
package com.nalashaa.timesheet.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.ProjectPerson;

/**
 * Interface for CRUD operations on a repository for a ProjectPerson type.
 * 
 * @author ashwanikannojia
 */
@Repository
public interface IProjectPersonDAO extends JpaRepository<ProjectPerson, Long> {

	/**
	 * A constant holding the query to get all active projects by person id. QUERY: {@value #FIND_ACTIVE_PROJECTS_BY_PERSON_ID_AND_STATUS}.
	 */
	String FIND_ACTIVE_PROJECTS_BY_PERSON_ID_AND_STATUS = "SELECT DISTINCT pp.project FROM ProjectPerson pp where pp.person.id = ?1 AND pp.project.projectStatus = ?2 ORDER BY pp.id desc";
	
	/**
	 * A constant holding the query to get all projects by person id. QUERY: {@value #FIND_ALL_PROJECTS_BY_PERSON_ID}.
	 */
	String FIND_ALL_PROJECTS_BY_PERSON_ID = "SELECT DISTINCT pp.project FROM ProjectPerson pp where pp.person.id = ?1";
	
	/**
	 * A constant holding the query to get all persons by a project. QUERY: {@value #FIND_ALL_PERSONS_BY_PROJECT}.
	 */
	String FIND_ALL_PERSONS_BY_PROJECT = "SELECT  DISTINCT pp.person FROM ProjectPerson pp where pp.project = ?1 AND pp.person.status = true";
	
	/**
	 * A constant holding the query to get all distinct projectPerson by project. QUERY: {@value #FIND_ALL_DISTINCT_PROJECTPERSON_BY_PROJECT}.
	 */
	String FIND_ALL_DISTINCT_PROJECTPERSON_BY_PROJECT = "SELECT  DISTINCT pp FROM ProjectPerson pp where pp.project = ?1";
	
	/**
	 * A constant holding the query to get all active projects by person. QUERY: {@value #FIND_ALL_ACTIVE_PROJECTS_BY_PERSON}.
	 */
	String FIND_ALL_ACTIVE_PROJECTS_BY_PERSON = "SELECT DISTINCT pp.project FROM ProjectPerson pp,Task task where pp.person.id = ?1 AND pp.project.projectStatus = TRUE AND (pp.project.endDate IS NULL OR pp.project.endDate >= ?2) AND (0<(SELECT COUNT(tp) FROM TaskPerson tp where tp.person.id = ?1 AND tp.task.status = true AND (tp.task.endDate IS NULL OR tp.task.endDate >= ?2) AND task.project IN(SELECT pp.project FROM ProjectPerson pp where pp.person.id = ?1 AND pp.project.projectStatus = TRUE)) OR 0<(SELECT COUNT(task) FROM Task task where task.status = true AND task.assignTeam = true AND (task.endDate IS NULL OR task.endDate >= ?2) AND task.project IN(SELECT pp.project FROM ProjectPerson pp where pp.person.id = ?1 AND pp.project.projectStatus = TRUE)))";
	
	/**
	 * A constant holding the query to get all persons by all projects for given person. QUERY: {@value #FIND_ALL_PERSONS_BY_ALL_PROJECTS_FOR_PERSON}.
	 */
	String FIND_ALL_PERSONS_BY_ALL_PROJECTS_FOR_PERSON = "SELECT  DISTINCT pp.person FROM ProjectPerson pp where pp.project IN ?1 AND pp.project.projectStatus = true AND pp.person.id != ?2 AND pp.person.role.id > (SELECT person.role.id FROM Person person WHERE person.id = ?2)";
	
	/**
	 * A constant holding the query to get max role of person in all projects. QUERY: {@value #FIND_MAX_ROLE_OF_PERSON_IN_ALL_PROJECTS}.
	 */
	String FIND_MAX_ROLE_OF_PERSON_IN_ALL_PROJECTS = "select min(pp.roleId) from ProjectPerson pp where pp.person.id = ?1";
	
	/**
	 * A constant holding the query to get all active projects by person and role. QUERY: {@value #FIND_ACTIVE_PROJECTS_BY_PERSON_AND_ROLE}.
	 */
	String FIND_ACTIVE_PROJECTS_BY_PERSON_AND_ROLE = "SELECT DISTINCT pp.project FROM ProjectPerson pp where pp.person.id = ?1 AND pp.project.projectStatus = ?2 and pp.roleId in (1 , 2)";
	
	/**
	 * A constant holding the query to get all active projects by a person for team lead. QUERY: {@value #FIND_ACTIVE_PROJECTS_BY_PERSON_FOR_TEAMLEAD}.
	 */
	String FIND_ACTIVE_PROJECTS_BY_PERSON_FOR_TEAMLEAD = "SELECT DISTINCT pp.project FROM ProjectPerson pp where pp.person.id = ?1 AND pp.project.projectStatus = ?2 and pp.roleId =3";
	
	/**
	 * A constant holding the query to get all projects by person and role. QUERY: {@value #FIND_ALL_PROJECTS_BY_PERSON_AND_ROLE}.
	 */
	String FIND_ALL_PROJECTS_BY_PERSON_AND_ROLE = "SELECT DISTINCT pp.project FROM ProjectPerson pp where pp.person.id = ?1 AND pp.roleId in(1,2,3) AND pp.project.projectStatus = true";

	/**
	 * A constant holding the query to fetch the list of users allocated to the given project.
	 */
	String FIND_USERS_UNDER_PROJECT = "select DISTINCT pp.person FROM ProjectPerson pp WHERE pp.project.id = ?1 ORDER BY pp.person.empName ASC";
	
	/**
	 * A constant holding the query to fetch the list of users who are not allocated to any project.
	 */
	String FIND_USERS_ON_BENCH = "select person FROM Person person where person NOT IN (select DISTINCT pp.person from ProjectPerson pp)";

	/**
	 * A constant holding the query to get the list of active projects for an project manager to assign resources
	 */
	String FIND_ACTIVE_PROJECTS_MANAGER = "SELECT DISTINCT pp.project FROM ProjectPerson pp where pp.person.id = ?1 AND pp.project.projectStatus = ?2 and pp.roleId in (1 , 2) AND pp.project.endDate >= ?3";

	/**
	 * This method is used to get all active projects by person id.
	 * 
	 * @param personId must not be {@literal null}.
	 * @param status must not be {@literal null}.
	 * @return list of active projects by given person id or {@literal null} if none found
	 */
    @Query(FIND_ACTIVE_PROJECTS_BY_PERSON_ID_AND_STATUS)
    List<Project> findActiveProjectsByPerson(long personId, boolean status);
    
    /**
     * This method is used to get all projects by person id.
     * 
     * @param personId must not be {@literal null}.
     * @return list of projects by given person id .or {@literal null} if none found
     */
    @Query(FIND_ALL_PROJECTS_BY_PERSON_ID)
    List<Project> findAllProjectsByPerson(long personId);

    /**
     * This method is used to get list of persons by project.
     * 
     * @param project must not be {@literal null}.
     * @return list of persons by given project or {@literal null} if none found
     */
    @Query(FIND_ALL_PERSONS_BY_PROJECT)
    List<Person> findByProject(Project project);

    /**
     * This method is used to get list of projectPerson by project.
     *  
     * @param project must not be {@literal null}.
     * @return list of proejctPerson by given project or {@literal null} if none found
     */
    @Query(FIND_ALL_DISTINCT_PROJECTPERSON_BY_PROJECT)
	List<ProjectPerson> findProjectPerson(Project project);

    /**
     * This method is used to get list of active projects by person id.
     *  
     * @param personId must not be {@literal null}.
     * @param monthStartDate must not be {@literal null}.
     * @return list of active projects by given person id. or {@literal null} if none found
     */
    @Query(FIND_ALL_ACTIVE_PROJECTS_BY_PERSON)
    List<Project> getProjectsByPersonActiveWithTasks(long personId,Date monthStartDate);

    /**
     * This method is used to get list of persons by list of projects for loggedId user.
     * 
     * @param projects must not be {@literal null}.
     * @param loggedInuserId must not be {@literal null}.
     * @return list of persons by given list of projects or {@literal null} if none found
     */
    @Query(FIND_ALL_PERSONS_BY_ALL_PROJECTS_FOR_PERSON)
    List<Person> findPersonByProjects(List<Project> projects , long loggedInuserId);

    /**
     * This method is used to get projectPerson by project and person.
     * 
     * @param projectSaved must not be {@literal null}.
     * @param user must not be {@literal null}.
     * @return projectPerson entity by given project and person or {@literal null} if none found
     */
    ProjectPerson findByProjectAndPerson(Project projectSaved, Person user);

    /**
     * This method is used to get max role for a person in all projects.
     * 
     * @param personId must not be {@literal null}.
     * @return max role in all projects by given person id  or {@literal null} if none found
     */
    
    ProjectPerson findByProjectAndRoleId(Project project, long roleId);
    
    @Query(FIND_MAX_ROLE_OF_PERSON_IN_ALL_PROJECTS)
    Long findMaxRoleIdOfPersonInAllProjects(Long personId);

    /**
     * This method is used to get the list of all active projects by person id and role.
     *  
     * @param personId must not be {@literal null}.
     * @param status must not be {@literal null}.
     * @return list of all active projects by given person id and role or {@literal null} if none found
     */
    @Query(FIND_ACTIVE_PROJECTS_BY_PERSON_AND_ROLE)
    List<Project> findActiveProjectsByPersonAndRole(long personId, boolean status);
    
    /**
     * This method is used to get list of all active projects by person id as a team lead.
     * 
     * @param personId must not be {@literal null}.
     * @param status must not be {@literal null}.
     * @return list of all active projects by given person id or {@literal null} if none found
     */
    @Query(FIND_ACTIVE_PROJECTS_BY_PERSON_FOR_TEAMLEAD)
    List<Project> findActiveProjectsByPersonForTeamLead(long personId, boolean status);
    
    /**
     * This method is used to get list of all projects by person id and role.
     * 
     * @param personId must not be {@literal null}.
     * @return list of projects by given person id or {@literal null} if none found
     */
    @Query(FIND_ALL_PROJECTS_BY_PERSON_AND_ROLE)
    List<Project> findAllProjectsByPersonAndRole(long personId);

    /**
     * 
     * @param projectId
     * @return List<Person>
     * 
     * This method fetches the list of users who are allocated to the project with given projectId.
     */
    @Query(FIND_USERS_UNDER_PROJECT)
    List<Person> findUsersUnderProject(long projectId);
    
    /**
     * 
     * @return List<Person>
     * 
     * This method returns the list of users who are not allocated to any project.
     */
    @Query(FIND_USERS_ON_BENCH)
    List<Person> findUsersOnBench();

    /**
     * 
     * @param personId
     * @param status
     * @param currDate
     * @return List<Project>
     * 
     * This method fetches the list of all active projects for which project manager can assign resources
     */
    @Query(FIND_ACTIVE_PROJECTS_MANAGER)
    List<Project> findActiveProjectsForManager(long personId, boolean status, Date currDate);
    
    @Modifying
    @Query(value="DELETE FROM orion.projectperson WHERE projectperson.project_id IN (?1)", nativeQuery=true)
    void purgeOldProjectPersonAllocation(List<Long> projectids);
}
