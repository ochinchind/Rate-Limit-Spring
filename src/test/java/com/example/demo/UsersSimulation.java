package com.example.demo;


import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.concurrent.ThreadLocalRandom;

public class UsersSimulation  extends Simulation {
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080"); // Update with your Spring Boot app URL

    ScenarioBuilder scn = scenario("UserSimulation")
            .exec(http("Generate Token")
                    .get("/api/users/generate-token")
                    .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhIiwiaWF0IjoxNzE0MTYxNzI5LCJleHAiOjE3MTQyODE3Mjl9.yGgZuluFyiTIwZAFMZuNy0_HWUyxLtFNDUU5DrxhoM1prdFvYBXhULDhfalf6wr9v26Ln9QzDtmh0itnF16Zfg")) // Endpoint for generating token
            .pause(1) // Pause between requests
            .repeat(10, "i").on(
            http("Page #{i}").get("/api/users/1")
                    .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhIiwiaWF0IjoxNzE0MTYxNzI5LCJleHAiOjE3MTQyODE3Mjl9.yGgZuluFyiTIwZAFMZuNy0_HWUyxLtFNDUU5DrxhoM1prdFvYBXhULDhfalf6wr9v26Ln9QzDtmh0itnF16Zfg"),
            pause(1)
            );

    {
        setUp(
                scn.injectOpen(atOnceUsers(1)) // Start one user to execute the scenario
        ).protocols(httpProtocol);
    }
}
