package com.rumahhobi.entity;

import java.time.Instant;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class ActivityLogEntity extends PanacheEntity {
    public String clientId;
    public String userId;
    public String action;
    public String detail;
    public Instant createdAt = Instant.now();
}
