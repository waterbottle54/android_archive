package com.penelope.ecar.api.place;

import com.penelope.ecar.models.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaceJsonParser {

    public static Place parse(JSONObject response) {

        // PlaceApi 에서 전달된 json 을 파싱하여 Place 리턴

        try {
            JSONArray results = response.getJSONArray("results");
            if (results.length() == 0) {
                return null;
            }

            JSONObject result = results.getJSONObject(0);

            JSONObject code = result.getJSONObject("code");
            String codeId = code.getString("id");

            JSONObject region = result.getJSONObject("region");
            JSONObject area1 = region.getJSONObject("area1");
            JSONObject area2 = region.getJSONObject("area2");
            JSONObject area3 = region.getJSONObject("area3");
            String name1 = area1.getString("name");
            String name2 = area2.getString("name");
            String name3 = area3.getString("name");

            return new Place(codeId, name1, name2, name3);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
