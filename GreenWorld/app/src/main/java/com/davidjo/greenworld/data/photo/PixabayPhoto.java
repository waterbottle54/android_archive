package com.davidjo.greenworld.data.photo;

import java.util.Objects;

public class PixabayPhoto {

    private final String previewURL;    // 사진 url

    public PixabayPhoto(String previewURL) {
        this.previewURL = previewURL;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PixabayPhoto that = (PixabayPhoto) o;
        return Objects.equals(previewURL, that.previewURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previewURL);
    }

}
