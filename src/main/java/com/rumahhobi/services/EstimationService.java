package com.rumahhobi.services;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EstimationService {
    public Integer estimasiHari(Integer quantity, Integer capacityPerDay) {

        if (quantity == null || capacityPerDay == null || capacityPerDay <= 0) {
            return 0;
        }

        return (int) Math.ceil(
            (double) quantity / capacityPerDay
        );
    }
}
