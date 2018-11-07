package com.nalashaa.timesheet.constant;

/**
 * This class contains all the endpoints.
 * @author ashwanikannojia
 *
 */
public class Endpoints {

	 public static final String DEFAULT="/";
	 public static final String ALL_COUNTRIES="/countries";
	 public static final String GET_ALL_ACTIVE_CLIENTS="/clients";
	 public static final String LOGIN="/login";
	 public static final String CHANGE_PASSWORD="/login/changePassword";
	 public static final String FORGET_PASSWORD="/login/forgetPassword";
	 public static final String GET_PROJECTS_BY_PERSON="/getProjectsByPerson/{personId}";
	 public static final String GET_PROJECT_BY_PERSON_AS_MANAGER="/getProjectsByPersonAsManager/{personId}";
	 public static final String CREATE_PROJECT="/createProject";
	 public static final String UPDATE_PROJECT="/updateProject";
	 public static final String GET_PROJECT_BY_PERSON_ACTIVE_WITH_TASKS="/getProjectsByPersonActiveWithTasks/{personId}/{monthStartDate}";
	 public static final String  GET_ROLES_BY_PARENT_ROLE = "/getRolesByParentRole/{roleId}";
	 public static final String  GET_ALL_ROLES = "/getAllRoles";
	 public static final String  GET_TASKS_BY_PROJECT = "/getTasksByProject/{projectId}";
	 public static final String  GET_TASK_SETUP = "/getTaskSetup/{taskId}";
	 public static final String  DELETE_TASK = "/deleteTask/{taskId}";
	 public static final String  GET_TASK_BY_TASK_ID = "/getTask/{id}";
	 public static final String  GET_TASKS_BY_PERSON_ID = "/getTasksByPerson/{personId}";
	 public static final String  CREATE_TASK_SETUP = "/taskSetup";
	 public static final String  UPDATE_TASK = "/updateTask";
	 public static final String  GET_TASKS_BY_PROJECT_AND_PERSON_AND_MONTH_START_DATE = "/getTasksByProjectAndPerson/{projectId}/{personId}/{monthStartDate}";
	 public static final String  GET_PROJECT_BY_ROLE = "/getProjectByPersonAndRole/{personId}";
	 public static final String CREATE_USER = "/createUser";
	 public static final String GET_LIST_OF_RESOURCES = "/resources";
	 public static final String UPDATE_USER = "/updateUser";
	 public static final String GET_DESTINATION_AND_SKILL = "/getDesignationAndSkill";
	 public static final String GET_PROJECTS_AND_RESOURCES = "/getProjectAndResource/{userId}";
	 public static final String ASSIGN_PROJECT_AND_RESOURCE = "/assignProjectAndResource/{userId}";
	 public static final String UPDATE_ASSIGN_PROJECT_AND_RESOURCE = "/updateAssignProjectAndResource/{userId}";
	 public static final String DELETE_ASSIGNED_RESOURCE = "/deleteAssignedResource/{userId}";
	 public static final String GET_USERS_BY_LOGGED_IN_USER_ROLE = "/getUsersByProjectAndRole/{userId}";
	 public static final String GET_TIMESHEET_BY_PERSON= "/getTimesheetByPerson/{loggedInUserId}/{personId}/{startDate}/{endDate}";
	 public static final String SAVE_TIMESHEET= "/saveTimeSheet/{loggedInUserId}";
	 public static final String APPROVE_TIMESHEET= "/approveTimeSheet";
	 public static final String TIMESHEET_STATUS= "/timeSheetStatus";
	 public static final String GET_WORKLOG_STATUS= "/getWoklogStatus";
	 public static final String GET_TIMESHEET_FOR_BULK_APPROVAL= "/getTimesheetForBulkApproval/{loggedInUserId}/{startDate}/{endDate}";
	 public static final String GET_TIMESHEET_APPROVABLE_PROJECTS = "/getapprovableprojects/{personId}";
	 public static final String GET_REPORT_SUMMARY = "/getReportSummary/{loggedInUserId}/{projectId}/{startDate}/{endDate}";
	 public static final String GET_ROLE_IN_PROJECT = "/getRoleInProject/{projectId}/{personId}";
	 public static final String GET_HOLIDAY_LIST = "/getHolidayList/{year}";
	 public static final String UPDATE_HOLIDAY_LIST = "updateHolidayList/{year}/{personId}";
	 public static final String GET_PMO_REPORT = "getpmoreport/{loggedInUserId}/{projectId}/{startDate}/{endDate}";
	 public static final String DELETE_TIMESHEET_ENTRIES = "deletetimesheetentries";
	 public static final String DOWLOAD_JIRA_IMPORT_SAMPLE = "downloadjiratemplate";
}
