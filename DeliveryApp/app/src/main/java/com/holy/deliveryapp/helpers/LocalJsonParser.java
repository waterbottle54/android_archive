package com.holy.deliveryapp.helpers;

import com.holy.deliveryapp.models.Local;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocalJsonParser {

    public static List<Local> parse(JSONObject response, boolean[] end) {

        List<Local> localList = new ArrayList<>();

        try {

            JSONObject meta = response.getJSONObject("meta");
            if (end != null && end.length > 0) {
                end[0] = meta.getBoolean("is_end");
            }

            JSONArray documents = response.getJSONArray("documents");

            for (int i = 0; i < documents.length(); i++) {

                JSONObject document = documents.getJSONObject(i);
                String id = document.getString("id");
                String categoryFullName = document.getString("category_name");
                String strDistance = document.getString("distance").trim();
                String phone = document.getString("phone");
                String name = document.getString("place_name");
                String url = document.getString("place_url");
                String address = document.getString("road_address_name");
                String strLatitude = document.getString("y").trim();
                String strLongitude = document.getString("x").trim();

                String[] categoryNames = categoryFullName.split(" > ");
                if (categoryNames.length < 3) {
                    continue;
                }
                String categoryName = categoryNames[1];
                String subcategoryName = categoryNames[2];

                if (strDistance.isEmpty() || strLatitude.isEmpty() || strLongitude.isEmpty()) {
                    continue;
                }
                double distance = Double.parseDouble(strDistance);
                double latitude = Double.parseDouble(strLatitude);
                double longitude = Double.parseDouble(strLongitude);

                Local local = new Local(id, categoryName, subcategoryName, distance, phone, name, url, address, latitude, longitude);
                localList.add(local);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return localList;
    }

}
