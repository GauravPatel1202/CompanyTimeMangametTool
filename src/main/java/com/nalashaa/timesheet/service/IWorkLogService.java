
package com.nalashaa.timesheet.service;

import java.util.Date;
import java.util.List;

import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Worklog;
import com.nalashaa.timesheet.entity.WorklogStatus;
import com.nalashaa.timesheet.model.BulkTimesheetDTO;
import com.nalashaa.timesheet.model.PersonDTO;
import com.nalashaa.timesheet.model.TimeSheet;
import com.nalashaa.timesheet.model.WorkLog;

public interface IWorkLogService {

    WorkLog getTimeSheetByPerson(long loggedInUserId,long personId, Date startDate, Date endDate);

    List<Worklog> saveTimeSheet(List<Worklog> worklog,String personId);

    void approveTimeSheet(TimeSheet timesheet);

    void timeSheetStatus(TimeSheet timesheet);

    List<WorklogStatus> getWoklogStatus();

    List<WorkLog> getWorkLogforUsers(long loggedInUserId, List<PersonDTO> persons, Date startDate, Date endDate); 
    
    List<Worklog> getPMOReport(List<Long> userIds, Date startDate, Date endDate);
    
    void deleteEntries(List<Long> entryIdList);
}
