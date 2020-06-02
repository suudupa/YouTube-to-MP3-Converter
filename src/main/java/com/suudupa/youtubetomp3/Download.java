package com.suudupa.youtubetomp3;

import java.net.URL;

public class Download {

    private final URL url;
    private final double totalSize;

    public Download(URL url, double totalSize) {
        this.url = url;
        this.totalSize = totalSize;
    }

    public URL getUrl() {
        return this.url;
    }

    public double getTotalSize() {
        return this.totalSize;
    }
}