package com.guinetik.challenge.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
/**
 * JPA repository for the Team entity
 * It has a couple of auto-generated methods to search by teamId
 */
@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
    boolean existsTeamEntityByTeamId(String teamId);
    TeamEntity findFirstByTeamId(String teamId);

    @Query(value = "select nextval('team_id_seq')", nativeQuery = true)
    Long getNextSequence();
}
