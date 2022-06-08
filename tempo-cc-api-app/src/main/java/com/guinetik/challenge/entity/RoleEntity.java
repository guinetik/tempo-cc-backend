package com.guinetik.challenge.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

/**
 * Represents a Role entity on our local domain DB
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Immutable
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
    @SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq", allocationSize = 1, initialValue = 4)
    private Long id;
    private String name;
}
