package com.rumahhobi.entity;

import java.math.BigDecimal;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

@Entity
@Table(indexes = {
    @Index(name = "idx_rab_client", columnList = "clientId"),
    @Index(name = "idx_rab_user", columnList = "userId")
})
public class RabHistoryEntity extends PanacheEntity  {
    public String clientId;
    public String userId;

    @Column(precision = 18, scale = 2)
    public BigDecimal materialCost;

    @Column(precision = 18, scale = 2)
    public BigDecimal laborCost;

    @Column(precision = 18, scale = 2)
    public BigDecimal overheadCost;

    @Column(precision = 18, scale = 2)
    public BigDecimal totalCost;
}
