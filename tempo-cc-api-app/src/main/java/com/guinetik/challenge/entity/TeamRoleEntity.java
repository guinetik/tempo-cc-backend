package com.guinetik.challenge.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

/**
 * Represents a relationship between a user and one of their teams and the role they perform in that team.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Immutable
public class TeamRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_role_id_seq")
    @SequenceGenerator(name = "team_role_id_seq", sequenceName = "team_role_id_seq", allocationSize = 1)
    private Long id;

    @OneToOne()
    private UserEntity user;
    @OneToOne()
    private TeamEntity team;

    @OneToOne()
    private RoleEntity role = new RoleEntity(1L, "Developer");
}
