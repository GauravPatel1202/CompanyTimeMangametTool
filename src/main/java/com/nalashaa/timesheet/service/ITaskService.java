
package com.nalashaa.timesheet.service;

import java.util.Date;
import java.util.List;

import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.model.TaskSetup;

public interface ITaskService {

    Task createTask(Task task);

    List<Task> findTask(String searchString);

    List<Task> getTasks(long personId);

    List<Task> getTasksByProject(long projectId);

    List<Task> getTasksByProjectAndPerson(long projectId, long personId,Date monthStartDate);

    Task getTask(long taskId);

    Task taskSetup(Task task);

    TaskSetup getTaskSetup(long taskId);

    void deleteTask(long taskId);
    
    public void updateTask(Task task);
    
    List<Project> getProjectBycreateTaskRole(long personId);


}
