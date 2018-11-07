
package com.nalashaa.timesheet.service.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.nalashaa.timesheet.Exception.TimeSheetException;
import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.entity.Worklog;
import com.nalashaa.timesheet.entity.WorklogStatus;
import com.nalashaa.timesheet.model.DistinctProject;
import com.nalashaa.timesheet.model.PersonDTO;
import com.nalashaa.timesheet.model.TimeSheet;
import com.nalashaa.timesheet.model.WorkLog;
import com.nalashaa.timesheet.repository.IHolidayDAO;
import com.nalashaa.timesheet.repository.IPersonDAO;
import com.nalashaa.timesheet.repository.IProjectDAO;
import com.nalashaa.timesheet.repository.IProjectPersonDAO;
import com.nalashaa.timesheet.repository.IWorkLogDAO;
import com.nalashaa.timesheet.repository.IWorkLogStatusDAO;
import com.nalashaa.timesheet.service.IEmailService;
import com.nalashaa.timesheet.service.IWorkLogService;
import com.nalashaa.timesheet.util.ApplicationConstants;

/**
 * @author siva
 *
 */
@Service
@Transactional
public class WorkLogServiceImpl implements IWorkLogService {

    private static final Logger logger = LogManager.getLogger(WorkLogServiceImpl.class);

    @Autowired
    IWorkLogDAO workLogRepository;

    @Autowired
    IPersonDAO personRepository;

    @Autowired
    IWorkLogStatusDAO workLogStatusRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    IProjectPersonDAO projectPersonRepository;

    @Autowired
    IProjectDAO projRepository;
    
    @Autowired
    IHolidayDAO holidayrepo;
    
    @Autowired
    IEmailService emailService;
    
    /* (non-Javadoc)
     * @see com.nalashaa.timesheet.service.IWorkLogService#getTimeSheetByPerson(long, java.util.Date, java.util.Date)
     */
    @Override
    /**
     * 
     */
    public WorkLog getTimeSheetByPerson(long loggedInUserId, long personId, Date startDate, Date endDate) {
        logger.info("Entered : getTimeSheetByPerson");
        WorkLog worklog = new WorkLog();
        List<DistinctProject> distinctProjs = new ArrayList<>();
        Person person = personRepository.findOne(personId);
        Stream<Worklog> workStream = workLogRepository.findByPersonAndDateBetweenOrderByIndex(person, startDate, endDate);
        Map<Date, List<Worklog>> worklogs = workStream.sorted(Comparator.comparing(Worklog::getDate)).collect(Collectors.groupingBy(Worklog::getDate));
        Set<Date> keys = worklogs.keySet();
        Map<String, List<Worklog>> finalWorklogs = new HashMap<>();
        Map<String, String> statuses = new HashMap<>();
        Person admin = personRepository.getOne(loggedInUserId);
        for (Date date : keys) {
            String pattern = ApplicationConstants.DATE_PATTERN;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String simpleDate = dateToString(date);
            simpleDateFormat.format(date);
            finalWorklogs.put(simpleDate, worklogs.get(date));

            if (null != worklogs.get(date).get(0).getStatus()) {
                statuses.put(simpleDate, worklogs.get(date).get(0).getStatus().getStatus());
            }
        }
        worklog.setWork(finalWorklogs);

        List<Object> distinctProjects = workLogRepository.distinctProjects(person, startDate, endDate);
        for (Object distinctProject : distinctProjects) {
            DistinctProject disProj = new DistinctProject();
            Object[] obj = (Object[]) distinctProject;
            disProj.setProject((Project) obj[0]);
            Task task = (Task) obj[1];
            Double hoursSpent = workLogRepository.sumOfHoursByTaskId(task.getId());
            if (null!= hoursSpent && 0 < hoursSpent && 0.0 < task.getEstimatedHours()) {
                task.setHoursLeft(task.getEstimatedHours() - hoursSpent);
                disProj.setHoursLeft(task.getEstimatedHours() - hoursSpent);
            } else {
                task.setHoursLeft(task.getEstimatedHours());
                disProj.setHoursLeft(task.getEstimatedHours());
            }

            disProj.setTask(task);
            disProj.setIndex((int) obj[2]);
            distinctProjs.add(disProj);
        }
        /*
         * To flag for approver option
         */
        List<DistinctProject> distiProjsWithApproveList = new ArrayList<>();
        DistinctProject distiProjsWithApprove = null;
        List<Project> projectsAssigned = null;
        if(admin != null && admin.getRole().getId() == 1) {
        	projectsAssigned = projRepository.findByProjectStatusTrueAndGlobalProjectFalse();
        }else {
        	projectsAssigned = projectPersonRepository.findActiveProjectsByPerson(loggedInUserId, true);	
        }
        List<Project> globalProjects = projRepository.getGlobalProjects();
        if(!globalProjects.isEmpty() && person.getRole().getId() < 3){
            projectsAssigned.addAll(globalProjects);
        }
        for (DistinctProject disProject : distinctProjs) {
            boolean found = false;
            for (Project project : projectsAssigned) {
                if (disProject.getProject() == project) {
                    distiProjsWithApprove = new DistinctProject();
                    distiProjsWithApprove.setProject(disProject.getProject());
                    distiProjsWithApprove.setTask(disProject.getTask());
                    distiProjsWithApprove.setIndex(disProject.getIndex());
                    distiProjsWithApprove.setApprover(true);
                    distiProjsWithApproveList.add(distiProjsWithApprove);
                    found = true;
                    break;
                }
            }
            if(!found){
                distiProjsWithApprove = new DistinctProject();
                distiProjsWithApprove.setProject(disProject.getProject());
                distiProjsWithApprove.setTask(disProject.getTask());
                distiProjsWithApprove.setIndex(disProject.getIndex());
                distiProjsWithApprove.setApprover(false);
                distiProjsWithApproveList.add(distiProjsWithApprove);
            }
        }
        worklog.setDistinctProjects(distiProjsWithApproveList);
        worklog.setStatus(statuses);
        if(admin != null && admin.getRole().getId() == 1) {
        	worklog.setProjectIds(workLogRepository.getProjectsIdsForAdmin(true));	
        }else {
        	worklog.setProjectIds(workLogRepository.getProjectsIdsForManager(loggedInUserId));
        }
        return worklog;

    }

