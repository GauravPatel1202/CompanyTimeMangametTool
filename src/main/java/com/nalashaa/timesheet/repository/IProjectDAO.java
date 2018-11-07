
package com.nalashaa.timesheet.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Task;

/**
 * 
 * Interface for CRUD operations on a repository for a Project type.
 * 
 * @author ashwanikannojia
 */
@Repository
public interface IProjectDAO extends JpaRepository<Project, Integer> {

	/**
	 * A constant holding the query to get all projects by client id. QUERY: {@value #FIND_ALL_PROJECTS_BY_CLIENT_ID}.
	 */
	String FIND_ALL_PROJECTS_BY_CLIENT_ID = "SELECT project FROM Project project WHERE project.client.id = ?1 AND project.projectStatus = true";
	
	/**
	 * A constant holding the query to delete/update project by project id. QUERY: {@value #DELETE_PROJECT_BY_PROJECT_ID}.
	 */
	String DELETE_PROJECT_BY_PROJECT_ID = "UPDATE Project project SET project.projectStatus = false WHERE project.id = ?1";
	
	/**
	 * A constant holding the query to get all projects by client id and person id. QUERY: {@value #FIND_ALL_PROJECTS_BY_CLIENT_ID_AND_PERSON_ID}.
	 */
	String FIND_ALL_PROJECTS_BY_CLIENT_ID_AND_PERSON_ID = "SELECT project FROM Project project WHERE project.client.id = ?1 AND project.projectStatus = true AND project.id IN (SELECT pp.project.id FROM ProjectPerson pp WHERE pp.person.id = ?2))";
    
	/**
	 * A constant holding the query to get all active global projects. QUERY: {@value #FIND_ALL_ACTIVE_GLOBAL_PROJECTS}.
	 */
	String FIND_ALL_ACTIVE_GLOBAL_PROJECTS = "SELECT project FROM Project project WHERE project.globalProject = true AND project.projectStatus = true";
	
	/**
	 * A constant holding the query to get all active projects. QUERY: {@value #FIND_ALL_ACTIVE_PROJECTS}.
	 */
	String FIND_ALL_ACTIVE_PROJECTS = "SELECT project FROM Project project WHERE project.globalProject = false AND project.projectStatus = true";
	
	/**
	 * A constant holding the query to get all projects for timesheet approvable by person id. QUERY: {@value #FIND_TIMESHEET_APPROVABLE_PROJECTS_BY_PERSON_ID}.
	 */
	String FIND_TIMESHEET_APPROVABLE_PROJECTS_BY_PERSON_ID ="SELECT project FROM Project project WHERE project.id IN (SELECT ppp.project.id FROM ProjectPerson ppp WHERE ppp.person.id=?1 AND (ppp.roleId=1 OR ppp.roleId=2 OR ppp.roleId=3))";
	
	/**
	 * A constant holding the query to get project by project id, month and year. QUERY: {@value #FIND_PROJECT_BY_PROJECT_ID_AND_MONTH_AND_YEAR}.
	 */
	String FIND_PROJECT_BY_PROJECT_ID_AND_MONTH_AND_YEAR ="SELECT project FROM Project project WHERE project.id = ?1 AND month(project.createdDate) = ?2 AND year(project.createdDate) = ?3";

	/**
	 * A constant holding the query to get the list of active projects for an admin to assign resources
	 */
	String FIND_ACTIVE_PROJECTS_ADMIN = "SELECT project FROM Project project where project.projectStatus = true AND project.globalProject = false AND project.endDate >= ?1";
	
	/**
	 * A constant holding the query to get the list of projects for an admin to create tasks
	 */
	String FIND_ACTIVE_PROJECTS_ADMIN_TASKS = "SELECT project FROM Project project where project.projectStatus = true AND project.endDate >= ?1";
	
	String FIND_OLD_PROJECTS_TO_BE_PURGED = "SELECT pp.project_id from orion.Project proj JOIN orion.ProjectPerson pp ON proj.id = pp.project_id \r\n" + 
			 		"AND proj.project_Status = 0 AND  proj.endDate < DATE_SUB(CURDATE(), INTERVAL 13 MONTH)";
	
	String PURGE_OLD_PROJECTS="DELETE orion.project FROM orion.project JOIN orion.task WHERE project.project_status = 0 AND task.status = 0 "
			+ "AND project.enddate < DATE_SUB(CURDATE(), INTERVAL 13 MONTH)";
	
