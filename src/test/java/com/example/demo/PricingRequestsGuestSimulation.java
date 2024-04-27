package com.example.demo;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.http.HttpDsl.http;

public class PricingRequestsGuestSimulation extends Simulation {

    ChainBuilder pricing =
            // Note how we force the counter name, so we can reuse it
            repeat(25, "i").on(
                    http("Page #{i}").get("/api2/users/pricing")
                            .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiIiwiaWF0IjoxNzE0MTk0OTk2LCJleHAiOjE3MTQzMTQ5OTZ9.qegd2_OYHH8FMIvakU8Mq1wn-7kZlxV1VAweIqnLtNdALntaI9nHaUipf27K0jKqli-uBuB0GBR6rnxEhytnnw"),
                    pause(1)
            );

    HttpProtocolBuilder httpProtocol =
            http.baseUrl("http://localhost:8080")
                    .authorizationHeader("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiIiwiaWF0IjoxNzE0MTk0OTk2LCJleHAiOjE3MTQzMTQ5OTZ9.qegd2_OYHH8FMIvakU8Mq1wn-7kZlxV1VAweIqnLtNdALntaI9nHaUipf27K0jKqli-uBuB0GBR6rnxEhytnnw");

    ScenarioBuilder users = scenario("Users").exec(pricing);

    {
        setUp(
                users.injectOpen(rampUsers(1).during(15))
        ).protocols(httpProtocol);
    }
}
