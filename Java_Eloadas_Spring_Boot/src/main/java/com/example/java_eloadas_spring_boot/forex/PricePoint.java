package com.example.java_eloadas_spring_boot.forex;

public class PricePoint {
    private double price;
    private long liquidity;
    public PricePoint() {}
    public PricePoint(double price, long liquidity) { this.price = price; this.liquidity = liquidity; }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getLiquidity() {
        return liquidity;
    }

    public void setLiquidity(long liquidity) {
        this.liquidity = liquidity;
    }
}
