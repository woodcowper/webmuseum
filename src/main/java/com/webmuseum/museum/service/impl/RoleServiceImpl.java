package com.webmuseum.museum.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.entity.Role;
import com.webmuseum.museum.models.ERoleName;
import com.webmuseum.museum.repository.RoleRepository;
import com.webmuseum.museum.service.IRoleService;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> findAllAvailableRoles(){
        Role superAdminRole = getSuperAdminRole();
        return roleRepository.findAll().stream()
            .filter((role) -> role.getId() != superAdminRole.getId())
            .toList();
    }

    @Override
    public Role getSuperAdminRole(){
        return getRole(ERoleName.ROLE_SUPERADMIN.name());
    }

    @Override
    public Role getAdminRole(){
        return getRole(ERoleName.ROLE_ADMIN.name());
    }

    @Override
    public Role getManagerRole(){
        return getRole(ERoleName.ROLE_MANAGER.name());
    }

    @Override
    public Role getClientRole(){
        return getRole(ERoleName.ROLE_CLIENT.name());
    }

    @Override
    public List<Long> getRolesIds(List<Role> roles) {
        return roles.stream().map((role) -> role.getId()).toList();
    }

    @Override
    public List<Role> getRolesByIds(List<Long> ids) {
        return roleRepository.findAll().stream()
                .filter((role) -> ids.contains(role.getId()))
                .toList();
    }

    private Role getRole(String name){
        Role role = roleRepository.findByName(name);
        if(role == null){
            role = new Role();
            role.setName(name);
            roleRepository.save(role);
        }
        return role;
    }
}
