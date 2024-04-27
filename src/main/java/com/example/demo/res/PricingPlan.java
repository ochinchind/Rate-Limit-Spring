package com.example.demo.res;

import com.example.demo.entities.users.Tokens;
import com.example.demo.repositories.users.TokenRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

import java.time.Duration;

public enum PricingPlan {
    FREE {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.classic(5, Refill.intervally(5, Duration.ofHours(1)));
        }
    },
    BASIC {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.classic(10, Refill.intervally(10, Duration.ofHours(1)));
        }
    },
    PROFESSIONAL {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.classic(20, Refill.intervally(20, Duration.ofHours(1)));
        }
    };
    public abstract Bandwidth getLimit();

}