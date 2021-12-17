package com.penelope.highwaycharger;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StationListResult {

    @SerializedName("currentCount")
    private final int count;

    @SerializedName("data")
    private final List<Station> stations;

    public StationListResult(int count, List<Station> stations) {
        this.count = count;
        this.stations = stations;
    }

    public int getCount() {
        return count;
    }

    public List<Station> getStations() {
        return stations;
    }
}
