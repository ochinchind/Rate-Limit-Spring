package com.example.demo.repositories.users.roles;


import com.example.demo.entities.users.roles.RolesGiven;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesGivenRepository extends JpaRepository<RolesGiven, Long> {
    @Query("SELECT COUNT(r) > 0 FROM RolesGiven r WHERE r.roleId = :roleId AND r.userId = :userId")
    boolean existsByRoleIdAndUserId(@Param("roleId") Long roleId, @Param("userId") Long userId);

    Optional<RolesGiven> findByUserId(Long userId);
}
