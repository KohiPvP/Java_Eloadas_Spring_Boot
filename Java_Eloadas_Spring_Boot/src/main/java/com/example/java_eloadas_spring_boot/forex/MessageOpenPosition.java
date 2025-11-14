package com.example.java_eloadas_spring_boot.forex;

public class MessageOpenPosition {
    private String instrument;
    private int units;

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }
}
