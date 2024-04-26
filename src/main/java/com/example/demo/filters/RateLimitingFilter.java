package com.example.demo.filters;

import io.github.bucket4j.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitingFilter extends GenericFilterBean {

    // Map to store Bucket4j instances for each IP address
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Configure rate limiting rules (e.g., 100 requests per minute)
    private final Bandwidth limit = Bandwidth.classic(100, Refill.intervally(10, Duration.ofMinutes(1)));

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get the IP address of the client
        String clientIP = getClientIP(httpRequest);

        // Get or create a Bucket4j instance for the client's IP
        Bucket bucket = buckets.computeIfAbsent(clientIP, k -> createNewBucket());

        // Try to consume a token from the bucket
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // If a token is consumed, proceed with the request
            chain.doFilter(request, response);
        } else {
            // If rate limit exceeded, send a 429 Too Many Requests response
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().write("Rate limit exceeded");
            httpResponse.getWriter().flush();
        }
    }

    // Create a new Bucket4j instance with the configured bandwidth limit
    private Bucket createNewBucket() {
        return Bucket4j.builder().addLimit(limit).build();
    }

    // Method to extract client's IP address from the request
    private String getClientIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || "".equals(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

}