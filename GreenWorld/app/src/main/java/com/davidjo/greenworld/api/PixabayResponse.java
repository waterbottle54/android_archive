package com.davidjo.greenworld.api;

import com.davidjo.greenworld.data.photo.PixabayPhoto;

import java.util.List;

public class PixabayResponse {

    // Pixabay API 가 제공하는 json 포맷에 일치시킨 클래스

    private final List<PixabayPhoto> hits;

    public PixabayResponse(List<PixabayPhoto> hits) {
        this.hits = hits;
    }

    public List<PixabayPhoto> getHits() {
        return hits;
    }
}
