package com.penelope.weatherteacher.api.measurement;

import com.penelope.weatherteacher.data.measurement.Measurement;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MeasurementMapperTest extends TestCase {

    public void testGetMeasurement() {

        List<MeasurementResponse.Value> values1 = new ArrayList<>();
        values1.add(new MeasurementResponse.Value("T1H", "13.4"));
        values1.add(new MeasurementResponse.Value("RN1", "0"));
        values1.add(new MeasurementResponse.Value("UUU", "-0.3"));
        values1.add(new MeasurementResponse.Value("VVV", "-0.2"));
        values1.add(new MeasurementResponse.Value("REH", "78"));
        values1.add(new MeasurementResponse.Value("VEC", "53"));
        values1.add(new MeasurementResponse.Value("WSD", "0.5"));

        Measurement measurement1 = MeasurementMapper.getMeasurement(values1);

        assertNull(measurement1);
    }
}