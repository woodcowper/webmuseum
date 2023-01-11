package com.webmuseum.museum.service;

import java.util.List;

import com.webmuseum.museum.entity.Role;

public interface IRoleService {

    Role getSuperAdminRole();

    Role getAdminRole();

    Role getManagerRole();

    Role getClientRole();
    
    List<Long> getRolesIds(List<Role> roles);

    List<Role> getRolesByIds(List<Long> ids);

    List<Role> findAllAvailableRoles();
}
