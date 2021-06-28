package com.arapp.cityintro.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import com.arapp.cityintro.models.CityInfo;
import com.google.gson.Gson;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UtilService {

    public static List<CityInfo> LoadCityInfoLFromJson(Context context, String path) {
        List<CityInfo> cityInfoList = new ArrayList<>();

        try {
            // Read given json file into buffer
            InputStream is = context.getAssets().open(path);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            // Make json string and parse it.
            String json = new String(buffer, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Map<String, Map> map = gson.fromJson(json, Map.class);
            for (Map mapCity : map.values()) {
                String name = (String) mapCity.get("name");
                String desc1 = (String) mapCity.get("desc1");
                String desc2 = (String) mapCity.get("desc2");
                double lat = (double) mapCity.get("lat");
                double lon = (double) mapCity.get("lon");
                CityInfo cityInfo = new CityInfo(name, desc1, desc2, lat, lon);
                cityInfoList.add(cityInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cityInfoList;
    }

    public static String getDirectionString(int degrees) {

        if (degrees > 337.5 || degrees <= 22.5) return "East";
        if (degrees > 22.5 && degrees <= 67.5) return "Southeast";
        if (degrees > 67.5 && degrees <= 112.5) return "South";
        if (degrees > 112.5 && degrees <= 157.5) return "Southwest";
        if (degrees > 157.5 && degrees <= 202.5) return "West";
        if (degrees > 202.5 && degrees <= 247.5) return "Northwest";
        if (degrees > 247.5 && degrees <= 292.5) return "North";
        return "Northeast";
    }

}
