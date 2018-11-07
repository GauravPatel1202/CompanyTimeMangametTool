/**
 * 
 */

package com.nalashaa.timesheet.model;

import java.util.List;
import java.util.Map;

import com.nalashaa.timesheet.entity.Worklog;

/**
 * @author siva
 *
 */

public class WorkLog {

private List<DistinctProject> distinctProjects;

private Map<String,List<Worklog>> work;

private Map<String,String> status;

private List<Long> projectIds;


public List<Long> getProjectIds() {
	return projectIds;
}

public void setProjectIds(List<Long> projectIds) {
	this.projectIds = projectIds;
}

public List<DistinctProject> getDistinctProjects() {
    return distinctProjects;
}

public void setDistinctProjects(List<DistinctProject> distinctProjects) {
    this.distinctProjects = distinctProjects;
}


public Map<String, List<Worklog>> getWork() {
    return work;
}


public void setWork(Map<String, List<Worklog>> work) {
    this.work = work;
}

public Map<String, String> getStatus() {
    return status;
}


public void setStatus(Map<String, String> status) {
    this.status = status;
}



}