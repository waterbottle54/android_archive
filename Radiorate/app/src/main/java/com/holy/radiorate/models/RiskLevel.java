package com.holy.radiorate.models;

import android.widget.SearchView;

import com.holy.radiorate.R;

import java.util.Objects;

public class RiskLevel {

    public static final int SafeLongTerm = 1;
    public static final int SafeMediumTerm = 2;
    public static final int SafeShortTerm = 3;
    public static final int Risky = 4;
    public static final int Danger = 5;
    public static final int HighDanger = 6;
    public static final int Severe = 7;
    public static final int VerySevere = 8;
    public static final int Fatal = 9;

    private final int level;

    public RiskLevel(double value) {

        if (value < 0.2) {
            level = SafeLongTerm;
        } else if (value < 0.5) {
            level = SafeMediumTerm;
        } else if (value < 1.0) {
            level = SafeShortTerm;
        } else if (value < 5) {
            level = Risky;
        } else if (value < 10) {
            level = Danger;
        } else if (value < 1000) {
            level = HighDanger;
        } else if (value < 100000) {
            level = Severe;
        } else if (value < 1000000) {
            level = VerySevere;
        } else {
            level = Fatal;
        }
    }

    public RiskLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o.getClass() == int.class) {
            int riskLevel = (int) o;
            return level == riskLevel;
        } else if (o.getClass() == RiskLevel.class) {
            RiskLevel riskLevel = (RiskLevel) o;
            return level == riskLevel.level;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level);
    }

    public static RiskLevel[] getRiskLevels() {

        return new RiskLevel[] {
                new RiskLevel(SafeLongTerm),
                new RiskLevel(SafeMediumTerm),
                new RiskLevel(SafeShortTerm),
                new RiskLevel(Risky),
                new RiskLevel(Danger),
                new RiskLevel(HighDanger),
                new RiskLevel(Severe),
                new RiskLevel(VerySevere),
                new RiskLevel(Fatal),
        };
    }

}
