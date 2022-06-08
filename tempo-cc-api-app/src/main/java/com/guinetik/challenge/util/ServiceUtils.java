package com.guinetik.challenge.util;

import com.guinetik.challenge.model.RoleRelationship;
import com.guinetik.challenge.model.TeamDetails;
import com.guinetik.challenge.model.User;
import com.guinetik.challenge.remote.RemoteApiService;
import com.guinetik.challenge.service.exeptions.ServiceException;
import com.guinetik.challenge.service.exeptions.ServiceRequestException;
import com.guinetik.challenge.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ServiceUtils {

    private final TeamRoleRepository teamRoleRepo;
    private final RemoteApiService apiService;
    private final UserRepository userRepo;
    private final TeamRepository teamRepo;
    private boolean silent = false;

    private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);

    public ServiceUtils(TeamRoleRepository teamRoleRepo, RemoteApiService apiService, UserRepository userRepo, TeamRepository teamRepo) {
        this.teamRoleRepo = teamRoleRepo;
        this.apiService = apiService;
        this.userRepo = userRepo;
        this.teamRepo = teamRepo;
    }

    @Transactional
    public RoleRelationship validateRoleMembership(RoleEntity roleEntity, TeamEntity teamEntity, UserEntity userEntity) {
        try {
            // now we check if this relationship already exists
            if (!teamRoleRepo.existsByTeamAndUserAndRole(teamEntity, userEntity, roleEntity)) {
                // if it doesn't exist, let's add it to the DB, but before that, we need to
                // we need to check if there is another role assigned to this user and make sure to update instead of insert
                // since one user can only have one role on a team (i think)
                if (teamRoleRepo.existsByTeamAndUser(teamEntity, userEntity)) {
                    // updating existing membership
                    teamRoleRepo.updateRoleRelationship(teamEntity.getId(), userEntity.getId(), roleEntity.getId());
                    return ModelUtils.roleEntityToModel(teamRoleRepo.findFirstByTeamAndUser(teamEntity, userEntity));
                } else {
                    // all good lets create this membership!
                    return this.saveMembershipRole(ModelUtils.getTeamRoleEntity(roleEntity, teamEntity, userEntity, teamRoleRepo.getNextSequence()));
                }
            } else {
                if (!silent) throw new ServiceException("User is already assigned to this role for this team");
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Error while saving the membership");
        }
        return null;
    }

    public RoleRelationship saveMembershipRole(TeamRoleEntity roleRelation) {
        TeamRoleEntity savedEntity = teamRoleRepo.save(roleRelation);
        logger.info("Membership role saved: " + savedEntity.getUser().getUserId() + " as " + savedEntity.getRole().getName() + "  on team: " + savedEntity.getTeam().getTeamId());
        return ModelUtils.roleEntityToModel(savedEntity);
    }

    /**
     * Checks if the supplied userId exists on the remote API. Then creates it on our local domain db
     *
     * @param userId - the user id to check
     * @return - The entity model for this user after it's been created/read from the db
     */
    public UserEntity findAndValidateUserEntity(String userId) {
        UserEntity userEntity = null;
        // search for the user in the remote API
        User remoteUser = this.apiService.getUserById(userId);
        if (remoteUser == null) {
            if (!silent) throw new ServiceRequestException("User not found on remote API: " + userId);
        } else {
            // if the user exists, check if our local domain knows about it
            if (userRepo.existsUserEntityByUserId(userId)) {
                userEntity = userRepo.findFirstByUserId(userId);
            } else {
                // if the user doesn't exist on our local db, create a new row
                userEntity = new UserEntity(userRepo.getNextSequence(), userId);
                userEntity = userRepo.save(userEntity);
                logger.info("User saved: " + userId);
            }
        }
        if (userEntity == null) {
            if (!silent) throw new ServiceException("Unable to validate user entity");
        }
        return userEntity;
    }

    /**
     * Checks if the supplied teamId exists on the remote API. Then creates it on our local domain db
     *
     * @param teamId - the team id to check
     * @return - The entity model for this team after it's been created/read from the db
     */
    public TeamEntity findAndValidateTeamEntity(String teamId) {
        TeamEntity teamEntity = null;
        // check if teamId exists
        if (teamId == null) {
            if (!silent) throw new ServiceRequestException("Team not found on remote api");
        } else {
            // if the team exists, check if our local domains knows about it
            if (teamRepo.existsTeamEntityByTeamId(teamId)) {
                teamEntity = teamRepo.findFirstByTeamId(teamId);
            } else {
                // if the team doesn't exist on our local db, create a new row
                teamEntity = new TeamEntity(teamRepo.getNextSequence(), teamId);
                teamEntity = teamRepo.save(teamEntity);
                logger.info("Team saved: " + teamEntity);
            }
        }
        if (teamEntity == null) {
            if (!silent) throw new ServiceException("Unable to validate team entity");
        }
        return teamEntity;
    }

    /**
     * Checks if a user belongs to a team
     *
     * @param userId the userId to check
     * @param team   API model for the team, obtained via remote API
     * @return true if the user belongs to the team
     */
    public boolean isUserAssignedToTeam(String userId, TeamDetails team) {
        // we don't need much here, a simple contains will do
        return team.getTeamMemberIds().contains(userId);
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }
}
