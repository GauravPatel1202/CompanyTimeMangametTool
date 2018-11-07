/**
 * 
 */
package com.nalashaa.timesheet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Skill;

/**
 * Interface for CRUD operations on a repository for a Skill type.
 * 
 * @author vijayganesh
 */
@Repository
public interface ISkillDAO extends JpaRepository<Skill, Long> {

	/**
	 * A constant holding the query to get list of skills. QUERY: {@value #GET_ALL_SKILLS}.
	 */
	String GET_ALL_SKILLS = "SELECT s.skillName FROM Skill s";
	
	/**
	 * This method is used to get list of skills.
	 * 
	 * @return list of skills or {@literal null} if none found
	 */
	@Query(GET_ALL_SKILLS)
	List<String> getAllSkillName();
}
