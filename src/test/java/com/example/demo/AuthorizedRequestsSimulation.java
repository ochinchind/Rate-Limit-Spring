package com.example.demo;


import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.concurrent.ThreadLocalRandom;
public class AuthorizedRequestsSimulation extends Simulation  {

    ChainBuilder browse =
            // Note how we force the counter name, so we can reuse it
            repeat(31, "i").on(
                    http("Page #{i}").get("/api/greeting"),
                    pause(1)
            );

    HttpProtocolBuilder httpProtocol =
            http.baseUrl("http://localhost:8080")
                    .authorizationHeader("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhIiwiaWF0IjoxNzE0MTYxNzI5LCJleHAiOjE3MTQyODE3Mjl9.yGgZuluFyiTIwZAFMZuNy0_HWUyxLtFNDUU5DrxhoM1prdFvYBXhULDhfalf6wr9v26Ln9QzDtmh0itnF16Zfg");

    ScenarioBuilder users = scenario("Users").exec(browse);

    {
        setUp(
                users.injectOpen(rampUsers(1).during(40))
        ).protocols(httpProtocol);
    }
}