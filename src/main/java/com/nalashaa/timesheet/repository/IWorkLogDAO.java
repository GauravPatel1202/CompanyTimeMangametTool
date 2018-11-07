
package com.nalashaa.timesheet.repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.entity.Worklog;
import com.nalashaa.timesheet.entity.WorklogStatus;

/**
 * Interface for CRUD operations on a repository for a Worklog type. 
 *
 * @author ashwanikannojia
 */
@Repository
public interface IWorkLogDAO extends JpaRepository<Worklog, Long> {

	/**
	 * A constant holding the query to update the timesheet status as approve. QUERY: {@value #JPQL_TIMESHEET_APPROVE_STATUS}.
	 */
    String JPQL_TIMESHEET_APPROVE_STATUS = "UPDATE Worklog wl SET wl.status = ?3, wl.lastUpdatedBy = ?4, wl.lastUpdatedTime = ?5 WHERE wl.person = ?1 AND wl.date = ?2";
    
    /**
	 * A constant holding the query to update the status of timesheet. QUERY: {@value #JPQL_TIMESHEET_UPDATE_STATUS}.
	 */
    String JPQL_TIMESHEET_UPDATE_STATUS = "UPDATE Worklog wl SET wl.status = ?3, wl.lastUpdatedBy = ?4, wl.lastUpdatedTime = ?5 WHERE wl.person = ?1 AND wl.date IN (?2)";

    /**
	 * A constant holding the query to get all distinct project, task by a person's start date and end date. QUERY: {@value #JPQL_TIMESHEET_PROJECTS}.
	 */
    String JPQL_TIMESHEET_PROJECTS = "SELECT DISTINCT wl.project , wl.task , wl.index FROM  Worklog wl WHERE wl.person = ?1 AND wl.date >= ?2 AND wl.date <=?3 ORDER BY wl.index ASC";
    
    /**
	 * A constant holding the query to get all spent hours on list of tasks. QUERY: {@value #JPQL_TIMESHEET_HOURSSPENT_BY_TASK}.
	 */
    String JPQL_TIMESHEET_HOURSSPENT_BY_TASK = "SELECT  DISTINCT wl.task.id,wl.task.estimatedHours,sum(wl.hoursSpent) FROM  Worklog wl,Task task WHERE wl.task IN (?1) GROUP BY wl.task.id";
    
    /**
	 * A constant holding the query to get spent hours by task id. QUERY: {@value #JPQL_TIMESHEET_HOURSSPENT_BY_TASK_ID}.
	 */
    String JPQL_TIMESHEET_HOURSSPENT_BY_TASK_ID = "SELECT sum(wl.hoursSpent) FROM  Worklog wl WHERE wl.task.id = ?1";
    
    /**
	 * A constant holding the query to get spent hours by task id and worklog status. QUERY: {@value #JPQL_TIMESHEET_HOURSSPENT_BY_TASK_ID_WITH_WORKLOG_STATUS}.
	 */
    String JPQL_TIMESHEET_HOURSSPENT_BY_TASK_ID_WITH_WORKLOG_STATUS = "SELECT sum(wl.hoursSpent) FROM  Worklog wl WHERE wl.task.id = ?1 and wl.status.id != ?2";

    /**
	 * A constant holding the query to update the status of worklog by a person and project. QUERY: {@value #JPQL_TIMESHEET_UPDATE_STATUS_WITH_PROJECT}.
	 */
    String JPQL_TIMESHEET_UPDATE_STATUS_WITH_PROJECT = "UPDATE Worklog wl SET wl.status = ?3, wl.lastUpdatedBy = ?4, wl.lastUpdatedTime = ?5 WHERE wl.person = ?1 AND wl.date IN (?2) AND wl.project IN(?6)";
    
    /**
	 * A constant holding the query to get worklog by person and date. QUERY: {@value #JPQL_TIMESHEET_SELECT_PERSON_AND_DATE}.
	 */
    String JPQL_TIMESHEET_SELECT_PERSON_AND_DATE = "SELECT wl FROM Worklog wl WHERE wl.person = ?1 AND wl.date IN (?2)";
    
