package com.holy.radiorate;

import android.app.Application;
import android.content.res.TypedArray;

import com.holy.radiorate.helpers.SQLiteHelper;
import com.holy.radiorate.models.NuclearPlant;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (SQLiteHelper.getInstance(this).getAllNuclearPlants().isEmpty()) {
            insertNuclearPlants();
        }
    }

    private void insertNuclearPlants() {

        TypedArray typedArray = getResources().obtainTypedArray(R.array.nuclear_plant_list);

        for (int i = 0; i < typedArray.length(); i++) {
            int resId = typedArray.getResourceId(i, 0);
            String[] nuclearPlantData = getResources().getStringArray(resId);
            String id = nuclearPlantData[0];
            String name = nuclearPlantData[1];
            double latitude = Double.parseDouble(nuclearPlantData[2]);
            double longitude = Double.parseDouble(nuclearPlantData[3]);
            String address = nuclearPlantData[4];

            NuclearPlant nuclearPlant = new NuclearPlant(
              id,
              name,
              latitude,
              longitude,
              address
            );
            SQLiteHelper.getInstance(this).addNuclearPlant(nuclearPlant);
        }

        typedArray.recycle();
    }

}
