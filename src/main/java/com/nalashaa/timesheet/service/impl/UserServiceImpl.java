
package com.nalashaa.timesheet.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nalashaa.timesheet.Exception.TimeSheetException;
import com.nalashaa.timesheet.entity.AssignResource;
import com.nalashaa.timesheet.entity.Designation;
import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Role;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.model.PersonDTO;
import com.nalashaa.timesheet.model.Resource;
import com.nalashaa.timesheet.repository.IAssignResourceDAO;
import com.nalashaa.timesheet.repository.IDesignationDAO;
import com.nalashaa.timesheet.repository.IPersonDAO;
import com.nalashaa.timesheet.repository.IProjectDAO;
import com.nalashaa.timesheet.repository.IProjectPersonDAO;
import com.nalashaa.timesheet.repository.IRoleDAO;
import com.nalashaa.timesheet.repository.ISkillDAO;
import com.nalashaa.timesheet.repository.ITaskDAO;
import com.nalashaa.timesheet.repository.ITaskPersonDAO;
import com.nalashaa.timesheet.repository.IWorkLogDAO;
import com.nalashaa.timesheet.service.IEmailService;
import com.nalashaa.timesheet.service.IUserService;
import com.nalashaa.timesheet.util.EntityToDTOAssembler;

/**
 * @author vijay ganesh
 *
 */
@Service
@Transactional
public class UserServiceImpl implements IUserService,EntityToDTOAssembler {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    
    private static final int NUMBER_OF_CHARACTERS = 10;

    @Autowired
    IPersonDAO personRepository;

    @Autowired
    IProjectDAO projectRepository;
    
    @Autowired
    IProjectPersonDAO projPerRepository;
    
    @Autowired
    IAssignResourceDAO assignResourceRepository;
    
    @Autowired
    IWorkLogDAO workLogRepository;
    
    @Autowired
    IEmailService emailService;
    
    @Autowired
    IDesignationDAO designationRepository;
    
    @Autowired
    ISkillDAO skillRepository;
    
    @Autowired
    IRoleDAO roleRepository;
    
    @Autowired
    private Environment env;
    
    @Autowired
    ITaskPersonDAO taskPersonRepository;
    
