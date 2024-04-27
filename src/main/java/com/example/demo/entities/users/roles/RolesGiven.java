package com.example.demo.entities.users.roles;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "users_roles_given")
public class RolesGiven {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "user_id")
    private Long user_id;

    @Column(nullable = false, name = "role_id")
    private Long role_id;
}