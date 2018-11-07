package com.nalashaa.timesheet.model;

import java.util.List;

import com.nalashaa.timesheet.entity.Worklog;

public class BulkTimesheetDTO {
	private List<Worklog> timeEntries;
	private List<Long> projectIds;
	public List<Worklog> getTimeEntries() {
		return timeEntries;
	}
	public void setTimeEntries(List<Worklog> timeEntries) {
		this.timeEntries = timeEntries;
	}
	public List<Long> getProjectIds() {
		return projectIds;
	}
	public void setProjectIds(List<Long> projectIds) {
		this.projectIds = projectIds;
	}
}
