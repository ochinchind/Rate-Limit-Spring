package com.example.demo.controllers.api.users;

import com.example.demo.entities.users.User;
import com.example.demo.repositories.users.UserRepository;
import com.example.demo.services.pricingPlan.PricingPlanService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api2/users")
public class UserWithPricingPlanController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PricingPlanService pricingPlanService;

    @GetMapping("/pricing")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestHeader(value = "Authorization") String authHeader
    ) {
        System.out.println(authHeader);
        Bucket bucket = pricingPlanService.resolveBucket(authHeader);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok()
                    .header("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()))
                    .body(users);
        }
        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill))
                .build();
    }
}