    /**
	 * A constant holding the query to get  worklog by person, project and date. QUERY: {@value #JPQL_TIMESHEET_SELECT_PERSON_AND_PROJECT_AND_DATE}.
	 */
    String JPQL_TIMESHEET_SELECT_PERSON_AND_PROJECT_AND_DATE = "SELECT wl FROM Worklog wl WHERE wl.person = ?1 AND wl.project IN(?2) AND wl.date IN (?3) ";
    
    /**
	 * A constant holding the query to get hoursSpent by worklog id. QUERY: {@value #JPQL_TIMESHEET_HOURSSPENT_BY_WORKLOG_ID}.
	 */
    String JPQL_TIMESHEET_HOURSSPENT_BY_WORKLOG_ID ="SELECT wl.hoursSpent FROM Worklog wl WHERE wl.id = ?1";
    
    /**
	 * A constant holding the query to get all distinct persons by project status as submitted. QUERY: {@value #JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS_SUBMITTED}.
	 */
    String JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS_SUBMITTED  = "SELECT DISTINCT wl.person FROM Worklog wl WHERE wl.project IN(?1) AND wl.person.status = true AND (wl.project.projectName LIKE %?2% OR wl.person.empName LIKE %?2%) AND wl.date > CURRENT_DATE -60 AND wl.person.id != ?3 AND wl.person.role.id > (SELECT person.role.id FROM Person person WHERE person.id = ?3)";
    
    /**
	 * A constant holding the query to get all distinct persons by project status as submitted without search. QUERY: {@value #JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS_SUBMITTED_WITHOUTSEARCH}.
	 */
    String JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS_SUBMITTED_WITHOUTSEARCH = "SELECT DISTINCT wl.person FROM Worklog wl WHERE wl.project IN(?1) AND wl.person.status = true AND wl.date > CURRENT_DATE -60 AND wl.person.id != ?2 AND wl.person.role.id > (SELECT person.role.id FROM Person person WHERE person.id = ?2)";
    
    /**
	 * A constant holding the query to get all distinct person by projects, and logged in user id. QUERY: {@value #JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS}.
	 */
    String JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS = "SELECT DISTINCT wl.person FROM Worklog wl WHERE wl.project IN(?1) AND wl.person.status = true AND (wl.project.projectName LIKE %?2% OR wl.person.empName LIKE %?2%) AND wl.status.id = ?3 AND wl.date > CURRENT_DATE -60 AND wl.person.id != ?4 AND wl.person.role.id > (SELECT person.role.id FROM Person person WHERE person.id = ?4)";
    
    /**
	 * A constant holding the query to get all distinct person by project and without search text. QUERY: {@value #JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS_WITHOUTSEARCH}.
	 */
    String JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS_WITHOUTSEARCH = "SELECT DISTINCT wl.person FROM Worklog wl WHERE wl.project IN(?1) AND wl.person.status = true  AND wl.status.id = ?2 AND wl.date > CURRENT_DATE -60 AND wl.person.id != ?3 AND wl.person.role.id > (SELECT person.role.id FROM Person person WHERE person.id = ?3)";
    
    /**
	 * A constant holding the query to get distinct persons by project and without worklog status. QUERY: {@value #JPQL_TIMESHEET_PERSON_BY_PROJECTS_WITHOUT_WORKLOG_STATUS}.
	 */
    String JPQL_TIMESHEET_PERSON_BY_PROJECTS_WITHOUT_WORKLOG_STATUS = "SELECT DISTINCT wl.person FROM Worklog wl WHERE wl.project IN(?1) AND wl.person.status = true  AND (wl.project.projectName LIKE %?2% OR wl.person.empName LIKE %?2%) AND wl.date > CURRENT_DATE -60 AND wl.person.id != ?3 AND wl.person.role.id > (SELECT person.role.id FROM Person person WHERE person.id = ?3)";
    
