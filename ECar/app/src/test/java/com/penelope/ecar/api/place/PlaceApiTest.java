package com.penelope.ecar.api.place;

import com.penelope.ecar.models.Place;

import junit.framework.TestCase;

public class PlaceApiTest extends TestCase {

    public void testGet() {

        Place place = PlaceApi.get(36.313505073321316, 127.86224373510308);

        if (place != null) {
            System.out.println(place.toString());
        } else {
            System.out.println("Shit");
        }
    }
}