    /**
     * @param date
     * @return
     */
    private String dateToString(Date date) {
        String pattern = ApplicationConstants.DATE_PATTERN;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    /* (non-Javadoc)
     * @see com.nalashaa.timesheet.service.IWorkLogService#saveTimeSheet(java.util.List)
     */
    @Override
    /**
     * 
     */
    public List<Worklog> saveTimeSheet(List<Worklog> timesheet, String personId) {
        logger.info("Entered : saveTimeSheet");
        List<Task> tasks = timesheet.stream().map(Worklog::getTask).distinct().collect(Collectors.toList());
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd");
        if (!tasks.isEmpty()) {
        	if (timesheet.get(0).getStatus().getId() == 1) {
        		Calendar cal = Calendar.getInstance();
        		Date date = null;
        		Date previous = null;
				try {
					date = sformat.parse(sformat.format(timesheet.get(0).getDate()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				cal.setTime(date);
				while(cal.get(Calendar.DAY_OF_WEEK) != 2) {
					date = DateUtils.addDays(date, -1);
					cal.setTime(date);
				}
				previous = DateUtils.addDays(date, -3);
				List<Date> holidays = holidayrepo.getHolidayDates();
				while(holidays.contains(previous))
					previous=DateUtils.addDays(previous, -1);
				List<Worklog> previousWl = workLogRepository.findEntryFroPreviousDay(timesheet.get(0).getPerson(),previous);
	    		if(previousWl.isEmpty() && !timesheet.get(0).getPerson().getDateOfJoining().after(previous)) {
	    			throw new TimeSheetException("Previous week`s hours are either not approved or not submitted. You can only save the timesheet for now.");
	    		}
    			List<Worklog> worklogs = workLogRepository.findByPersonAndDateBetweenOrderByIndexAsc(timesheet.get(0).getPerson(),DateUtils.addDays(date, -7),DateUtils.addDays(date, -3));
        		List<Worklog> nonapprovedtimesheet = worklogs.stream().filter(wl -> wl.getStatus().getId() != 2).collect(Collectors.toList());
            	if((worklogs.isEmpty() || !nonapprovedtimesheet.isEmpty()) && /*date.after(timesheet.get(0).getPerson().getDateOfJoining())*/ !timesheet.get(0).getPerson().getDateOfJoining().after(previous)) {
            		throw new TimeSheetException("Previous week`s hours are either not approved or not submitted. You can only save the timesheet for now.");
            	}
        	}
        
        	if(timesheet.get(0).getStatus().getId() == 3) {
        		Set<Person> rejectedPersons = new HashSet<>();
        		Map<Person,List<Date>> rejectedDatesmap = new HashMap<>();
        		for (Worklog worklog : timesheet) {
        			if(rejectedPersons.contains(worklog.getPerson())) {
        				if(rejectedDatesmap.get(worklog.getPerson()).contains(worklog.getDate())) {
        					continue;
        				}
        					rejectedDatesmap.get(worklog.getPerson()).add(worklog.getDate());
        			}
        			else {
        				List<Date> date = new ArrayList<Date>();
        				date.add(worklog.getDate());
        				rejectedDatesmap.put(worklog.getPerson(),date);
        				rejectedPersons.add(worklog.getPerson());
        			}
        			
        		}
        		if(!rejectedPersons.isEmpty()) {
        			for(Person person : rejectedPersons) {
        				List<Date>  rejectedDates = rejectedDatesmap.get(person);
        				String dates = "\n";
        				for(Date onedate: rejectedDates)
        					dates = dates+sformat.format(onedate)+"\n";
        				try {
        					emailService.sendMail(person.getEmailAddress(),false, null, "timesheet.manager.rejection","Timesheet Rejected", person.getEmpName(), person.getEmpId(),dates);
        				} catch (MessagingException e) {
        					e.printStackTrace();
        				}
        			}
        		}
        	}
        	/*for (Worklog worklog : timesheet) {
            	Date previous = null;
            	Calendar cal = Calendar.getInstance();
            	cal.setTime(worklog.getDate());
            	if(cal.get(Calendar.DAY_OF_WEEK) == 2) {
	            		previous = DateUtils.addDays(worklog.getDate(), -3);
	            	List<Worklog> wl = workLogRepository.findEntryFroPreviousDay(worklog.getPerson(),previous);
	            	long previousDayHoursSpent = 0;
	            	for (Worklog worklog2 : wl) {
	            		previousDayHoursSpent += worklog2.getHoursSpent();
					}
	            	if((wl.isEmpty() || previousDayHoursSpent < 8) && !worklog.getDate().equals(worklog.getPerson().getDateOfJoining())) {
	            		throw new TimeSheetException("Please fill previous day's timesheet.");
	            	}
            	}
                Double hoursSpentForTask = workLogRepository.sumOfHoursByTaskId(worklog.getTask().getId());
                if (worklog.getId() != 0 && worklog.getTask().getEstimatedHours()> 0.0) {
                    Double hoursSpent = workLogRepository.getHoursSpentById(worklog.getId());
                    if ( null !=  hoursSpentForTask && (!worklog.getProject().isGlobalProject() && worklog.getTask().getEstimatedHours() < hoursSpentForTask + worklog.getHoursSpent() - hoursSpent)) {
                        throw new TimeSheetException(worklog.getTask().getTaskName()+" "+messageSource.getMessage("timesheet.task.not.active", null, null));
                    }
                } else if (null != hoursSpentForTask && 0.0 != worklog.getTask().getEstimatedHours() && (!worklog.getProject().isGlobalProject() && worklog.getTask().getEstimatedHours() < hoursSpentForTask + worklog.getHoursSpent())) {
                    throw new TimeSheetException(worklog.getTask().getTaskName()+" "+messageSource.getMessage("timesheet.task.not.active", null, null));
                }*/
        	List<Long> zerohrwl = new ArrayList<Long>();
        	for (Worklog worklog : timesheet) {
	                worklog.setLastUpdatedBy(personId);
	                if(worklog.getId()==0){
	                	worklog.setCreatedBy(personId);
	                	worklog.setCreatedDate(new Timestamp(System.currentTimeMillis()));
	                	worklog.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
	                }else {
	                	 worklog.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
	                }
	                if(worklog.getId() != 0 && worklog.getHoursSpent() == 0) 
	                	zerohrwl.add(worklog.getId());
                }
        	timesheet.removeIf(worklog->(worklog.getHoursSpent() == 0));
        	if(!zerohrwl.isEmpty())
        		deleteEntries(zerohrwl);
            }
        List<Worklog> workLogs = workLogRepository.save(timesheet);
        return workLogs;
    }

    /* (non-Javadoc)
     * @see com.nalashaa.timesheet.service.IWorkLogService#approveTimeSheet(com.nalashaa.timesheet.model.TimeSheet)
     */
    /**
     * 
     */
    @Override
    public void approveTimeSheet(TimeSheet timesheet) {
        logger.info("Entered : approveTimeSheet");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String s = df.format(timesheet.getDate());
        String result = s;
        Date date = null;
        try {
            date = df.parse(result);
        } catch (ParseException e) {
            throw new TimeSheetException("Invalid Date format");
        }
        Person person = new Person();
        person.setId(timesheet.getPersonId());
        workLogRepository.approveTimeSheet(person, date, timesheet.getStatus(), timesheet.getUpdatedBy(), new Date());
    }

    @Override
    public void timeSheetStatus(TimeSheet timesheet) {
        logger.info("Entered : timeSheetStatus");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> formattedDateList = new ArrayList<>();
        try {
            List<Date> dateList = timesheet.getDates();
            if (dateList != null) {
                for (Date date : dateList) {
                    String s = df.format(date);
                    String result = s;
                    date = df.parse(result);
                    formattedDateList.add(date);
                }
            }

        } catch (ParseException e) {
            throw new TimeSheetException("Invalid Date format");
        }
        Person loggedInuser = personRepository.findOne(timesheet.getLoggedUserId());
        Person person = personRepository.findOne(timesheet.getPersonId());
        if (null != loggedInuser && loggedInuser.getRole().getId() == 1) {
            workLogRepository.updateTimeSheetStatus(person, formattedDateList, timesheet.getStatus(), timesheet.getUpdatedBy(), new Date());
            List<Worklog> worklogs = workLogRepository.findByPersonAndDates(person,formattedDateList);
        } else  {
            List<Project> projects = projectPersonRepository.findAllProjectsByPerson(loggedInuser.getId());
            if (loggedInuser.getRole().getId() == 2) {
                List<Project> globalProjects = projRepository.getGlobalProjects();
                if (!globalProjects.isEmpty()) {
                    projects.addAll(globalProjects);
                }
            }
            if (!projects.isEmpty()) {
                workLogRepository.updateTimeSheetStatusForProject(person, formattedDateList, timesheet.getStatus(), timesheet.getUpdatedBy(), new Date(), projects);
                List<Worklog> worklogs = workLogRepository.findByPersonAndProjectsAndDates(person, projects, formattedDateList);
            }
        }
    }
    /**
     * 
     */
    @Override
    public List<WorklogStatus> getWoklogStatus() {
        return workLogStatusRepository.findAll();
    }
    /**
     * 
     */
	@Override
	public List<WorkLog> getWorkLogforUsers(long loggedInUserId, List<PersonDTO> persons, Date startDate, Date endDate) {
		List<WorkLog> workLogList = new ArrayList<WorkLog>();
		WorkLog workLog;
		for (PersonDTO person : persons) {
			workLog = this.getTimeSheetByPerson(loggedInUserId,person.getId(),startDate,endDate);
			if(!workLog.getWork().isEmpty()) {
				workLogList.add(workLog);
			}
		}
		return workLogList;
	}
	/**
	 * 
	 */
	@Override
	public List<Worklog> getPMOReport(List<Long> userIds, Date startDate, Date endDate) {
		return workLogRepository.getReportForPMO(userIds, startDate, endDate);
	}
	/**
	 * 
	 */
	@Override
	public void deleteEntries(List<Long> entryIdList) {
		logger.info("Entered : deleteEntries");
        for(Long entryId : entryIdList) {
        	if(workLogRepository.getOne(entryId) != null) {
        		workLogRepository.delete(entryId);	
        	}
        }
	}

}
