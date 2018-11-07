
package com.nalashaa.timesheet.service.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.nalashaa.timesheet.Exception.TimeSheetException;
import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.entity.TaskPerson;
import com.nalashaa.timesheet.entity.Worklog;
import com.nalashaa.timesheet.model.TaskSetup;
import com.nalashaa.timesheet.repository.IAssignResourceDAO;
import com.nalashaa.timesheet.repository.IPersonDAO;
import com.nalashaa.timesheet.repository.IProjectDAO;
import com.nalashaa.timesheet.repository.IProjectPersonDAO;
import com.nalashaa.timesheet.repository.ITaskDAO;
import com.nalashaa.timesheet.repository.ITaskPersonDAO;
import com.nalashaa.timesheet.repository.IWorkLogDAO;
import com.nalashaa.timesheet.service.ITaskService;

/**
 * @author siva
 *
 */
@Service
@Transactional
public class TaskServiceImpl implements ITaskService {

    private static final Logger logger = LogManager.getLogger(TaskServiceImpl.class);

    @Autowired
    ITaskDAO taskRepository;
    
    @Autowired
    IPersonDAO personRespository;
    
    @Autowired
    IWorkLogDAO workLogRepository;
    
    @Autowired
    ITaskPersonDAO taskPersonRepository;
    
    @Autowired
    IWorkLogDAO worklogRepository;
    
    @Autowired
    IProjectDAO projRespository;
    
    @Autowired
    IAssignResourceDAO assignResourceRepository;
    
    @Autowired
    IProjectPersonDAO projectPersonRepository;

    /* (non-Javadoc)
     * @see com.nalashaa.timesheet.service.ITaskService#createTask(com.nalashaa.timesheet.entity.Task)
     */
    @Override
    public Task createTask(Task task) {
        logger.info("Entered : createTask");
        return taskRepository.save(task);
    }

    /* (non-Javadoc)
     * @see com.nalashaa.timesheet.service.ITaskService#findTask(java.lang.String)
     */
    @Override
    public List<Task> findTask(String searchString) {
        logger.info("Entered : findTask");
        return taskRepository.findByTaskName(searchString);
    }

    /* (non-Javadoc)
     * @see com.nalashaa.timesheet.service.ITaskService#getTasks()
     */
    // todo consumedhrs logic
    @Override
    public List<Task> getTasks(long personId) {
        logger.info("Entered : getTasks by Person");
        List<Project> projects = null;
        Person person = personRespository.getOne(personId);
        if(person != null && person.getRole().getId()==1)
        	projects = projRespository.findByProjectStatusTrue();
        else
        	projects = projectPersonRepository.findAllProjectsByPersonAndRole(personId);        
        List<Task> tasklist = new ArrayList<>();
        for(Project project: projects) {
        	if(!project.getProjectStatus())
    			continue;
        	List<Task> tasks = taskRepository.findByProjectAndStatusTrue(project);
        	for(Task task: tasks) {
        		List<Person> taskPersonmapped = taskPersonRepository.getPersonsByTask(task);
        		Double hoursSpent = workLogRepository.sumOfHoursByTaskId(task.getId());
        		task.setAssignedUsers(taskPersonmapped);
        		task.setHoursConsumed(hoursSpent);
        	}
        	tasklist.addAll(tasks);
        }
        return tasklist;
    }

