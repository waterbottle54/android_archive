package com.cool.realtimebus.data.arrival;

import java.util.Objects;

public class Arrival {

    private final String routeId;
    private final String routeTitle;
    private final int secondsLeft;
    private final int stationsLeft;

    public Arrival(String routeId, String routeTitle, int secondsLeft, int stationsLeft) {
        this.routeId = routeId;
        this.routeTitle = routeTitle;
        this.secondsLeft = secondsLeft;
        this.stationsLeft = stationsLeft;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getRouteTitle() {
        return routeTitle;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public int getStationsLeft() {
        return stationsLeft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arrival arrival = (Arrival) o;
        return secondsLeft == arrival.secondsLeft && stationsLeft == arrival.stationsLeft && routeId.equals(arrival.routeId) && routeTitle.equals(arrival.routeTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId, routeTitle, secondsLeft, stationsLeft);
    }
}
