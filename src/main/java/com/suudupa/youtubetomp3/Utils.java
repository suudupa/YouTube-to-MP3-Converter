package com.suudupa.youtubetomp3;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Utils {

    private static final String DELIMITER = "youtube";
    private static final String CONVERTER = "320";
    private static final String DOWNLOAD_ELEMENT = "a.btn.btn-success.btn-lg";
    private static final String DOWNLOAD_LINK = "abs:href";
    public static final String FILE_FORMAT = ".mp3";

    private static final int BUFFER = 1024;

    //get converter URL
    public static String convertUrl(String url) throws NullPointerException {
        int index = url.indexOf(DELIMITER);
        StringBuilder convertedUrl = new StringBuilder(url);
        return convertedUrl.insert(index, CONVERTER).toString();
    }

    //get download URL
    public static URL getDownloadUrl(String url) throws IOException {
        Document pageSource = Jsoup.connect(url).get();
        String downloadUrl = pageSource.select(DOWNLOAD_ELEMENT).first().attr(DOWNLOAD_LINK);
        return new URL(downloadUrl);
    }

    //download file from download URL
    public static void download(URL url, String file) throws IOException {
        System.out.println("Starting download...");
        long downloadStart = System.currentTimeMillis();

        InputStream in = url.openStream();
        FileOutputStream fos = new FileOutputStream(new File(file));

        int length = -1;
        byte[] buffer = new byte[BUFFER];

        while ((length = in.read(buffer)) > -1) {
            fos.write(buffer, 0, length);
        }

        fos.close();
        in.close();

        long downloadTime = (System.currentTimeMillis() - downloadStart) / 1000L;
        System.out.println("Download successful (" + downloadTime + "s).\n");
    }

    //exit program
    public static void exit(int status) {
        System.out.println("Exiting program...\n");
        System.exit(status);
    }
}