package com.guinetik.challenge.api;

import com.guinetik.challenge.model.Role;
import com.guinetik.challenge.model.RoleInput;
import com.guinetik.challenge.model.RoleRelationship;
import com.guinetik.challenge.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service delegate that implements behavior for the Roles Api.
 * Internally, the generated swagger code with call for a default implementation of RolesApiDelegate,
 * which allow us to skip all the boilerplate spring rest controller stuff and focus on business.
 * This class uses a let-it-crash strategy trusting that the services underneath will raise the appropriate Exceptions.
 */
@Service
public class RolesApiDelegateImpl implements RolesApiDelegate {


    private final RolesService service;

    @Autowired
    public RolesApiDelegateImpl(RolesService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<RoleRelationship> assignRole(RoleRelationship roleRelationship) {
        return ResponseEntity.ok(
                service.assignRole(roleRelationship.getRoleId(), roleRelationship.getUserId(), roleRelationship.getTeamId())
        );
    }

    @Override
    public ResponseEntity<Role> createRole(RoleInput role) {
        return new ResponseEntity<>(service.createRole(role.getName()), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<RoleRelationship>> getMembershipsByRole(Long roleId) {
        return ResponseEntity.ok(service.getMembershipsByRole(roleId));
    }

    @Override
    public ResponseEntity<RoleRelationship> getRoleByMembership(String userId, String teamId) {
        return ResponseEntity.ok(service.getRoleByMembership(userId, teamId));
    }
}
