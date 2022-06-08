package com.guinetik.challenge.api;

import com.guinetik.challenge.model.Role;
import com.guinetik.challenge.model.RoleInput;
import com.guinetik.challenge.model.RoleRelationship;
import com.guinetik.challenge.service.RolesService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RolesApiDelegateImplTest {

    @MockBean
    private RolesService service;

    @Test
    void assignRole() {
        RoleRelationship roleRelationship = new RoleRelationship();
        roleRelationship.setRoleId(1L);
        roleRelationship.setTeamId("1");
        roleRelationship.setUserId("1");
        Mockito.when(service.assignRole(1L, "1", "1")).thenReturn(roleRelationship);
        RolesApiDelegateImpl rolesApiDelegate = new RolesApiDelegateImpl(service);
        ResponseEntity<RoleRelationship> roleRelationshipResponseEntity = rolesApiDelegate.assignRole(roleRelationship);
        assertEquals(200, roleRelationshipResponseEntity.getStatusCode().value());
    }

    @Test
    void createRole() {
        String roleName = "Developer";
        Role roleResult = new Role();
        roleResult.setId(1L);
        roleResult.setName(roleName);
        //
        Mockito.when(service.createRole(roleName)).thenReturn(roleResult);
        RoleInput i = new RoleInput();
        i.setName(roleName);
        //
        RolesApiDelegateImpl rolesApiDelegate = new RolesApiDelegateImpl(service);
        ResponseEntity<Role> roleResponseEntity = rolesApiDelegate.createRole(i);
        assertEquals(201, roleResponseEntity.getStatusCode().value());
    }

    @Test
    void getMembershipsByRole() {
        Mockito.when(service.getMembershipsByRole(1L)).thenReturn(Collections.emptyList());
        RolesApiDelegateImpl rolesApiDelegate = new RolesApiDelegateImpl(service);
        ResponseEntity<List<RoleRelationship>> membershipsByRole = rolesApiDelegate.getMembershipsByRole(1L);
        assertEquals(200, membershipsByRole.getStatusCode().value());
    }

    @Test
    void getRoleByMembership() {
        Mockito.when(service.getRoleByMembership("1", "1")).thenReturn(new RoleRelationship());
        RolesApiDelegateImpl rolesApiDelegate = new RolesApiDelegateImpl(service);
        ResponseEntity<RoleRelationship> roleByMembership = rolesApiDelegate.getRoleByMembership("1", "1");
        assertEquals(200, roleByMembership.getStatusCode().value());
    }
}
