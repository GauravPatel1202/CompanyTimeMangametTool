/**
 * 
 */
package com.nalashaa.timesheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Country;

/**
 * Interface for CRUD operations on a repository for a Country type.
 * 
 * @author ashwanikannojia
 */
@Repository
public interface ICountryDAO extends JpaRepository<Country, Long> {

}
