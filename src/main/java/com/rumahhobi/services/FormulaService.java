package com.rumahhobi.services;

import java.math.BigDecimal;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FormulaService {
   public BigDecimal hitungRAB(
            BigDecimal material,
            BigDecimal labor,
            BigDecimal overhead
    ) {

        BigDecimal safeMaterial = material != null ? material : BigDecimal.ZERO;
        BigDecimal safeLabor = labor != null ? labor : BigDecimal.ZERO;
        BigDecimal safeOverhead = overhead != null ? overhead : BigDecimal.ZERO;

        return safeMaterial
                .add(safeLabor)
                .add(safeOverhead);
    }
}
