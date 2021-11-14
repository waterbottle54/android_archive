package com.penelope.weatherteacher.di;

import android.app.Application;

import com.penelope.weatherteacher.api.geocoding.GeocodingApi;
import com.penelope.weatherteacher.api.measurement.MeasurementApi;
import com.penelope.weatherteacher.api.reversegeocoding.ReverseGeocodingApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(MeasurementApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public MeasurementApi provideMeasurementApi(Retrofit retrofit) {
        return retrofit.create(MeasurementApi.class);
    }

    @Provides
    @Singleton
    public ReverseGeocodingApi provideReverseGeocodingApi(Application app) {
        return new ReverseGeocodingApi(app);
    }

    @Provides
    @Singleton
    public GeocodingApi provideGeocodingApi(Application app) {
        return new GeocodingApi(app);
    }

}
