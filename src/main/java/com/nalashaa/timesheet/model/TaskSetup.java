
package com.nalashaa.timesheet.model;

import java.util.List;

import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.entity.TaskPerson;

public class TaskSetup {

    private Task task;

    private List<Person> users;
    
    private List<TaskPerson> taskUsers;

    
    /**
     * @return the task
     */
    public Task getTask() {
        return task;
    }

    
    /**
     * @param task the task to set
     */
    public void setTask(Task task) {
        this.task = task;
    }

    
    /**
     * @return the users
     */
    public List<Person> getUsers() {
        return users;
    }

    
    /**
     * @param users the users to set
     */
    public void setUsers(List<Person> users) {
        this.users = users;
    }

    
    /**
     * @return the taskUsers
     */
    public List<TaskPerson> getTaskUsers() {
        return taskUsers;
    }

    
    /**
     * @param taskUsers the taskUsers to set
     */
    public void setTaskUsers(List<TaskPerson> taskUsers) {
        this.taskUsers = taskUsers;
    }

}