    @Override
    public List<Task> getTasksByProject(long projectId) {
        logger.info("Entered : getTasksByProject");
        Project project = new Project();
        project.setId(projectId);
        List<Task> tasksWithHoursSpent = new ArrayList<>();
        List<Task> tasks = taskRepository.findByProjectAndStatusTrue(project);
        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                Double hoursSpent = worklogRepository.sumOfHoursByTaskId(task.getId());
                if (hoursSpent != null && 0.0 < task.getEstimatedHours()) {
                    task.setHoursLeft(task.getEstimatedHours() - hoursSpent);
                } else {
                    task.setHoursLeft(task.getEstimatedHours());
                }
                List<Person> taskPerson = taskPersonRepository.getPersonsByTask(task);
                task.setAssignedUsers(taskPerson);
                tasksWithHoursSpent.add(task);
            }
            return tasksWithHoursSpent;
        } else {
            return tasks;
        }
    }

    @Override
    public List<Task> getTasksByProjectAndPerson(long projectId, long personId,Date monthStartDate) {
        logger.info("Entered : getTasksByProjectAndPerson");
        List<Task> tasksWithHoursSpent = new ArrayList<>();
        Project project  = projRespository.findById(projectId);
        if(!project.isGlobalProject()){
	        List<Task> tasks = taskPersonRepository.getTasksByProjectAndPerson(projectId,personId,monthStartDate);
	        List<Task> assignToteamTasks = taskRepository.findByProjectAndAssignTeamTrue(project);
	        tasks.addAll(assignToteamTasks);
	            if (!tasks.isEmpty()) {
	                for (Task task : tasks) {
	                    Double hoursSpent = worklogRepository.sumOfHoursByTaskId(task.getId());
	                    if ( null != hoursSpent && 0.0 < task.getEstimatedHours()) {
	                        task.setHoursLeft(task.getEstimatedHours() - hoursSpent);
	                    } else {
	                        task.setHoursLeft(task.getEstimatedHours());
	                    }
	                    if(task.isActive()) {
	                    	tasksWithHoursSpent.add(task);	
	                    }
	                }
	            }
        }else{
            tasksWithHoursSpent = taskRepository.findByProjectAndStatusTrue(project);
            tasksWithHoursSpent = tasksWithHoursSpent.stream().filter(task -> task.isActive()).collect(Collectors.toList());
        }
        return tasksWithHoursSpent;
    }

    @Override
    public Task getTask(long taskId) {
        logger.info("Entered : getTask");
        return taskRepository.findById(taskId);
    }

    @Override
    public Task taskSetup(Task task) {
        logger.info("Entered : taskSetup");
        Task taskSaved = null;
        task.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        try {
        taskSaved = taskRepository.save(task);
        }
        catch(Exception ex) {
        	throw new TimeSheetException("Task is already registered with this task Id");
        }
        //taskAudit(taskSaved);
        List<Person> personList = new ArrayList<Person>();
        if(task.getAssignedUsers() != null)
        	personList.addAll(task.getAssignedUsers());
        List<Person> taskPersonmapped = taskPersonRepository.getPersonsByTask(task);
        List<TaskPerson> savePersonList = new ArrayList<>();
        for (Person Person : personList) {
            boolean mapped = false;
            for (Person user : taskPersonmapped) {
                if(Person.getId() == user.getId()){
                    mapped = true;
                    break;
                }
            }
            if(!mapped){
            TaskPerson taskPers = new TaskPerson();
            taskPers.setTask(taskSaved);
            taskPers.setPerson(Person);
            savePersonList.add(taskPers);
            }
        }
        List<TaskPerson> taskPerson = taskPersonRepository.save(savePersonList);
        List<Person> taskPersonSaved = taskPersonRepository.getPersonsByTask(task);
        return taskSaved;
    }

    @Override
    public TaskSetup getTaskSetup(long taskId) {
        logger.info("Entered : getTaskSetup");
        TaskSetup taskSetup = new TaskSetup();
        Task task = taskRepository.findById(taskId);
        List<Person> taskPerson = taskPersonRepository.getPersonsByTask(task);
        taskSetup.setTask(task);
        taskSetup.setUsers(taskPerson);
        return taskSetup;
    }

   
    @Override
    @Transactional
    @Modifying
    public void deleteTask(long id) {
        logger.info("Entered : deleteTask");
        Task task = taskRepository.findById(id);
        List<Worklog> worklogList=worklogRepository.getUnApprovedWorkLogForTaskById(id);
        for(Worklog worklog:worklogList) {
        	if(worklog != null) {
        		throw new TimeSheetException("Please approve all the timesheet hours for this task before deleting.");
        	}
        }
        taskPersonRepository.deleteByTask(task);
        taskRepository.deleteTask(id);
    }
   
    /**
     * 
     */
    @Override
    public void updateTask(Task task) {
    	
    	task.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
    	Task taskSaved=null;
    	try {
    		taskSaved=taskRepository.save(task);
        } catch (Exception e){
        	throw new TimeSheetException("Task is already registered with this task Id");
        }
    	//taskAudit(taskSaved);
    	List<Person> personList = new ArrayList<Person>();
        if(task.getAssignedUsers() != null)
        	personList.addAll(task.getAssignedUsers());
        taskPersonRepository.deleteByTask(task);
        List<Person> taskPersonmapped = taskPersonRepository.getPersonsByTask(task);
        List<TaskPerson> savePersonList = new ArrayList<>();
        for (Person Person : personList) {
            boolean mapped = false;
            for (Person user : taskPersonmapped) {
                if(Person.getId() == user.getId()){
                    mapped = true;
                    break;
                }
            }
            if(!mapped){
            TaskPerson taskPers = new TaskPerson();
            taskPers.setTask(taskSaved);
            taskPers.setPerson(Person);
            savePersonList.add(taskPers);
            }
        }
        taskPersonRepository.save(savePersonList);
    }
    
    public List<Project> getProjectBycreateTaskRole(long personId){
    	Person person = personRespository.getOne(personId);
    	DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    	Date date = null;
    	try {
			date = format.parse(format.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    		if(person.getRole() != null && person.getRole().getId() == 1)
    			return projRespository.findActiveProjectsForAdminTasks(date);
    	return assignResourceRepository.getProjectsByCreateTaskRole(personId, date);
    }

}
