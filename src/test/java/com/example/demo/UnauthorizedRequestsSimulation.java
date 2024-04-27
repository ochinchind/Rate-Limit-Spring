package com.example.demo;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.concurrent.ThreadLocalRandom;
public class UnauthorizedRequestsSimulation extends Simulation  {

    ChainBuilder browse =
            // Note how we force the counter name, so we can reuse it
            repeat(15, "i").on(
                    http("Page #{i}").get("/api/greet")
                            .header("X-FORWARDED-FOR", "192.167.104.32"),
                    pause(1)
            );

    HttpProtocolBuilder httpProtocol =
            http.baseUrl("http://localhost:8080")
                    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .acceptLanguageHeader("en-US,en;q=0.5")
                    .acceptEncodingHeader("gzip, deflate")
                    .userAgentHeader(
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
                    );

    ScenarioBuilder users = scenario("Users").exec(browse);

    {
        setUp(
                users.injectOpen(rampUsers(1).during(15))
        ).protocols(httpProtocol);
    }
}
