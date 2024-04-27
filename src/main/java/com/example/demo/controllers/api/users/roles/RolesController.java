package com.example.demo.controllers.api.users.roles;

import com.example.demo.entities.users.roles.Roles;
import com.example.demo.entities.users.roles.RolesGiven;
import com.example.demo.repositories.users.roles.RolesGivenRepository;
import com.example.demo.repositories.users.roles.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/roles")
public class RolesController {

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RolesGivenRepository rolesGivenRepository;

    @PostMapping("/create")
    public Roles createRole(@RequestBody Roles user) {
        return rolesRepository.save(user);
    }

    @PostMapping("/assign-user")
    public String assignUser(@RequestBody RolesGiven rolesGiven) {
        if (rolesGivenRepository.existsByRoleIdAndUserId(rolesGiven.getRoleId(), rolesGiven.getUserId())) {
            return "Assignment already exists!";
        }

        rolesGivenRepository.save(rolesGiven);

        return "Successfully assigned!";
    }
}