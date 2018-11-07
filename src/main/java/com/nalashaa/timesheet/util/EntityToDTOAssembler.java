/**
 * 
 */
package com.nalashaa.timesheet.util;

import org.springframework.beans.BeanUtils;

import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.model.PersonDTO;
import com.nalashaa.timesheet.model.RoleDTO;

/**
 * @author ashwanikannojia
 *
 */
public interface EntityToDTOAssembler {

	default PersonDTO getPersonDTO(Person person) {
		PersonDTO personDTO=new PersonDTO();
		RoleDTO roleDTO=new RoleDTO();
		if(person.getRole()!=null) {
			BeanUtils.copyProperties(person.getRole(), roleDTO);
		}
		BeanUtils.copyProperties(person, personDTO);
		personDTO.setRole(roleDTO);
		return personDTO;
	}
	
}
