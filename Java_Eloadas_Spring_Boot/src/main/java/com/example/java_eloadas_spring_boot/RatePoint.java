package com.example.java_eloadas_spring_boot;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RatePoint {
    private LocalDate date;
    private BigDecimal rate;

    public RatePoint(LocalDate date, BigDecimal rate) {
        this.date = date;
        this.rate = rate;
    }
    public LocalDate getDate() { return date; }
    public BigDecimal getRate() { return rate; }
}