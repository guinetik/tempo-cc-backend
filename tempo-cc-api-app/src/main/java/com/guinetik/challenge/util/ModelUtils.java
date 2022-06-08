package com.guinetik.challenge.util;

import com.guinetik.challenge.entity.RoleEntity;
import com.guinetik.challenge.entity.TeamEntity;
import com.guinetik.challenge.entity.TeamRoleEntity;
import com.guinetik.challenge.entity.UserEntity;
import com.guinetik.challenge.model.Role;
import com.guinetik.challenge.model.RoleRelationship;

import java.util.LinkedList;
import java.util.List;

public class ModelUtils {
    public static List<RoleRelationship> getMembershipsByRole(List<TeamRoleEntity> teamRoles) {
        List<RoleRelationship> result = new LinkedList<>();
        teamRoles.forEach(s -> addRoleEntityToList(result, s));
        return result;
    }

    private static void addRoleEntityToList(List<RoleRelationship> result, TeamRoleEntity s) {
        RoleRelationship r = roleEntityToModel(s);
        result.add(r);
    }

    public static Role roleEntityToModel(RoleEntity r) {
        Role role = new Role();
        role.setName(r.getName());
        role.setId(r.getId());
        return role;
    }

    /**
     * this method is mostly a sanity check to avoid null pointer in case of deletions on the db
     * @param teamRole - membership entity with role data
     * @return - api model with role data
     */
    public static RoleRelationship roleEntityToModel(TeamRoleEntity teamRole) {
        RoleRelationship r = new RoleRelationship();
        RoleEntity role = teamRole.getRole();
        if (role != null) {
            r.setRoleId(role.getId());
            r.setName(role.getName());
        }
        UserEntity user = teamRole.getUser();
        if (user != null) {
            r.setUserId(user.getUserId());
        }
        TeamEntity team = teamRole.getTeam();
        if (team != null) {
            r.setTeamId(team.getTeamId());
        }
        return r;
    }

    public static TeamRoleEntity getTeamRoleEntity(RoleEntity r, TeamEntity t, UserEntity u, Long nextSequence) {
        TeamRoleEntity roleRelation = new TeamRoleEntity(nextSequence, u, t, r);
        return roleRelation;
    }

    public static RoleEntity createRoleEntity(String name, Long nextSequence) {
        return new RoleEntity(nextSequence, name);
    }

    public static TeamEntity getTeamEntity(Long id, String teamId) {
        return new TeamEntity(id, teamId);
    }
}
