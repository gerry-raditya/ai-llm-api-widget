package com.rumahhobi.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class ConversationStateEntity extends PanacheEntity {
    public Long sessionId;
    public String fieldName;
    public String fieldValue;
}
