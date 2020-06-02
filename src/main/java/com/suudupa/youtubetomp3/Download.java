package com.suudupa.youtubetomp3;

import java.net.URL;

public class Download {

    private final URL url;
    private final long totalSize;

    public Download(URL url, long totalSize) {
        this.url = url;
        this.totalSize = totalSize;
    }

    public URL getUrl() {
        return this.url;
    }

    public long getTotalSize() {
        return this.totalSize;
    }
}