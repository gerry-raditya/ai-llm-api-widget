package com.rumahhobi.services;

import com.rumahhobi.domain.SubscriptionPlan;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubscriptionService {

    public SubscriptionPlan getPlan(String clientId) {
        if ("pro".equalsIgnoreCase(clientId)) return SubscriptionPlan.PRO;
        if ("basic".equalsIgnoreCase(clientId)) return SubscriptionPlan.BASIC;
        return SubscriptionPlan.FREE;
    }
}

