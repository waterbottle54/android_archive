package com.davidjo.greenworld.data.photo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.davidjo.greenworld.api.PixabayApi;
import com.davidjo.greenworld.api.PixabayResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoRepository {

    public static final int PHOTO_COUNT = 10;

    private final PixabayApi pixabayApi;

    @Inject
    public PhotoRepository(PixabayApi pixabayApi) {
        this.pixabayApi = pixabayApi;
    }

    // 쿼리문을 통해서 Pixabay 에 사진을 요청하고 결과를 리턴함

    public LiveData<List<PixabayPhoto>> getSearchResults(String query) {

        if (query == null || query.isEmpty()) {
            return new MutableLiveData<>(new ArrayList<>());
        }

        MutableLiveData<List<PixabayPhoto>> photos = new MutableLiveData<>();

        Call<PixabayResponse> photosCall = pixabayApi.getResponse(query, PHOTO_COUNT);
        photosCall.enqueue(new Callback<PixabayResponse>() {
            @Override
            public void onResponse(@NonNull Call<PixabayResponse> call, @NonNull Response<PixabayResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    photos.setValue(null);
                    return;
                }
                photos.setValue(response.body().getHits());
            }

            @Override
            public void onFailure(@NonNull Call<PixabayResponse> call, @NonNull Throwable t) {
                photos.setValue(null);
            }
        });

        return photos;
    }

}
