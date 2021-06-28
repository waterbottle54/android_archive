package com.holy.blueplayer;

import android.net.Uri;

public class AudioInfo {

    private final Uri uri;
    private final String title;
    private final Uri albumArtUri;

    public AudioInfo(Uri uri, String title, Uri albumArtUri) {
        this.uri = uri;
        this.title = title;
        this.albumArtUri = albumArtUri;
    }

    public Uri getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public Uri getAlbumArtUri() {
        return albumArtUri;
    }

}
