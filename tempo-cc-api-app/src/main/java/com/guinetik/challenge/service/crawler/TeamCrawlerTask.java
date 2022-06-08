package com.guinetik.challenge.service.crawler;

import com.guinetik.challenge.entity.*;
import com.guinetik.challenge.model.TeamDetails;
import com.guinetik.challenge.remote.RemoteApiService;
import com.guinetik.challenge.util.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Scope(proxyMode = ScopedProxyMode.INTERFACES)
public class TeamCrawlerTask {

    private static final Logger logger = LoggerFactory.getLogger(TeamCrawlerTask.class);

    private final TeamRoleRepository teamRoleRepo;
    private final RemoteApiService apiService;

    private final ServiceUtils serviceUtils;

    public TeamCrawlerTask(TeamRoleRepository teamRoleRepo, TeamRepository teamRepo, UserRepository userRepo, RemoteApiService apiService) {
        this.teamRoleRepo = teamRoleRepo;
        this.apiService = apiService;
        this.serviceUtils = new ServiceUtils(teamRoleRepo, apiService, userRepo, teamRepo);
        this.serviceUtils.setSilent(true);
    }

    @Async
    public CompletableFuture<Boolean> fetchTeam(String teamId) {
        try {
            // get team details from API
            TeamDetails teamDetails = apiService.getTeamById(teamId);
            logger.info(teamId + ": got team details");
            // save a reference for a team a role entity we'll need for later
            TeamEntity team = this.serviceUtils.findAndValidateTeamEntity(teamId);
            RoleEntity role = new RoleEntity(1L, "Developer");
            // we need to check if our saved roles for the team has the same number as the number of members of the team
            int rolesOfTeam = teamRoleRepo.countAllByTeam(team);
            if (rolesOfTeam != teamDetails.getTeamMemberIds().size()) {
                // now loop through each team member and create a new user if one doesn't exist
                teamDetails.getTeamMemberIds().parallelStream().forEach(userId -> {
                    // if not we need to check if the user exists and if not, create a new user. this is encapsulated below
                    final UserEntity newUser = this.serviceUtils.findAndValidateUserEntity(userId);
                    this.serviceUtils.validateRoleMembership(role, team, newUser);
                    logger.info(teamId, "all roles crated");
                });
            } else {
                logger.info(teamId, "all roles already integrated");
            }
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }
}