	/**
	 * This method is used to get all projects by its name.
	 * 
	 * @param searchString must not be {@literal null}
	 * @return list of projects with the given name or {@literal null} if none found
	 */
	List<Project> findByProjectName(String searchString);

	/**
	 * This method is used to get project by id.
	 * 
	 * @param id must not be {@literal null}
	 * @return project with the given Id or {@literal null} if none found
	 */
    Project findById(long id);
    
    /**
     * This method is used to get project by project id.
     * 
     * @param projectId  must not be {@literal null}
     * @return project with the given projectId or {@literal null} if none found
     */
    Project findByProjectId(String projectId);

    /**
     * This method is used to get list of inactive global projects.
     * 
     * @return list of inactive global projects or {@literal null} if none found
     */
    List<Project> findByGlobalProjectFalse();
    
    /**
     * This method is used to get all active projects excluding global projects.
     * 
     * @return list of all active projects or {@literal null} if none found.
     */
    List<Project> findByProjectStatusTrueAndGlobalProjectFalse();

    /**
     * This method is to get all active projects including global projects.
     * 
     * @return list of all active projects.
     */
    List<Project> findByProjectStatusTrue();
    
    /**
     * This method is used get all projects by client id.
     * 
     * @param clientId must not be {@literal null}
     * @return list of projects for given client id or {@literal null} if none found
     */
    @Query(FIND_ALL_PROJECTS_BY_CLIENT_ID)
    List<Project> getProjectByClient(long clientId);
    
    /**
     * This method is used to update/delete the project by project id.
     * 
     * @param projectId must not be {@literal null}
     */
    @Modifying
    @Query(DELETE_PROJECT_BY_PROJECT_ID)
    void deleteProject(long projectId);

    /**
     * This method is used get all the projects by client id and person id.
     * 
     * @param clientId must not be {@literal null}
     * @param personId must not be {@literal null}
     * @return list of projects for given client id and person id or {@literal null} if none found
     */
    @Query(FIND_ALL_PROJECTS_BY_CLIENT_ID_AND_PERSON_ID)
    List<Project> getProjectByClientAndPerson(long clientId, long personId);

    /**
     * This method is used to get all active global projects.
     * 
     * @return list of all active global projects or {@literal null} if none found
     */
    @Query(FIND_ALL_ACTIVE_GLOBAL_PROJECTS)
    List<Project> getGlobalProjects();
    
    /**
     * This method is used to get all active projects excluding global projects.
     * 
     * @return list of all active projects or {@literal null} if none found
     */
    @Query(FIND_ALL_ACTIVE_PROJECTS)
    List<Project> getProjectDetails();

    /**
     * This method is used to get all projects for timesheet approvable by person id.
     * 
     * @param personId must not be {@literal null}
     * @return list of all projects for given person id or {@literal null} if none found
     */
    @Query(FIND_TIMESHEET_APPROVABLE_PROJECTS_BY_PERSON_ID)
    List<Project> getTimesheetApprovableProjects(long personId);
    
    /**
     * This method is used to get all projects by project id, month and year.
     * 
     * @param projectId must not be {@literal null}
     * @param month must not be {@literal null}
     * @param year must not be {@literal null}
     * @return list of projects for given project id, month and year or {@literal null} if none found
     */
    @Query(FIND_PROJECT_BY_PROJECT_ID_AND_MONTH_AND_YEAR)
    Project getProjectsByProjectIdAndMonthAndYear(Long projectId,Integer month, Integer year);

    /**
     * 
     * @param currDate
     * @return List<Project>
     * 
     * This method fetches the list of all active projects for which admin can assign resources
     */
    @Query(FIND_ACTIVE_PROJECTS_ADMIN)
    List<Project> findActiveProjectsForAdmin(Date currDate);
    
    /**
     * 
     * @param currDate
     * @return List<Project>
     * 
     * This method fetches the list of all active projects for which admin can create tasks
     */
    @Query(FIND_ACTIVE_PROJECTS_ADMIN_TASKS)
    List<Project> findActiveProjectsForAdminTasks(Date currDate);
    
    /**
     * 
     * @return
     */
    @Query(value = FIND_OLD_PROJECTS_TO_BE_PURGED, nativeQuery=true)
    List<Long> findOldProjectsToBePurged();
    
    /**
     * 
     */
    @Modifying
    @Query(value = PURGE_OLD_PROJECTS , nativeQuery=true)
    void purgeOldProjects();
    
}
