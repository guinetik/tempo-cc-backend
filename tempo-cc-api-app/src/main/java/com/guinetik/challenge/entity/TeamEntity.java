package com.guinetik.challenge.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

/**
 * Represents a Team entity on our local domain DB.
 * Even tough this microservice is not the source of the team data,
 * it is important to keep a minimal domain db set up with this data in order to keep constraints in check.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Immutable
public class TeamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_id_seq")
    @SequenceGenerator(name = "team_id_seq", sequenceName = "team_id_seq", allocationSize = 1)
    private Long id;
    private String teamId;
}
