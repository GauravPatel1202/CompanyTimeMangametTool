
package com.nalashaa.timesheet.service;

import java.util.Date;
import java.util.List;

import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Role;

public interface IProjectService {

    List<Project> getProjectsByPerson(long personId);

    public void createProject(Project project);

    void updateProject(Project project);
    
    List<Project> getProjectsByPersonActiveWithTasks(long personId,Date monthStartDate);

	List<Project> getProjectsByPersonAndRole(long personId);
	
	List<Project> getTimesheetApprovableProjects(long personId);
	
	long getRoleInProject(long projectId, long personId);

}
