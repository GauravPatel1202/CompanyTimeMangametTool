/**
 * 
 */

package com.nalashaa.timesheet.model;

import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Task;

/**
 * @author siva
 *
 */

public class DistinctProject {

    private Project project;
    
    private Task task;
    
    private long index;
    
    private double hoursLeft;

    private boolean isApprover;
    
    public Project getProject() {
        return project;
    }

    
    public void setProject(Project project) {
        this.project = project;
    }

    
    public Task getTask() {
        return task;
    }

    
    public void setTask(Task task) {
        this.task = task;
    }

    
    public long getIndex() {
        return index;
    }

    
    public void setIndex(long index) {
        this.index = index;
    }

    /**
     * @return the hoursLeft
     */
    public double getHoursLeft() {
        return hoursLeft;
    }


    
    /**
     * @param hoursLeft the hoursLeft to set
     */
    public void setHoursLeft(double hoursLeft) {
        this.hoursLeft = hoursLeft;
    }


    /**
     * @return the isApprover
     */
    public boolean isApprover() {
        return isApprover;
    }


    
    /**
     * @param isApprover the isApprover to set
     */
    public void setApprover(boolean isApprover) {
        this.isApprover = isApprover;
    }
    
}
