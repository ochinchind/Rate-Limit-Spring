package com.example.demo;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.http.HttpDsl.http;

public class PricingRequestsAdminSimulation extends Simulation {

    ChainBuilder pricing =
            // Note how we force the counter name, so we can reuse it
            repeat(25, "i").on(
                    http("Page #{i}").get("/api2/users/pricing")
                            .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhIiwiaWF0IjoxNzE0MTYxNzI5LCJleHAiOjE3MTQyODE3Mjl9.yGgZuluFyiTIwZAFMZuNy0_HWUyxLtFNDUU5DrxhoM1prdFvYBXhULDhfalf6wr9v26Ln9QzDtmh0itnF16Zfg"),
                    pause(1)
            );

    HttpProtocolBuilder httpProtocol =
            http.baseUrl("http://localhost:8080")
                    .authorizationHeader("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhIiwiaWF0IjoxNzE0MTYxNzI5LCJleHAiOjE3MTQyODE3Mjl9.yGgZuluFyiTIwZAFMZuNy0_HWUyxLtFNDUU5DrxhoM1prdFvYBXhULDhfalf6wr9v26Ln9QzDtmh0itnF16Zfg");

    ScenarioBuilder users = scenario("Users").exec(pricing);

    {
        setUp(
                users.injectOpen(rampUsers(1).during(15))
        ).protocols(httpProtocol);
    }
}
