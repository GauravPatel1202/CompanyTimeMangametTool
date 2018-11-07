
package com.nalashaa.timesheet.service;

import java.util.List;

import com.nalashaa.timesheet.entity.Role;

public interface IRoleService {

    List<Role> getRolesByParentRole(long roleId);

    List<Role> getAllRoles();

}
