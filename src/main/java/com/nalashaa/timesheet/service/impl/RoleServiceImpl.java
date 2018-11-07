
package com.nalashaa.timesheet.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nalashaa.timesheet.entity.Role;
import com.nalashaa.timesheet.repository.IRoleDAO;
import com.nalashaa.timesheet.service.IRoleService;

/**
 * @author siva
 *
 */
@Service
@Transactional
public class RoleServiceImpl implements IRoleService {

    private static final Logger logger = LogManager.getLogger(RoleServiceImpl.class);

    @Autowired
    IRoleDAO roleDAO;

    /* (non-Javadoc)
     * @see com.nalashaa.timesheet.service.IRoleService#getRoles(int)
     */
    @Override
    public List<Role> getRolesByParentRole(long roleId) {
        logger.info("Entered : getRolesByParentRole");
        return roleDAO.findAll();
    }

    /* (non-Javadoc)
     * @see com.nalashaa.timesheet.service.IRoleService#getAllRoles()
     */
    @Override
    public List<Role> getAllRoles() {
        logger.info("Entered : getAllRoles");
        return roleDAO.findAll();
    }

}