    /**
	 * A constant holding the query to get distinct persons by project, without worklog status and search text. QUERY: {@value #JPQL_TIMESHEET_PERSON_BY_PROJECTS_WITHOUT_WORKLOG_STATUS_SEARCHTEXT}.
	 */
    String JPQL_TIMESHEET_PERSON_BY_PROJECTS_WITHOUT_WORKLOG_STATUS_SEARCHTEXT = "SELECT DISTINCT wl.person FROM Worklog wl WHERE wl.project IN(?1) AND wl.person.status = true  AND wl.date > CURRENT_DATE -60 AND wl.person.id != ?2 AND wl.person.role.id > (SELECT person.role.id FROM Person person WHERE person.id = ?2)";
    
    /**
	 * A constant holding the query to get all projects ids for manager. QUERY: {@value #JPQL_TIMESHEET_GET_PROJECTS_IDS_FOR_MANAGER}.
	 */
    String JPQL_TIMESHEET_GET_PROJECTS_IDS_FOR_MANAGER = "SELECT project.id FROM Project project WHERE project.id IN (SELECT ppp.project.id FROM ProjectPerson ppp WHERE ppp.person.id=?1 AND (ppp.roleId=1 OR ppp.roleId=2 OR ppp.roleId=3)) OR project.globalProject = true";
    
    /**
	 * A constant holding the query to get all  projects ids for Admin. QUERY: {@value #JPQL_TIMESHEET_GET_PROJECTS_IDS_FOR_ADMIN}.
	 */
    String JPQL_TIMESHEET_GET_PROJECTS_IDS_FOR_ADMIN = "SELECT project.id FROM Project project WHERE project.projectStatus = ?1";
    
    /**
	 * A constant holding the query to get all worklog for list of persons. QUERY: {@value #JPQL_TIMESHEET_GET_TIMESHEETS_FOR_LIST_OF_USERS}.
	 */
    String JPQL_TIMESHEET_GET_TIMESHEETS_FOR_LIST_OF_USERS = "SELECT wl FROM Worklog wl WHERE wl.person IN (?1) AND wl.date >= ?2 AND wl.date <= ?3";
    
    /**
	 * A constant holding the query to get worklog by person id. QUERY: {@value #JPQL_TIMESHEET_FIND_TIMESHEET_FOR_PERSON}.
	 */
    String JPQL_TIMESHEET_FIND_TIMESHEET_FOR_PERSON = "FROM Worklog wl WHERE wl.person.id = ?1";
    
    /**
	 * A constant holding the query to get worklog of previous day by a person. QUERY: {@value #JPQL_TIMESHEET_FIND_TIMESHEET_LIST_FOR_PREVIOUS_DAY_FOR_PERSON}.
	 */
    String JPQL_TIMESHEET_FIND_TIMESHEET_LIST_FOR_PREVIOUS_DAY_FOR_PERSON = "SELECT wl FROM Worklog wl WHERE wl.person=?1 AND wl.date = ?2 AND wl.status.id in(1,2)";
    
    /**
     * A constant holding the query to get the list of users who have entered timesheet against the given project in given duration.
     */
    String JPQL_FETCH_USERS_WITH_TIMESHEET_FOR_PROJECT = "SELECT DISTINCT person from Person person WHERE person.id IN ( SELECT wl.person.id from Worklog wl WHERE wl.project.id = ?1 AND wl.date >= ?2 AND wl.date <= ?3) OR person.id IN (SELECT pp.person.id from ProjectPerson pp WHERE pp.project.id = ?1) ORDER BY person.empName ASC";
    
    String JPQL_GET_PMO_REPORT = "SELECT wl from Worklog wl WHERE wl.person.id in (?1) AND  wl.date >= ?2 AND wl.date <= ?3 ORDER BY wl.person.id, wl.date";
    
    String JPQL_GET_UNAPPROVED_WORKLOG_FORTASK_BYID = "SELECT worklog FROM Worklog worklog WHERE worklog.status <> 2 AND worklog.task.id = ?1";
    
    String SQL_DELETE_13MONTH_OLD_ENTRIES= "DELETE FROM orion.worklog WHERE worklog.task_id IN (?1)";
    
