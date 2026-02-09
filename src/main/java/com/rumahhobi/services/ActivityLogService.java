package com.rumahhobi.services;

import com.rumahhobi.entity.ActivityLogEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ActivityLogService {
    
    @Transactional
    public void log(String clientId, String userId, String action, String detail){
        ActivityLogEntity log = new ActivityLogEntity();
        log.clientId = clientId;
        log.userId = userId;
        log.action = action;
        log.detail = detail;
        log.persist();
    }
}
