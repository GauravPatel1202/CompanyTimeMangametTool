package com.nalashaa.timesheet.job;

import java.io.File;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.ProjectPerson;
import com.nalashaa.timesheet.entity.Role;
import com.nalashaa.timesheet.entity.Worklog;
import com.nalashaa.timesheet.model.Email;
import com.nalashaa.timesheet.repository.IHolidayDAO;
import com.nalashaa.timesheet.repository.IPersonDAO;
import com.nalashaa.timesheet.repository.IProjectDAO;
import com.nalashaa.timesheet.repository.IProjectPersonDAO;
import com.nalashaa.timesheet.repository.IRoleDAO;
import com.nalashaa.timesheet.repository.IWorkLogDAO;
import com.nalashaa.timesheet.util.ApplicationConstants;

/**
 * 
 * @author somadutta
 *
 */
@Component
@PropertySource("classpath:application.properties")
public class ScheduleJob {
 
 @Autowired
 IPersonDAO personRepository;
 
 @Autowired
 IWorkLogDAO worklogrepo;
 
 @Autowired
 Environment env;
 
 @Autowired
 IRoleDAO rolerepo;
 
 @Autowired
 IProjectPersonDAO projpersonrepo;
 
 @Autowired
 IHolidayDAO holiday;
 
 	@Autowired
	private JavaMailSender mailSender;
 	
 	@Autowired
 	private IProjectDAO projectRepo;
 	
