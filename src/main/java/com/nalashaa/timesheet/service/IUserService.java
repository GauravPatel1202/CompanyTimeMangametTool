
package com.nalashaa.timesheet.service;

import java.util.Date;
import java.util.List;

import com.nalashaa.timesheet.entity.AssignResource;
import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.model.PersonDTO;
import com.nalashaa.timesheet.model.Resource;

public interface IUserService {

	public Person createUser(Person person);
    
    public void updateUser(Person person);
    
    public List<Person> getListOfResources();
    
    public Resource getDesignationAndSkill();
    
    public Resource getProjectAndAssignedResources(Long personId);
    
    public Resource assignProjectAndResource(Long personId,List<AssignResource> assignResource);
    
    public Resource unAssignProjectAndResource(Long personId,List<AssignResource> assignResource);
    
    public List<AssignResource> getAssignResourceByPerson(Long personID);
    
    

    List<Project> getProjects(long empId);

    PersonDTO login(String empId, String password);

    List<Person> getUsers();

    List<PersonDTO> getUsersByProjectAndRole(long userId);

    List<Person> getUsersByLoggedInUserRoleFilter(long userId,String searchText, long worklogStatusId);

    List<Person> getUsersByProject(long projectId);

    List<Person> getUsersByLoggedInUserRoleFilter(long userId, long worklogStatusId);

    /**
     * This method is used to change the password for provided empId.
     * @param empId
     * 			It represents the user empid, who wants to change password.
     * @param oldPassword
     * 			It is used to contain old password for that user.
     * @param newPassword
     * 			It is used to contain new password for that user.
     */
	void changePassword(String empId, String oldPassword, String newPassword);

	/**
	 * This method is responsible to generate the new password,in case user forget his/her password.
	 * @param empId
	 *        	It represents the user id, who has forgotton his/her password. 
	 */
	void forgetPassword(String empId);
	
	List<PersonDTO> getUsersForReportSummary(long personId, long projectId, Date startDate, Date endDate);

}
