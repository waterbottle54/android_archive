package com.myapp.proximitysensortest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mProximitySensor;
    private TextView mProximityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProximityText = findViewById(R.id.txt_proximity);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (mProximitySensor != null) {
            Toast.makeText(this, "Proximity sensor available", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Proximity sensor not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        mProximityText.setText(String.format(Locale.getDefault(),
                "%f cm", event.values[0])
        );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mProximitySensor != null) {
            mSensorManager.registerListener(
                    this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mProximitySensor != null) {
            mSensorManager.unregisterListener(this, mProximitySensor);
        }
    }
}