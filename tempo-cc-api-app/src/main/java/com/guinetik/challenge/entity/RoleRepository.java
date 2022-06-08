package com.guinetik.challenge.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for the Role entity
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findTopByName(String name);

    @Query(value = "select nextval('role_id_seq')", nativeQuery = true)
    Long getNextSequence();
}
