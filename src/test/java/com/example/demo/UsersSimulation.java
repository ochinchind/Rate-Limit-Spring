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
                    .get("/api/users/generate-token")) // Endpoint for generating token
            .pause(1) // Pause between requests
            .repeat(10, "i").on(
            http("Page #{i}").get("/api/users"),
            pause(1)
            );

    {
        setUp(
                scn.injectOpen(atOnceUsers(1)) // Start one user to execute the scenario
        ).protocols(httpProtocol);
    }
}