    @Autowired
    ITaskDAO taskRepository;
	
    
    /**
     * @param person
     */
    @Override
    public Person createUser(Person person) {
        logger.info("Entered : createUser");
        
        validUser(person, false);
        person.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        String password=randomAlphaNumericGenerator();
        person.setStatus(true);
        person.setPassword(encodePassword(password));
        person = personRepository.save(person);
        String subject = env.getRequiredProperty("passwordSubject");
        try {
			emailService.sendMail(person.getEmailAddress(),true, password, "passwordTemplate", subject, person.getEmpName(), person.getEmpId(),"");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return person;
    }
    
    /**
     * @param person
     */
    public void updateUser(Person person) {
    	 
    	 Person  personDetail = personRepository.findByIdAndStatusTrue(person.getId());
		 person.setPassword(personDetail.getPassword());
    	 validUser(person, true);
    	 person.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
    	 personRepository.save(person);
    }

    /**
     * @param person
     */
    private void validUser(Person person, boolean isUpdate){
    	
    	 Person  personInDb = null;
         try {
             if (StringUtils.isNotEmpty(person.getEmailAddress())) {
            	 if (isUpdate) {
            		 personInDb = personRepository.findByEmailIdForUpdate(person.getEmailAddress(), person.getId());
            	 } else {
            		 personInDb = personRepository.findByEmailAddressAndStatusTrue(person.getEmailAddress());
            	 }
                 if (personInDb != null) {
                     throw new TimeSheetException("User is already registed with this email address");
                 }
             }  
             if (StringUtils.isNotEmpty(person.getEmpId())) {
            	 if (isUpdate) {
            		 personInDb = personRepository.findByEmpIdForUpdate(person.getEmpId(), person.getId());
            	 } else {
            		 personInDb = personRepository.findByEmpIdAndStatusTrue(person.getEmpId());
            	 }
                 
                 if (personInDb != null) {
                     throw new TimeSheetException("User is already registed with this employee Id");
                 }
             }
         } catch (Exception ex) {
             logger.error("Error in UserServiceImpl/CreateUser");
             logger.error(ex.getMessage());
             throw ex;
         }
    }
    
    /**
     * @param empId , password
     */
	public PersonDTO login(final String empId, final String password) {
        logger.info("Entered - UserServiceImpl/login");
        Person  person = personRepository.findByEmpIdAndStatusTrue(empId);
        validateUser(password, person);
        Long roleId=projPerRepository.findMaxRoleIdOfPersonInAllProjects(person.getId());        
        PersonDTO personDTO = getPersonDTO(person);
        if(roleId!=null&&personDTO.getRole().getId()>roleId) {
        	Role role = roleRepository.getOne(roleId);
        	personDTO.getRole().setId(roleId);
        	personDTO.getRole().setRoleName(role.getRoleName());
		 }
        return personDTO;
    }
	
	/**
     * This method is used to change the password for provided empId.
     * @param empId
     * 			It represents the user empid, who wants to change password.
     * @param oldPassword
     * 			It is used to contain old password for that user.
     * @param newPassword
     * 			It is used to contain new password for that user.
     */
	@Override
	@Transactional
	public void changePassword(String empId, String oldPassword, String newPassword) {
		
		   Person  person = personRepository.findByEmpIdAndStatusTrue(empId);
		   validateUser(oldPassword, person);
		   person.setLastUpdatedBy(String.valueOf(person.getId()));
		   person.setLastUpdatedTime(new Date());
		   person.setPassword(encodePassword(newPassword));
		   person.setFirstLogin(false);
		   personRepository.save(person);
		   logger.info("Change password successfully. ");
	}

	/**
	 * This method is responsible to generate the new password,in case user forget his/her password.
	 * @param empId
	 *        	It represents the user id, who has forgotton his/her password. 
	 */
	@Override
	public void forgetPassword(String empId) {
		
		   logger.info("Entered Service - forget Password");
		   Person person=personRepository.findByEmpIdAndStatusTrue(empId);
		   if (person == null) {
	            throw new TimeSheetException("Invalid Username or Password.");
	        }
		   String tempPassword=randomAlphaNumericGenerator();
		   System.out.println("Temporary PASSWORD :"+tempPassword+" :for USRID : "+empId);
		   person.setLastUpdatedTime(new Date());
		   person.setPassword(encodePassword(tempPassword));
		   person.setFirstLogin(true);
           personRepository.save(person);
           String subject = env.getRequiredProperty("forgotPasswordSubject");
           try {
			emailService.sendMail(person.getEmailAddress(),true,tempPassword, "forgotPasswordTemplate",subject, person.getEmpName(), person.getEmpId(),"");
           } catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is used to generate random alphanumeric string.
	 * @return
	 * 		It returns alphanumeric string.
	 */
	private String randomAlphaNumericGenerator() {
		
		return RandomStringUtils.randomAlphanumeric(NUMBER_OF_CHARACTERS);
	}

	
	public List<Person> getListOfResources() {
		
		return personRepository.findByStatusTrue();
	}
	
    @Override
    public Resource getDesignationAndSkill() {
        logger.info("Entered - UserServiceImpl/getDesignationAndSkill");
        
        List<String> skillList =  skillRepository.getAllSkillName();
        List<Designation> designationList =  designationRepository.findAll();
        Resource resource = new Resource();
        resource.setDesignationList(designationList);
        resource.setSkill(skillList);
        return resource;
    }
    
    public Resource getProjectAndAssignedResources(Long personId) {
    	
    	List<Object[]> assignResourceList = assignResourceRepository.getAssignedResource();
    	List<AssignResource>  assignedResList = null;
    	AssignResource assignResource = null; 
    	Map<String, List<AssignResource>> assignResMap = new HashMap<String, List<AssignResource>> ();
    	
    	for (Object[] resource :assignResourceList) {
    		
    		Long projectId = (Long) resource[0];
    		assignedResList = assignResMap.get(projectId.toString());
    		
    		if (assignedResList == null ) {
    			
    			assignedResList = new ArrayList<AssignResource>();
    		} 
    		assignResource = new AssignResource();
    		assignResource.setProjectId(projectId);
    		assignResource.setId((Long) resource[1]);
    		assignResource.setPersonId((Long) resource[2]);
    		assignResource.setRoleId((Long) resource[3]);
    		assignResource.setEmpId((String) resource[4]);
			assignResource.setEmpName((String) resource[5]);
			assignResource.setRoleName((String) resource[6]);
			assignResource.setResourceAllocation((boolean) resource[7]);
			
    		assignedResList.add(assignResource);
    		assignResMap.put(projectId.toString(), assignedResList);
    	}
    	
    	List<Role> roleList =  roleRepository.findAll();
    	Person person = personRepository.getOne(personId);
    	List<Project> projectList = null;
    	if(person != null && person.getRole().getId() == 1)
    		projectList = projectRepository.findActiveProjectsForAdmin(new Date());
    	else
    		projectList =  projPerRepository.findActiveProjectsForManager(personId,true, new Date());
        List<Person> personList =  personRepository.getPersonDetails();        
    	Resource resource = new Resource();
    	resource.setRole(roleList);
    	resource.setProject(projectList);
    	resource.setPerson(personList);
    	resource.setAssignedResource(assignResMap);
    	return resource;
    }
	
    public Resource assignProjectAndResource(Long personId,List<AssignResource> assignResource) {
    	
    	assignResourceRepository.save(assignResource);
    	return getProjectAndAssignedResources(personId);
    }
    
    public Resource unAssignProjectAndResource(Long personId,List<AssignResource> assignResource) {
    	
    	if(assignResource != null) {
    		for(AssignResource assignresource:assignResource)	{
    			List<Task> tasks = taskPersonRepository.getTasksByProjectAndPerson(assignresource.getProjectId(),assignresource.getPersonId(),null);
    			if(!tasks.isEmpty()) {
    				for(Task task:tasks)
    					taskPersonRepository.deleteByTaskAndPerson(task,personRepository.getOne(assignresource.getPersonId()));
    			}
    		}
    	}
    	assignResourceRepository.delete(assignResource);
    	return getProjectAndAssignedResources(personId);
    }
    
    public List<AssignResource> getAssignResourceByPerson(Long personID) {
    	
    	return assignResourceRepository.getAssignedResourcebyPerson(personID);
    }
    
	/**
	 * This method is used to validate the user existing in system.
	 * @param password
	 * 			It is used for user authentication to prove identity or access approval to gain access to a resource.
	 * @param person
	 * 			This is used to contain the user information.
	 */
	private void validateUser(final String password, Person person) {
		if (person == null) {
            throw new TimeSheetException("Invalid Username or Password.");
        } 

        if (person.getPassword() != null && !BCrypt.checkpw(password, person.getPassword())) {
                logger.info("Authentication failed: password does not match stored value");
                throw new TimeSheetException("Incorrect Password");
        }
	}
	
	/**
     * @param password
     * @return
     */
    private String encodePassword(String password) {
    	
        logger.info("Entered - UserServiceImpl/encodePassword");
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        logger.info("Exiting - UserServiceImpl/encodePassword");
        return encodedPassword;
    }


    
	/*************************************************/
	

    /* (non-Javadoc)
     * @see com.nalashaa.timesheet.service.IUserService#getProjects(long)
     */
    @Override
    public List<Project> getProjects(long empId) {
        logger.info("Entered - UserServiceImpl/getProjects");
        return projectRepository.findAll();
    }

    /* (non-Javadoc)
     * @see com.nalashaa.timesheet.service.IUserService#getUsers()
     */
    @Override
    public List<Person> getUsers() {
        logger.info("Entered - UserServiceImpl/getUsers");
        List<Person> personList =  personRepository.findAll();
        
        return personList;
    }

    @Override
    public List<PersonDTO> getUsersByProjectAndRole(long userId) {
        logger.info("Entered - UserServiceImpl/getUsersByProjectAndRole");
        Person person = personRepository.getOne(userId);
        List<Person> usersList;
        List<PersonDTO> responseList = new ArrayList<PersonDTO>();
        PersonDTO personDTO;
        if(person != null && person.getRole().getId() == 1) {
        	usersList = personRepository.findByStatusTrueOrderByEmpNameAsc();
        }else {
        	usersList = personRepository.getUsersByProjectAndRole(userId);
        }
        for (Person user : usersList) {
        	Long roleId=projPerRepository.findMaxRoleIdOfPersonInAllProjects(user.getId());        
            personDTO = getPersonDTO(user);
            if(roleId!=null&&personDTO.getRole().getId()>roleId) {
            	Role role = roleRepository.getOne(roleId);
            	personDTO.getRole().setId(roleId);
            	personDTO.getRole().setRoleName(role.getRoleName());
    		 }
            responseList.add(personDTO);
		}
        if(person != null && person.getRole().getId() != 1) {
        	responseList = responseList.stream().filter(user -> user.getRole().getId() > 2).collect(Collectors.toList());	
        }
        return responseList;
    }

    @Override
    public List<Person> getUsersByLoggedInUserRoleFilter(long userId, String searchText, long worklogStatusId) {
        logger.info("Entered - UserServiceImpl/getUsersByLoggedInUserRoleFilter");
        List<Person> allUsers = null;
        Person person = personRepository.findOne(userId);
        List<Project> projects = projPerRepository.findActiveProjectsByPerson(person.getId(), true);
        if (worklogStatusId == 0) {
            allUsers = workLogRepository.getUsersByProjectWithoutStatus(projects, searchText,userId); 
        } else {
        if (worklogStatusId == 4) {
            allUsers = projPerRepository.findPersonByProjects(projects,userId);
            List<Person> temp = new ArrayList<>();
            List<Person> usersFiledTimesheet = workLogRepository.findUsersByProjectsAndSubmitted(projects,searchText,userId);
                for (Person user : allUsers) {
                    boolean userFound = false;
                    for (Person personfilled : usersFiledTimesheet) {
                        if (user.getId() == personfilled.getId()) {
                            userFound = true;
                        }
                    }
                    if (!userFound) {
                        temp.add(user);
                    }
                }
                allUsers = temp;
            } else {
            allUsers = workLogRepository.getUsersByProject(projects, searchText, worklogStatusId,userId);
        }
        }        
        return allUsers;
    }

    
    @Override
    public List<Person> getUsersByLoggedInUserRoleFilter(long userId, long worklogStatusId) {
        logger.info("Entered - UserServiceImpl/getUsersByLoggedInUserRoleFilter");
        List<Person> allUsers = null;
        Person person = personRepository.findOne(userId);
        List<Project> projects = projPerRepository.findActiveProjectsByPerson(person.getId(), true);
        if (worklogStatusId == 0) {
            allUsers = workLogRepository.getUsersByProjectWithoutStatusWithoutSearchText(projects,userId); 
        } else {
        if (worklogStatusId == 4) {
            allUsers = projPerRepository.findPersonByProjects(projects,userId);
            List<Person> temp = new ArrayList<>();
            List<Person> usersFiledTimesheet = workLogRepository.findUsersByProjectsAndSubmittedWithoutSearchText(projects,userId);
                for (Person user : allUsers) {
                    boolean userFound = false;
                    for (Person personfilled : usersFiledTimesheet) {
                        if (user.getId() == personfilled.getId()) {
                            userFound = true;
                        }
                    }
                    if (!userFound) {
                        temp.add(user);
                    }
                }
                allUsers = temp;
            } else {
            allUsers = workLogRepository.getUsersByProjectWithoutSearchText(projects, worklogStatusId,userId);
        }
        }        
        return allUsers;
    }
    
   

    @Override
    public List<Person> getUsersByProject(long projectId) {
        Project project = new Project();
        project.setId(projectId);
        return projPerRepository.findByProject(project);
    }

	@Override
	public List<PersonDTO> getUsersForReportSummary(long personId, long projectId, Date startDate, Date endDate) {
		logger.info("Entered - UserServiceImpl/getUsersByProjectAndRole");
        Person person = personRepository.getOne(personId);
        Project project = projectRepository.findById(projectId);
        List<Person> usersList;
        List<PersonDTO> responseList = new ArrayList<PersonDTO>();
        PersonDTO personDTO;
        if(projectId == -1) {
        	usersList = personRepository.findAll();
        }else if(project != null && project.isGlobalProject()) {
        	usersList = projPerRepository.findUsersOnBench();
        }else {
        	//usersList = projPerRepository.findUsersUnderProject(projectId);
        	usersList = workLogRepository.getUsersWithTimesheetForProject(projectId, startDate, endDate);
        }
        for (Person user : usersList) {
        	Long roleId=projPerRepository.findMaxRoleIdOfPersonInAllProjects(user.getId());        
            personDTO = getPersonDTO(user);
            if(roleId!=null&&personDTO.getRole().getId()>roleId) {
            	Role role = roleRepository.getOne(roleId);
            	personDTO.getRole().setId(roleId);
            	personDTO.getRole().setRoleName(role.getRoleName());
    		 }
            responseList.add(personDTO);
		}
        return responseList;
	}

}
