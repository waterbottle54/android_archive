package com.penelope.weatherteacher.api.measurement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// 기상청 초단기실황 API 을 위한 Retrofit interface

public interface MeasurementApi {

    String BASE_URL = "http://apis.data.go.kr/";
    String SERVICE_KEY = "xg9o0%2BtaSHcj9cuhS%2Ff6KtSuvLA2Xq7AEDZl7HtWrcGUpnbC38kPJmvDXBg1kPRaIL%2B1DTz1JYhXcUIlVovttA%3D%3D";

    @GET("1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?dataType=json&serviceKey=" + SERVICE_KEY)
    Call<MeasurementResponse> getResponse(
            @Query("base_time") String baseTime,
            @Query("base_date") String baseDate,
            @Query("nx") int nx,
            @Query("ny") int ny
    );

}
