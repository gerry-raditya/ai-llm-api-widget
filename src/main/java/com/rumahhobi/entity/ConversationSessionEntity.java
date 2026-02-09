package com.rumahhobi.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class ConversationSessionEntity extends PanacheEntity  {
    public String clientId;
    public String userId;
    public String intent;
    public String status;
    
}
