package com.guinetik.challenge.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for the User-Team-Role relationship
 * It has a couple of auto-generated methods to search by team and user and role.
 */
@Repository
public interface TeamRoleRepository extends JpaRepository<TeamRoleEntity, Long> {
    TeamRoleEntity findFirstByTeamAndUser(TeamEntity team, UserEntity user);

    List<TeamRoleEntity> findAllByRoleId(Long roleId);

    boolean existsByTeamAndUserAndRole(TeamEntity team, UserEntity user, RoleEntity role);

    boolean existsByTeamAndUser(TeamEntity team, UserEntity user);

    @Modifying
    @Query(value = "update TEAM_ROLE_ENTITY set team_id = ?1, user_id = ?2, role_id = ?3 where team_id = ?1 and user_id = ?2", nativeQuery = true)
    void updateRoleRelationship(Long teamId, Long userId, Long roleId);

    @Query(value = "select nextval('team_role_id_seq')", nativeQuery = true)
    Long getNextSequence();

    int countAllByTeam(TeamEntity team);
}