    String JPQL_GET_REJECTED_WORKLOGS_FOR_PERSON="Select wl FROM Worklog wl WHERE wl.person = ?1 AND wl.status.id = 3 and wl.date between ?2 AND ?3";
    
    String JPQL_GET_REJECTED_WORKLOGS="SELECT wl FROM Worklog wl WHERE wl.person=?1 AND wl.project=?2 AND wl.status.id = 3 and wl.date between ?3 AND ?4";
    
    String JPQL_FIND_WORKLOG_BY_PERSON_PROJECT_DATE="SELECT wl FROM Worklog wl WHERE wl.person=?1 AND wl.project=?2 AND wl.date = ?3 AND wl.status.id in(1,2)";
    /**
     * This method is used to get list of worklogs by a person, start and end date.
     * 
     * @param person must not be {@literal null}
     * @param startDate must not be {@literal null}
     * @param endDate must not be {@literal null}
     * @return list of worklog for given person's start date and end date or {@literal null} if none found
     */
    List<Worklog> findByPersonAndDateBetweenOrderByIndexAsc(Person person, Date startDate, Date endDate);
    
    /**
     * This method is used to get list of worklogs by a person and start date and end date using ordering.
     * 
     * @param person must not be {@literal null}
     * @param startDate must not be {@literal null}
     * @param endDate must not be {@literal null}
     * @return list of worklogs by given person and start date and end date or {@literal null} if none found
     */
    Stream<Worklog> findByPersonAndDateBetweenOrderByIndex(Person person, Date startDate, Date endDate);
    
    /**
     * 
     * @param person
     * @param project
     * @param task
     * @param date
     * @return
     */
    Worklog findByPersonAndProjectAndTaskAndDate(Person person, Project project, Task task, Date date);
    
    /**
     * 
     * @param person
     * @param date
     * @return
     */
    List<Worklog> findByPersonAndDate(Person person, Date date);

    /**
     * This method is used to get list of worklogs by person id.
     * 
     * @param PersonId must not be {@literal null}
     * @return list of worklogs by given person id or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_FIND_TIMESHEET_FOR_PERSON)
    List<Worklog> findTimesheet(long PersonId);
    
    /**
     * This method is used to get list of worklogs by a person and previous date
     * 
     * @param person must not be {@literal null}
     * @param date must not be {@literal null}
     * @return list of worklogs by given person and previous date or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_FIND_TIMESHEET_LIST_FOR_PREVIOUS_DAY_FOR_PERSON)
    List<Worklog> findEntryFroPreviousDay(Person person,Date date);
    
    /**
     * 
     * @param person
     * @param project
     * @param date
     * @return
     */
    @Query(JPQL_FIND_WORKLOG_BY_PERSON_PROJECT_DATE)
    List<Worklog> findByPersonAndProjectAndDate(Person person, Project project, Date date);
    
    /**
     * 
     * @param person
     * @param project
     * @param startdate
     * @param endDate
     * @return
     */
    @Query(JPQL_GET_REJECTED_WORKLOGS)
    List<Worklog> getRejectedWorklogs(Person person, Project project, Date startdate,Date endDate);
    
    /**
     * 
     * @param person
     * @param startdate
     * @param endDate
     * @return
     */
    @Query(JPQL_GET_REJECTED_WORKLOGS_FOR_PERSON)
    List<Worklog> getRejectedWorklogsForPerson(Person person, Date startdate,Date endDate);
    /**
     * This method is used to get all distinct projects by a person, start date and end date.
     * 
     * @param person must not be {@literal null}
     * @param startDate must not be {@literal null}
     * @param endDate must not be {@literal null}
     * @return all distinct projects by  given person, start date and end date or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_PROJECTS)
    List<Object> distinctProjects(Person person, Date startDate, Date endDate);

    /**
     * This method is used to approve timesheet by a person.
     * 
     * @param person must not be {@literal null}
     * @param date must not be {@literal null}
     * @param status must not be {@literal null}
     * @param updatedBy must not be {@literal null}
     * @param updatedDate must not be {@literal null}
     */
    @Modifying
    @Query(JPQL_TIMESHEET_APPROVE_STATUS)
    void approveTimeSheet(Person person, Date date, WorklogStatus status, Person updatedBy, Date updatedDate);

