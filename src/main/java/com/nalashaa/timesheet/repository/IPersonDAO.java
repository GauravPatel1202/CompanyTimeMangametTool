
package com.nalashaa.timesheet.repository;

import java.util.List;

import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Role;

/**
 *Interface for CRUD operations on a repository for a Person type.
 * 
 * @author ashwanikannojia
 */
@Repository
public interface IPersonDAO extends JpaRepository<Person, Long> {

	/**
	 * A constant holding the query to get active employee by emp id. QUERY: {@value #FIND_ACTIVE_PERSON_BY_EMP_ID}.
	 */
	String FIND_ACTIVE_PERSON_BY_EMP_ID = "SELECT person FROM Person person WHERE person.empId = ?1 AND person.status = true";
	
	/**
	 * A constant holding the query to get active person by id. QUERY: {@value #FIND_ACTIVE_PERSON_BY_ID}.
	 */
	String FIND_ACTIVE_PERSON_BY_ID = "SELECT person FROM Person person WHERE person.id = ?1 AND person.status = true";
	
	/**
	 * A constant holding the query to update person by emp id and id. QUERY: {@value #FIND_PERSON_BY_EMP_ID_AND_ID_FOR_UPDATE}.
	 */
	String FIND_PERSON_BY_EMP_ID_AND_ID_FOR_UPDATE = "SELECT person FROM Person person WHERE person.empId = ?1 AND person.id != ?2 AND person.status = true";
	
	/**
	 * A constant holding the query to update person by email id and id. QUERY: {@value #FIND_PERSON_BY_EMAIL_ID_AND_ID_FOR_UPDATE}.
	 */
	String FIND_PERSON_BY_EMAIL_ID_AND_ID_FOR_UPDATE = "SELECT person FROM Person person WHERE person.emailAddress = ?1 AND person.id != ?2 and person.status = true";
	
	/**
	 * A constant holding the query to find all persons by project and role. QUERY: {@value #FIND_PERSON_BY_PROJECT_AND_ROLE}.
	 */
	String FIND_PERSON_BY_PROJECT_AND_ROLE = "SELECT person FROM Person person  WHERE person.id IN (select pp.person.id FROM ProjectPerson pp WHERE pp.project.id IN (SELECT ppp.project.id FROM ProjectPerson ppp WHERE ppp.person.id=?1 AND (ppp.roleId=1 OR ppp.roleId=2 OR ppp.roleId=3))) AND  (person.id <> ?1 OR person.role.id=1) ORDER BY person.empName ASC";
	
	/**
	 * A constant holding the query to find all unassigned active persons. QUERY: {@value #FIND_ALL_ACTIVE_AND_UNASSIGNED_PERSONS}.
	 */
	String FIND_ALL_ACTIVE_AND_UNASSIGNED_PERSONS = "SELECT person FROM Person person WHERE person.status = true AND person.id not IN ( "
    													+ "SELECT DISTINCT pp.person FROM ProjectPerson pp WHERE pp.resourceAllocation = true)";
    /**
     * A constant holding the query to find all persons allocated to global projects. QUERY: {@value #FIND_GLOBAL_PROJECT_PERSONS}.
     */
	String FIND_GLOBAL_PROJECT_PERSONS = "Select p from Person p where p.status = true and p.id NOT IN(Select pp.person.id from ProjectPerson pp)";
	
	String PURGE_INACTIVE_EMPLOYEES = "DELETE FROM orion.person WHERE person.status = 0 \r\n" + 
    		"AND person.last_Updated_date < DATE_SUB(CURDATE(), INTERVAL 13 MONTH)";
	
	/**
     * Retrieves an entity by its email id.
     * 
     * @param email must not be {@literal null}.
     * @return the entity with the given email or {@literal null} if none found
     */
	Person findByEmailAddressAndStatusTrue(String email);
    
	/**
	 * Retrieves an entity by its emp id.
	 * 
	 * @param empId must not be {@literal null}.
	 * @return the entity with the given empId or {@literal null} if none found
	 */
//    Person findByEmpIdAndStatusTrue(String empId);
    
    /**
     * Retrieves all active entities of person.
     * 
     * @return the list of all active employees or {@literal null} if none found
     */
    List<Person> findByStatusTrue();
    
    /**
     * Retrieves an entity by its emp name.
     * 
     * @param empName must not be {@literal null}.
     * @return an entity with the given empName or {@literal null} if none found
     */
    Person findByEmpName(String empName);
    
    /**
     * Retrieves an active entity by its empId.
     * 
     * @param empId must not be null {@literal null}.
     * @return an entity with the given empId or {@literal null} if none found 
     */
//    @Query(FIND_ACTIVE_PERSON_BY_EMP_ID)
    Person findByEmpIdAndStatusTrue(String empId);
    
    /**
     * This method is used to get active person entity by its id.
     * 
     * @param id must not be null {@literal null}.
     * @return an entity of person with the given id or {@literal null} if none found
     */
    @Query(FIND_ACTIVE_PERSON_BY_ID)
    Person findByIdAndStatusTrue(long id);
    
    /**
     * This method is used to update person entity by its empId and id.
     * 
     * @param empId must not be null {@literal} 
     * @param id must not be null {@literal}
     * @return an updated entity of person with the given empId and id
     */
    @Query(FIND_PERSON_BY_EMP_ID_AND_ID_FOR_UPDATE)
    Person findByEmpIdForUpdate(String empId, long id);
    
    /**
     * This method is used to update person entity by its emailId and id.
     * 
     * @param email must not be null {@literal}
     * @param id must not be null {@literal}
     * @return an updated entity of person with the given emailId and id
     */
    @Query(FIND_PERSON_BY_EMAIL_ID_AND_ID_FOR_UPDATE)
    Person findByEmailIdForUpdate(String email, long id);

    /**
     * This method is used to get list of persons entities by its id.
     * 
     * @param userId must not be null {@literal}
     * @return a list of persons entities which are associated with given userId
     */
    @Query(FIND_PERSON_BY_PROJECT_AND_ROLE)
    List<Person> getUsersByProjectAndRole(long userId);
    
    /**
     * This method is used to get all active and unassigned persons.
     * 
     * @return a list of all active and unassigned persons list 
     */
    @Query(FIND_ALL_ACTIVE_AND_UNASSIGNED_PERSONS)
    List<Person> getPersonDetails();

    List<Person> findByStatusTrueOrderByEmpNameAsc();
    
    @Query(FIND_GLOBAL_PROJECT_PERSONS)
    List<Person> findGlobalProjectPersons();
    
    List<Person> findByRole(Role role);
    
    @Modifying
    @Query(value=PURGE_INACTIVE_EMPLOYEES, nativeQuery = true)
    void purgeInactiveEmployees();
}
