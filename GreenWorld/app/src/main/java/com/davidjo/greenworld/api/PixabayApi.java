package com.davidjo.greenworld.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PixabayApi {

    String BASE_URL = "https://pixabay.com/";
    String KEY = "23041690-1c819730d7deb9c5afa95c846";

    @GET("api/?key=" + KEY + "&image_type=photo")
    Call<PixabayResponse> getResponse(@Query("q") String query, @Query("per_page") int count);

}

