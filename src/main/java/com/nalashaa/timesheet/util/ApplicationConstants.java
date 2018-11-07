package com.nalashaa.timesheet.util;


public interface ApplicationConstants {

    public final String PERSON_STATUS_ACTIVE = "ACTIVE";
    public final String PERSON_STATUS_INACTIVE = "INACTIVE";
    
    public final String PROJECT_STATUS_OPEN = "OPEN";
    public final String PROJECT_STATUS_CLOSED = "CLOSED";
    public final String PROJECT_STATUS_ONHOLD = "ONHOLD";
    public final String PROJECT_TYPE_FIXED = "FIXED";
    public final String PROJECT_TYPE_ONHOLD = "VARIABLE";
    
    public final String TASK_STATUS_OPEN = "OPEN";
    public final String TASK_STATUS_CLOSED = "CLOSED";
    public final String TASK_STATUS_ONHOLD = "ONHOLD";
    public final String TASK_TYPE_FIXED = "FIXED";
    public final String TASK_TYPE_ONHOLD = "VARIABLE";
    
    public final String WORKLOG_STATUS_SUBMITTED = "SUBMITTED";
    public final String WORKLOG_STATUS_APPROVED = "APPROVED";
    public final String WORKLOG_STATUS_REJECTED = "REJECTED";
    public final String WORKLOG_STATUS_REVERTED = "REVERTED";
    /** The Constant BLANK. */
	public static final String BLANK = "";
	public static final String SPACE= "\n";
    
    public final String DATE_PATTERN = "yyyy-MM-dd";
}
