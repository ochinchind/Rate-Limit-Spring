package com.example.demo.services.pricingPlan;

import com.example.demo.entities.users.Tokens;
import com.example.demo.entities.users.User;
import com.example.demo.entities.users.roles.Roles;
import com.example.demo.entities.users.roles.RolesGiven;
import com.example.demo.filters.JwtAuthenticationFilter;
import com.example.demo.repositories.users.TokenRepository;
import com.example.demo.repositories.users.UserRepository;
import com.example.demo.repositories.users.roles.RolesGivenRepository;
import com.example.demo.repositories.users.roles.RolesRepository;
import com.example.demo.res.PricingPlan;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.demo.res.PricingPlan.*;

@Service
public class PricingPlanService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private RolesGivenRepository rolesGivenRepository;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String authHeader) {
        return cache.computeIfAbsent(authHeader, this::newBucket);
    }

    private Bucket newBucket(String authHeader) {
        var apiKey = authHeader.substring(JwtAuthenticationFilter.BEARER_PREFIX.length());
        PricingPlan pricingPlan = resolvePlanFromApiKey(apiKey);
        return Bucket.builder()
                .addLimit(pricingPlan.getLimit())
                .build();
    }

    private PricingPlan resolvePlanFromApiKey(String apiKey) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("auth");
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            System.out.println("auth");
            if (principal instanceof UserDetails) {
                System.out.println("auth");
                User userDetails = (User) principal;
                Long userId = userDetails.getId();
                System.out.println(userId);
                Optional<RolesGiven> rolesGivenOptional = rolesGivenRepository.findByUserId(userId);
                System.out.println(rolesGivenOptional);
                if (rolesGivenOptional.isPresent()) {
                    System.out.println("auth");
                    RolesGiven rolesGiven = rolesGivenOptional.get();
                    if (Objects.equals(rolesGiven.getRoleId(), 1L)) {
                        return PROFESSIONAL;
                    } else if (Objects.equals(rolesGiven.getRoleId(), 2L)) {
                        return BASIC;
                    }
                }
            }
        }
        return FREE;
    }
}