 	private static Logger logger = LogManager.getLogger(ScheduleJob.class);
 	/**
 	 * @throws MessagingException 
 	 * 
 	 */
    @Scheduled(cron = "${cron.schedule.job.schedulereminder}")
    public void schedulereminder() throws MessagingException {
    	List<Person> persons = personRepository.findByStatusTrue();
    	for(Person person: persons) {
    		Map<String, String> replacements = new HashMap<String, String>();
    		replacements.put("user", person.getEmpName());
    		String message = getTemplate(env.getProperty("timesheet.reminder"),replacements);
    		Email email = new Email(env.getProperty("from"), person.getEmailAddress(), "Timesheet Reminder", message);
    		sendPlainTextMail(email);   	
   	}
    }
   
    
    private void sendPlainTextMail(Email eParams) throws MessagingException {
    	MimeMessage mime = mailSender.createMimeMessage();
    	MimeMessageHelper helper = new MimeMessageHelper(mime, true);
//		SimpleMailMessage mailMessage = new SimpleMailMessage();
		eParams.getTo().toArray(new String[eParams.getTo().size()]);
		helper.setTo(eParams.getTo().toArray(new String[eParams.getTo().size()]));
		helper.setReplyTo(eParams.getFrom());
		helper.setFrom(eParams.getFrom());
		helper.setSubject(eParams.getSubject());
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("email-templates/"+env.getProperty("logopath")).getFile());
		String htmlText = "<pre style='font-family:serif;font-size:12pt;color:rgb(31,73,125);'>"+eParams.getMessage()+
				"</pre><br/><img src=\"cid:logo\"><pre style='color:rgb(127,127,127);font-size:11pt;font-family:calibri;'>This message was sent by Nalashaa Healthcare Solutions – Orion\r\n" + 
				"<span style='font-size:10pt;'>Login to Orion-Time tracker: http://nalashaa.orion.com:8080/#/login" + 
				"<br/><i>If you happen to encounter any issue with regards to accessing the Orion application or  any bugs kindly email it to orion_helpdesk@nalashaa.com</i></pre> ";
		helper.setText(htmlText,true);
		helper.addInline("logo", file);
		if(eParams.getCc() != null)
			if (eParams.getCc().size() > 0) {
				helper.setCc(eParams.getCc().toArray(new String[eParams.getCc().size()]));
			}
		mailSender.send(mime);
		
    }
    
    public String getTemplate(String templateId,Map<String, String> replacements) {
    	ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("email-templates/" + templateId).getFile());
		String content = ApplicationConstants.BLANK;
		try {
			content = new String(Files.readAllBytes(file.toPath()));
		} catch (Exception e) {
			logger.error("UNABLE TO READ TEMPLATE WITH ID : "+templateId +" DUE TO "+e.getMessage());
		}
		
		if (StringUtils.isNotEmpty(content)) {
			for (Map.Entry<String, String> entry : replacements.entrySet()) {
				content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
			}
		}
		
		return content;
    }
    /**
     * @throws MessagingException 
     * 
     */
    @Scheduled(cron = "${cron.schedule.job.firstscheduleDefaulterreminder}")
    public void firstscheduleDefaulterreminder() throws MessagingException {
    	
    	List<Person> persons = personRepository.findByStatusTrue();
    	Date previous = null;
		Calendar cal = Calendar.getInstance();
		
		DateFormat sdate = new SimpleDateFormat("dd-MM-yyyy");
		Date date = null;
		Date dateOfJoining = null;
		try {
			date = sdate.parse(sdate.format(new Date()));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 
		cal.setTime(date);
		String endDate= sdate.format(DateUtils.addDays(date, -3));
		String startDate = sdate.format(DateUtils.addDays(date, -7));
		previous = DateUtils.addDays(date, -3);
    		for(Person person : persons) {
    			try {
					dateOfJoining=sdate.parse(sdate.format(person.getDateOfJoining()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			List<Date> holidays = holiday.getHolidayDates();
    			while(holidays.contains(previous))
    				previous=DateUtils.addDays(previous, -1);
    				
    			
    				List<Worklog> wl = worklogrepo.findEntryFroPreviousDay(person,previous);
    				List<Worklog> rejectwls = worklogrepo.getRejectedWorklogsForPerson(person, DateUtils.addDays(date, -7), DateUtils.addDays(date, -3));
    				if((wl.isEmpty() || !rejectwls.isEmpty()) && !dateOfJoining.after(previous)){
    					Map<String, String> replacements = new HashMap<String, String>();
    		    		replacements.put("user", person.getEmpName());
    		    		replacements.put("startDate", startDate);
    		    		replacements.put("endDate", endDate);
    		    		String message = getTemplate(env.getProperty("timesheet.defaulter.reminder"),replacements);
    					Email email = new Email(env.getProperty("from"), person.getEmailAddress(), "Timesheet Not Filled", message);
    					sendPlainTextMail(email);
    				}
    				/*else{
    				for (Worklog worklog2 : wl) {
    					if(!dateOfJoining.after(previous)
    							&& !"Pending Approval".equals(worklog2.getStatus().getStatus())
    							&& !"Approved".equals(worklog2.getStatus().getStatus())) { 
    						defaulter = true;
    						break;
    						}
    					}
    				if(defaulter) {
    					Map<String, String> replacements = new HashMap<String, String>();
    		    		replacements.put("user", person.getEmpName());
    		    		replacements.put("startDate", startDate);
    		    		replacements.put("endDate", endDate);
    		    		String message = getTemplate(env.getProperty("timesheet.defaulter.reminder"),replacements);
    					Email email = new Email(env.getProperty("from"), person.getEmailAddress(), "Timesheet Not Filled", message);
    					sendPlainTextMail(email);
    					}
    				}*/
    		}
    }
    
    /**
     * @throws MessagingException 
     * 
     */
//    @Scheduled(cron = "${cron.schedule.job.secondscheduleDefaulterreminder}")
    public void secondscheduleDefaulterreminder() throws MessagingException {
    	
    	List<Person> persons = personRepository.findByStatusTrue();
    	Date previous = null;
		Calendar cal = Calendar.getInstance();
		DateFormat sdate = new SimpleDateFormat("dd-MM-yyyy");
		Date date = null;
		Date dateOfJoining = null;
		try {
			date = sdate.parse(sdate.format(new Date()));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.setTime(date);
			previous = DateUtils.addDays(date, -4);
			String endDate= sdate.format(DateUtils.addDays(date, -4));
			String startDate = sdate.format(DateUtils.addDays(date, -8));
		for(Person person : persons) {
			
				List<Worklog> wl = worklogrepo.findEntryFroPreviousDay(person,previous);
				boolean defaulter = false;
				try {
					dateOfJoining=sdate.parse(sdate.format(person.getDateOfJoining()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(wl.isEmpty() && !dateOfJoining.after(previous)){
					Map<String, String> replacements = new HashMap<String, String>();
		    		replacements.put("user", person.getEmpName());
		    		replacements.put("startDate", startDate);
		    		replacements.put("endDate", endDate);
		    		String message = getTemplate(env.getProperty("timesheet.defaulter.reminder"),replacements);
					Email email = new Email(env.getProperty("from"), person.getEmailAddress(), "Timesheet Not Filled", message);
					sendPlainTextMail(email);
				}
				else{
				for (Worklog worklog2 : wl) {
					if(!dateOfJoining.after(previous)
							&& !"Pending Approval".equals(worklog2.getStatus().getStatus())
							&& !"Approved".equals(worklog2.getStatus().getStatus())) { 
						defaulter = true;
						break;
						}
					}
				if(defaulter) {
					Map<String, String> replacements = new HashMap<String, String>();
		    		replacements.put("user", person.getEmpName());
		    		replacements.put("startDate", startDate);
		    		replacements.put("endDate", endDate);
		    		String message = getTemplate(env.getProperty("timesheet.defaulter.reminder"),replacements);
					Email email = new Email(env.getProperty("from"), person.getEmailAddress(), "Timesheet Not Filled", message);
					sendPlainTextMail(email);
					}
				}
		}
    } 
    /**
     * @throws MessagingException 
     * 
     */
    @Scheduled(cron = "${cron.schedule.job.managerEscalation}")
    public void managerEscalation() throws MessagingException {
    	List<Project> projects = projectRepo.findByProjectStatusTrue();
    	List<Person> globalpersons = personRepository.findGlobalProjectPersons();
    	Date previous = null;
    	DateFormat sdate = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();
		Date date = null;
		Date dateOfJoining = null;
		try {
			date = sdate.parse(sdate.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.setTime(date);
			previous = DateUtils.addDays(date, -4);
			List<Date> holidays = holiday.getHolidayDates();
			while(holidays.contains(previous))
				previous=DateUtils.addDays(previous, -1);
			String endDate= sdate.format(DateUtils.addDays(date, -4));
			String startDate = sdate.format(DateUtils.addDays(date, -8));
		List<String> globaldefaulters = new ArrayList<String>();
    	for(Person globalPerson:globalpersons)
    	{
    		try {
				dateOfJoining=sdate.parse(sdate.format(globalPerson.getDateOfJoining()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		List<Worklog> wl = worklogrepo.findEntryFroPreviousDay(globalPerson,previous);
    		if(wl.isEmpty() && !dateOfJoining.after(previous)) {
    			globaldefaulters.add(globalPerson.getEmpName());
    		}
    		/*else {
    			for (Worklog worklog2 : wl) {
					if (!dateOfJoining.after(previous)
						&& !"Pending Approval".equals(worklog2.getStatus().getStatus())
						&& !"Approved".equals(worklog2.getStatus().getStatus())) {
						globaldefaulters.add(globalPerson.getEmpName());
					}
					}
    		}*/
    	}
		List<Person> managerDefaulters = new ArrayList<>();
    	for(Project project : projects) {
    		if(project.isGlobalProject() == true)
    			continue;
    		List<Person> defaulters = new ArrayList<>();
    		List<Person> projectpersons = projpersonrepo.findByProject(project);
    		String manager = ApplicationConstants.BLANK;
    		String managerName = ApplicationConstants.BLANK;;
    		for(Person person : projectpersons) {
    			ProjectPerson projectPerson = projpersonrepo.findByProjectAndPerson(project, person);
    			if(projectPerson.getRoleId() == 2) {
    				manager = person.getEmailAddress();
    				managerName = person.getEmpName();
    			}
    			try {
    				dateOfJoining=sdate.parse(sdate.format(person.getDateOfJoining()));
    			} catch (ParseException e) {
    				e.printStackTrace();
    			}
				List<Worklog> wl = worklogrepo.findByPersonAndProjectAndDate(person,project,previous);
				
				if(wl.isEmpty() && !dateOfJoining.after(previous)){
					List<Worklog> otherwl = worklogrepo.findEntryFroPreviousDay(person, previous);
					List<Worklog> rejectedwl = worklogrepo.getRejectedWorklogs(person, project, DateUtils.addDays(date, -8), DateUtils.addDays(date, -4));
					
	            	if((otherwl.isEmpty() || !rejectedwl.isEmpty()) && !defaulters.contains(person)) {
							defaulters.add(person);
	            	}
				}
				/*else {
					for (Worklog worklog2 : wl) {
					if (!dateOfJoining.after(previous)
						&& !"Pending Approval".equals(worklog2.getStatus().getStatus())
						&& !"Approved".equals(worklog2.getStatus().getStatus())) {
							defaulters.add(person);
							break;
						}
					}
				}*/
			}
    		if(!defaulters.isEmpty()) {
    			String defaulterList =  ApplicationConstants.SPACE;
        		for(Person defaulter:defaulters) {
        			ProjectPerson projectPerson = projpersonrepo.findByProjectAndPerson(project, defaulter);
        			if(projectPerson.getRoleId() == 2 && !managerDefaulters.contains(defaulter))
        				managerDefaulters.add(defaulter);
        			defaulterList += defaulter.getEmpName() + ApplicationConstants.SPACE;
        		}
			if(StringUtils.isNotBlank(manager)) {
				Map<String, String> replacements = new HashMap<String, String>();
	    		replacements.put("user", managerName);
	    		replacements.put("startDate", startDate);
	    		replacements.put("endDate", endDate);
	    		replacements.put("resources", defaulterList);
	    		String message = getTemplate(env.getProperty("timesheet.manager.escalation"),replacements);
				Email email = new Email(env.getProperty("from"), manager, "Timesheet Not Filled", message);
				sendPlainTextMail(email);
    		}
    	}
    }
    	Role role = rolerepo.findById(1);
    	List<Person> admins = personRepository.findByRole(role);
    	List<String> adminNames = new ArrayList<String>();
    	for(Person person : admins) {
			adminNames.add(person.getEmailAddress());
		}
    	if(!managerDefaulters.isEmpty() || !globaldefaulters.isEmpty()) {
    		String managerDef = ApplicationConstants.BLANK;
    		String defaulterList = ApplicationConstants.BLANK;
    		if(!managerDefaulters.isEmpty()) {
    			managerDef = ApplicationConstants.SPACE + "Managers : \n\n";
    			for(Person manager:managerDefaulters) {
    				managerDef += manager.getEmpName() +  ApplicationConstants.SPACE;
    			}
    		}
    		if(!globaldefaulters.isEmpty()) {
    			defaulterList =  ApplicationConstants.SPACE + "Global Resources : \n\n";
    			for(String defaulter:globaldefaulters) {
    				defaulterList += defaulter +  ApplicationConstants.SPACE ;
    			}
    		}
    		Map<String, String> replacements = new HashMap<String, String>();
    		replacements.put("user", "Admin");
    		replacements.put("startDate", startDate);
    		replacements.put("endDate", endDate);
    		replacements.put("resources", defaulterList+managerDef);
    		String message = getTemplate(env.getProperty("timesheet.manager.escalation"),replacements);
			Email email = new Email(env.getProperty("from"), adminNames, "Timesheet Not Filled", message);
			sendPlainTextMail(email);
    	}
    }
    
    /**
     * @throws MessagingException 
     * 
     */
    @Scheduled(cron = "${cron.schedule.job.adminReport}")
    public void adminReport() throws MessagingException {
    	List<String> empNames = new ArrayList<String>();
    	List<Person> persons = personRepository.findByStatusTrue();
    	Date previous = null;
		Calendar cal = Calendar.getInstance();
		
		DateFormat sdate = new SimpleDateFormat("dd-MM-yyyy");
		Date date = null;
		Date dateOfJoining = null;
		try {
			date = sdate.parse(sdate.format(new Date()));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 
		cal.setTime(date);
		String endDate= sdate.format(DateUtils.addDays(date, 0));
		String startDate = sdate.format(DateUtils.addDays(date, -4));
		previous = DateUtils.addDays(date, 0);
    		for(Person person : persons) {
    			try {
					dateOfJoining=sdate.parse(sdate.format(person.getDateOfJoining()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			List<Date> holidays = holiday.getHolidayDates();
    			while(holidays.contains(previous))
    				previous=DateUtils.addDays(previous, -1);
    				
    			
    				List<Worklog> wl = worklogrepo.findEntryFroPreviousDay(person,previous);
    				List<Worklog> rejectwls = worklogrepo.getRejectedWorklogsForPerson(person, DateUtils.addDays(date, -7), DateUtils.addDays(date, -3));
    				if((wl.isEmpty() || !rejectwls.isEmpty()) && !dateOfJoining.after(previous)){
    					empNames.add(person.getEmpName());
    				}
    		}
    		String defaulterList = ApplicationConstants.BLANK;
    		if(!empNames.isEmpty()) {
    			defaulterList =  ApplicationConstants.SPACE + "Resources : \n\n";
    			for(String defaulter:empNames) {
    				defaulterList += defaulter +  ApplicationConstants.SPACE ;
    			}
    		}
    		Map<String, String> replacements = new HashMap<String, String>();
    		replacements.put("user", "Admin");
    		replacements.put("startDate", startDate);
    		replacements.put("endDate", endDate);
    		replacements.put("resources",defaulterList);
    		String message = getTemplate(env.getProperty("timesheet.manager.escalation"),replacements);
    		Email email = new Email(env.getProperty("from"), env.getProperty("adminemail"), "Timesheet Not Filled", message);
			if("PROD".equals(env.getProperty("environment"))) {
				sendPlainTextMail(email);
			}
    }
   
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
    }
}

