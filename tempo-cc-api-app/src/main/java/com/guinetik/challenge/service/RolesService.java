package com.guinetik.challenge.service;

import com.guinetik.challenge.entity.*;
import com.guinetik.challenge.model.Role;
import com.guinetik.challenge.model.RoleRelationship;
import com.guinetik.challenge.model.TeamDetails;
import com.guinetik.challenge.remote.RemoteApiService;
import com.guinetik.challenge.service.exeptions.EmptyRoleException;
import com.guinetik.challenge.service.exeptions.MembershipNotFoundException;
import com.guinetik.challenge.service.exeptions.ServiceException;
import com.guinetik.challenge.service.exeptions.ServiceRequestException;
import com.guinetik.challenge.util.ModelUtils;
import com.guinetik.challenge.util.ServiceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Class responsible for creating roles, assigning roles to team members and checking roles for a membership.
 */
@Service
public class RolesService {

    private final RoleRepository rolesRepo;
    private final TeamRoleRepository teamRoleRepo;
    private final TeamRepository teamRepo;
    private final UserRepository userRepo;
    private final RemoteApiService apiService;

    private final ServiceUtils serviceUtils;

    public RolesService(RoleRepository r, TeamRoleRepository t, TeamRepository tt, UserRepository u, RemoteApiService api) {
        this.rolesRepo = r;
        this.teamRoleRepo = t;
        this.teamRepo = tt;
        this.userRepo = u;
        this.apiService = api;
        this.serviceUtils = new ServiceUtils(teamRoleRepo, apiService, userRepo, teamRepo);
    }

    /**
     * Creates a new role on our local domain db
     *
     * @param name name of the role
     * @return Role model with ID
     */
    public Role createRole(String name) {
        if (rolesRepo.findTopByName(name) != null) throw new ServiceRequestException("That role already exist");
        try {
            RoleEntity savedEntity = rolesRepo.save(ModelUtils.createRoleEntity(name, rolesRepo.getNextSequence()));
            return ModelUtils.roleEntityToModel(savedEntity);
        } catch (Exception e) {
            throw new ServiceException("Error while saving the role");
        }
    }

    /**
     * Look up a role for a membership. A membership is defined by a user id and a team id.
     *
     * @param userId - the id of the user on the remote API
     * @param teamId - the id of the team on the remote API
     * @return - A model with the user's role within the team
     */
    public RoleRelationship getRoleByMembership(String userId, String teamId) {
        TeamDetails remoteTeam = this.apiService.getTeamById(teamId);
        UserEntity user = serviceUtils.findAndValidateUserEntity(userId);
        TeamEntity team = serviceUtils.findAndValidateTeamEntity(remoteTeam != null ? remoteTeam.getId() : null);
        TeamRoleEntity result;
        //
        try {
            result = teamRoleRepo.findFirstByTeamAndUser(team, user);
        } catch (Exception e) {
            throw new ServiceException("Unable to retrieve role memberships.");
        }
        if (result == null) {
            throw new MembershipNotFoundException("Couldn't find role for the provided membership");
        }
        return ModelUtils.roleEntityToModel(result);
    }

    /**
     * Look up memberships for a role
     *
     * @param roleId - id of the role on the local domain db
     * @return - a list with all the memberships (user ids and team ids) with that role
     */
    public List<RoleRelationship> getMembershipsByRole(Long roleId) {
        List<TeamRoleEntity> result = null;
        checkRoleExistsReturnAsOptional(roleId);
        try {
            result = teamRoleRepo.findAllByRoleId(roleId);
        } catch (Exception e) {
            throw new ServiceException("Unable to retrieve role memberships.");
        }
        if (result == null || result.size() == 0) {
            throw new EmptyRoleException("This role has no memberships");
        }
        return ModelUtils.getMembershipsByRole(result);
    }

    /**
     * Assigns a team member to a role
     *
     * @param userId - the id of the user on the remote API
     * @param teamId - the id of the team on the remote API
     * @return - A model with the user's newly created role within the team
     */
    @Transactional
    public RoleRelationship assignRole(Long roleId, String userId, String teamId) {
        // first, let's check if the role exists using Optional
        RoleEntity roleEntity = checkRoleExistsReturnAsOptional(roleId).get();
        // now let's grab a reference for the team on the API side, we'll need it for later.
        // if this value is null, it will throw an exception eventually
        TeamDetails remoteTeam = this.apiService.getTeamById(teamId);
        if (remoteTeam == null) throw new ServiceRequestException("Team not found on remote service: " + teamId);
        // find the team and user entities on the remote side, add them to our local domain if they don't exist yet
        TeamEntity teamEntity = serviceUtils.findAndValidateTeamEntity(remoteTeam != null ? remoteTeam.getId() : null);
        UserEntity userEntity = serviceUtils.findAndValidateUserEntity(userId);
        // using the remote team reference we got from earlier, let's check if this user belongs to this team
        if (serviceUtils.isUserAssignedToTeam(userId, remoteTeam)) {
            try {
                return serviceUtils.validateRoleMembership(roleEntity, teamEntity, userEntity);
            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServiceException("Error while saving role membership");
            }
        } else {
            throw new ServiceRequestException("User not assigned to this team!");
        }
    }

    private Optional<RoleEntity> checkRoleExistsReturnAsOptional(Long roleId) {
        Optional<RoleEntity> r = rolesRepo.findById(roleId);
        if (!r.isPresent()) throw new ServiceRequestException("Invalid Role");
        return r;
    }
}
