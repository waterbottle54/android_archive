package com.holy.radiorate.models;

import java.util.Date;
import java.util.Objects;

public class RadioactivityData {

    private final String explanation;
    private final String name;
    private final Date time;
    private final double value;

    public RadioactivityData(String explanation, String name, Date time, double value) {
        this.explanation = explanation;
        this.name = name;
        this.time = time;
        this.value = value;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getName() {
        return name;
    }

    public Date getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

    public RiskLevel getRiskLevel() {
        return new RiskLevel(value);
    }

    @Override
    public String toString() {
        return "RadioactivityData{" +
                "explanation='" + explanation + '\'' +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", value=" + value +
                '}';
    }

}
