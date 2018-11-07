package com.nalashaa.timesheet.service.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nalashaa.timesheet.Exception.TimeSheetException;
import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.ProjectPerson;
import com.nalashaa.timesheet.entity.Role;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.entity.Worklog;
import com.nalashaa.timesheet.repository.IPersonDAO;
import com.nalashaa.timesheet.repository.IProjectDAO;
import com.nalashaa.timesheet.repository.IProjectPersonDAO;
import com.nalashaa.timesheet.repository.ITaskDAO;
import com.nalashaa.timesheet.repository.IWorkLogDAO;
import com.nalashaa.timesheet.service.IProjectService;

/**
 * @author vijayganesh
 *
 */
@Service
@Transactional
public class ProjectServiceImpl implements IProjectService {
    
    private static final Logger logger = LogManager.getLogger(ProjectServiceImpl.class);

    @Autowired
    IProjectDAO projectRepository;
    
    @Autowired
    IPersonDAO personRepository;
    
    @Autowired
    IProjectPersonDAO projPerRepository;
    
    @Autowired
    ITaskDAO taskRepository;
    
    @Autowired
    IWorkLogDAO workLogRepository;
    
    @Override
    public List<Project> getProjectsByPerson(long personId) {
        Person person = personRepository.findOne(personId);
        List<Project> projects = new ArrayList<>();
        if (person != null) {
            if (person.getRole().getId() == 1) {
                projects = projectRepository.findByProjectStatusTrue();
            } else {
                projects = projPerRepository.findActiveProjectsByPerson(personId, true);
            }
        }
        return projects;
    }
    
    @Override
    public List<Project> getProjectsByPersonAndRole(long personId) {
        return projPerRepository.findActiveProjectsByPersonAndRole(personId, true);
    }
    
    @Override
    public void createProject(Project project) {
        logger.info("Entered : projectSetup");
        project.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        project.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
        try {
        	projectRepository.save(project);
        } catch (Exception e){
        	throw new TimeSheetException("Project is already registered with this project Id");
        }
    }

    public void updateProject(Project project) {
    	
    	project.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
    	try {
    		if(project.getProjectStatus() == false) {
    			List<Task> taskList=taskRepository.findByProjectAndStatusTrue(project);
        		if(taskList != null) {
        			for(Task task : taskList) {
        			 List<Worklog> worklogList=workLogRepository.getUnApprovedWorkLogForTaskById(task.getId());
        			 for(Worklog workl:worklogList) {
    	    			 if(workl !=null)
    	    				 throw new TimeSheetException("Please approve all the timesheet hours for this project before deleting.");
    	    			}
        			 }
        			 for(Task task1 : taskList) {
    					 task1.setStatus(false);
    					 task1.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
    					 // one parameter need to set i.e. setLastUpdatedBy
    					 taskRepository.save(task1);
    				 }
        		}	
    		}
        	projectRepository.save(project);
        } catch(TimeSheetException e) {
        	throw e;
        } catch (Exception e){
        	throw new TimeSheetException("Project is already registered with this project Id");
        }
    }
    
    @Override
    public List<Project> getProjectsByPersonActiveWithTasks(long personId,Date monthStartDate) {
        logger.info("Entered : getProjectsByPersonActiveWithTasks");
        monthStartDate = formatDate(monthStartDate);
        List<Project> projects = projPerRepository.getProjectsByPersonActiveWithTasks(personId,monthStartDate);
        List<Project> globalProjects = projectRepository.getGlobalProjects();
        if(!globalProjects.isEmpty()){
            projects.addAll(globalProjects);
        }
        return projects;
    }

    private Date formatDate(Date date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (date != null) {
                    String s = df.format(date);
                    String result = s;
                    date = df.parse(result);
            }

        } catch (ParseException e) {
            throw new TimeSheetException("Invalid Date format");
        }
        return date;
    }

	@Override
	public List<Project> getTimesheetApprovableProjects(long personId) {
		Person person = personRepository.getOne(personId);
		if(person.getRole() != null && person.getRole().getId() == 1)
			return projectRepository.findByProjectStatusTrue();
		return projectRepository.getTimesheetApprovableProjects(personId);
	}

	@Override
	public long getRoleInProject(long projectId, long personId) {
		Person person = personRepository.findByIdAndStatusTrue(personId);
		Project project = projectRepository.findById(projectId);
		ProjectPerson projectPerson = projPerRepository.findByProjectAndPerson(project, person);
		if(projectPerson != null) {
			return projectPerson.getRoleId();
		}
		return -1;
	}
}