    /**
     * This method is used to update the all timesheets status by a person 
     *  
     * @param person must not be {@literal null}
     * @param formattedDateList must not be {@literal null}
     * @param status must not be {@literal null}
     * @param updatedBy must not be {@literal null}
     * @param date must not be {@literal null}
     */
    @Modifying
    @Query(JPQL_TIMESHEET_UPDATE_STATUS)
    void updateTimeSheetStatus(Person person, List<Date> formattedDateList, WorklogStatus status, Person updatedBy, Date date);

    /**
     * This method is used to get all spent hours by list of tasks.
     * 
     * @param tasks must not be {@literal null}
     * @return all spent hours by given list of tasks or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_HOURSSPENT_BY_TASK)
    List<Object[]> sumOfHoursByTask(List<Task> tasks);
    
    /**
     * This method is used to get all spent hours by a task id.
     *  
     * @param id must not be {@literal null}
     * @return all spent hours by given task id or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_HOURSSPENT_BY_TASK_ID)
    Double sumOfHoursByTaskId(long id);
    
    /**
     * This method is used to get all spent hours by task id and worklog status.
     * 
     * @param id must not be {@literal null}
     * @param worklogStatusId must not be {@literal null}
     * @return all spent hours by given task id and worklog status or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_HOURSSPENT_BY_TASK_ID_WITH_WORKLOG_STATUS)
    Double sumOfHoursByTaskIdAndWorklogStatus(long id, long worklogStatusId);

    /**
     * This method is used to update the timesheet for a person of all associated projects.
     * 
     * @param person must not be {@literal null}
     * @param formattedDateList must not be {@literal null}
     * @param status must not be {@literal null}
     * @param updatedBy must not be {@literal null}
     * @param date must not be {@literal null}
     * @param projects must not be {@literal null}
     */
    @Modifying
    @Query(JPQL_TIMESHEET_UPDATE_STATUS_WITH_PROJECT)
    void updateTimeSheetStatusForProject(Person person, List<Date> formattedDateList, WorklogStatus status, Person updatedBy, Date date, List<Project> projects);

