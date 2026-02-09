package com.rumahhobi.services;

import java.math.BigDecimal;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RABService {
    public BigDecimal hitungRAB(
            BigDecimal material,
            BigDecimal labor,
            BigDecimal overhead) {
        return material
                .add(labor)
                .add(overhead);
    }
}
