package com.guinetik.challenge.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
/**
 * JPA repository for the Team entity
 * It has a couple of auto-generated methods to search by userId
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsUserEntityByUserId(String userId);
    UserEntity findFirstByUserId(String userId);

    @Query(value = "select nextval('user_id_seq')", nativeQuery = true)
    Long getNextSequence();
}