    /**
     * This method is used to get spent hours by a worklog id. 
     * 
     * @param worklogId must not be {@literal null}
     * @return spent hours by given worklog id or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_HOURSSPENT_BY_WORKLOG_ID)
    Double getHoursSpentById(long worklogId);
    
    /**
     * This method is used to get all worklog by a person and list of dates
     * 
     * @param person must not be {@literal null}
     * @param formattedDateList must not be {@literal null}
     * @return list of worklog by given person and list of dates or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_SELECT_PERSON_AND_DATE)
    List<Worklog> findByPersonAndDates(Person person, List<Date> formattedDateList);

    /**
     * This method is used to get list of worklogs by a person, list of projects and list of dates.
     * 
     * @param person must not be {@literal null}
     * @param projects must not be {@literal null}
     * @param formattedDateList must not be {@literal null}
     * @return list of worklog by a person and list of projects or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_SELECT_PERSON_AND_PROJECT_AND_DATE)
    List<Worklog> findByPersonAndProjectsAndDates(Person person, List<Project> projects, List<Date> formattedDateList);

    /**
     * This method is used to get all person list whoever submitted their timesheet by a list of projects and search text.
     * 
     * @param projects must not be {@literal null}
     * @param searchText must not be {@literal null}
     * @param loggedInuserID must not be {@literal null}
     * @return list of person by given project's list or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS_SUBMITTED)
    List<Person> findUsersByProjectsAndSubmitted(List<Project> projects,String searchText,long loggedInuserID);

    /**
     * This method is used to get list of persons by list of projects , searchText and worklog status.
     * 
     * @param projects must not be {@literal null}
     * @param searchText must not be {@literal null}
     * @param worklogStatusId must not be {@literal null}
     * @param loggedInuserID must not be {@literal null}
     * @return list of person by given projects list, searchText and worklog status or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS)
    List<Person> getUsersByProject(List<Project> projects, String searchText, long worklogStatusId,long loggedInuserID);

    /**
     * This method is used to get all persons by list of projects without checking the status.
     * 
     * @param projects must not be {@literal null}
     * @param searchText must not be {@literal null}
     * @param loggedInuserID must not be {@literal null}
     * @return list of person by given list of projects and searchText or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_PERSON_BY_PROJECTS_WITHOUT_WORKLOG_STATUS)
    List<Person> getUsersByProjectWithoutStatus(List<Project> projects, String searchText,long loggedInuserID);
    
    /**
     * This method is used to get list of persons by list of projects without status and without searchText.
     * 
     * @param projects must not be {@literal null}
     * @param loggedInuserID must not be {@literal null}
     * @return list of persons by given list of projects or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_PERSON_BY_PROJECTS_WITHOUT_WORKLOG_STATUS_SEARCHTEXT)
    List<Person> getUsersByProjectWithoutStatusWithoutSearchText(List<Project> projects,long loggedInuserID);

    /**
     * This method is used to get list of persons by worklog status as submitted and list of projects.
     * 
     * @param projects must not be {@literal null}
     * @param loggedInuserID must not be {@literal null}
     * @return list of persons by given status as submitted and list of projects or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS_SUBMITTED_WITHOUTSEARCH)
    List<Person> findUsersByProjectsAndSubmittedWithoutSearchText(List<Project> projects,long loggedInuserID);

    /**
     * This method is used to get list of persons by list of projects and status of worklog.
     * 
     * @param projects must not be {@literal null}
     * @param worklogStatusId must not be {@literal null}
     * @param loggedInuserID must not be {@literal null}
     * @return list of persons by given list of project and worklog status  or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_PERSON_BY_PROJECTS_STATUS_WITHOUTSEARCH)
    List<Person> getUsersByProjectWithoutSearchText(List<Project> projects, long worklogStatusId,long loggedInuserID);

    /**
     * This method is used to get all projects ids by person id.
     * 
     * @param personId must not be {@literal null}
     * @return list of projects ids by given person id or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_GET_PROJECTS_IDS_FOR_MANAGER)
    List<Long> getProjectsIdsForManager(long personId);
    
    /**
     * This method is used to get list of all projects ids by project status for admin.
     * 
     * @param projectStatus must not be {@literal null}
     * @return list of projects ids by given projects status or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_GET_PROJECTS_IDS_FOR_ADMIN)
    List<Long> getProjectsIdsForAdmin(boolean projectStatus);
    
    /**
     * This method is used to get all worklog list by list of persons, start date and end date.
     * 
     * @param users must not be {@literal null}
     * @param startDate must not be {@literal null}
     * @param endDate must not be {@literal null}
     * @return list of worklog by given list of persons or {@literal null} if none found
     */
    @Query(JPQL_TIMESHEET_GET_TIMESHEETS_FOR_LIST_OF_USERS)
    List<Worklog> getTimeSheetForListOfUsers(List<Person> users, Date startDate, Date endDate);

    /**
     * 
     * @param id
     * @return
     */
    @Query(JPQL_GET_UNAPPROVED_WORKLOG_FORTASK_BYID)
    List<Worklog> getUnApprovedWorkLogForTaskById(Long id);
    
    /**
     * 
     * @param projectId
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(JPQL_FETCH_USERS_WITH_TIMESHEET_FOR_PROJECT)
    List<Person> getUsersWithTimesheetForProject(long projectId,Date startDate,Date endDate);
    
    /**
     * 
     * @param userIds
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(JPQL_GET_PMO_REPORT)
    List<Worklog> getReportForPMO(List<Long> userIds, Date startDate,Date endDate);
    
    @Modifying
    @Query(value=SQL_DELETE_13MONTH_OLD_ENTRIES, nativeQuery=true)
    void purgeOldEntries(List<Long> taskids);
    
}
