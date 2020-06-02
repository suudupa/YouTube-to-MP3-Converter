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
    private static final String DOWNLOAD_BTN = "a.btn.btn-success.btn-lg";
    private static final String DOWNLOAD_LINK = "abs:href";
    private static final String PARAGRAPH = "p";
    public static final String FILE_FORMAT = ".mp3";

    private static final int DOWNLOAD_SIZE = 3;
    private static final int BUFFER = 1024;

    //get converter URL
    public static String convertUrl(String url) throws NullPointerException {
        int index = url.indexOf(DELIMITER);
        StringBuilder convertedUrl = new StringBuilder(url);
        return convertedUrl.insert(index, CONVERTER).toString();
    }

    //get download data
    public static Download getDownload(String url) throws IOException {
        Document pageSource = Jsoup.connect(url).get();
        String downloadUrl = pageSource.select(DOWNLOAD_BTN).first().attr(DOWNLOAD_LINK);
        String downloadSize = pageSource.select(PARAGRAPH).get(DOWNLOAD_SIZE).text();
        return new Download(new URL(downloadUrl), parseDownloadSize(downloadSize));
    }

    //parse download size from text
    private static long parseDownloadSize(String downloadSize) {
        downloadSize = downloadSize.replaceAll("[^\\d.]", "");
        double totalBytes;
        try {
            totalBytes = Double.parseDouble(downloadSize) * 1000000L;
        } catch (NumberFormatException e) {
            totalBytes = 0;
        }
        return (long) totalBytes;
    }

    //download file from download URL
    public static void download(Download download, String file) throws IOException {
        System.out.println("\nStarting download...");
        long downloadStart = System.currentTimeMillis();

        InputStream in = download.getUrl().openStream();
        File mp3File = new File(file);
        FileOutputStream fos = new FileOutputStream(mp3File);
        long bytesDownloaded = 0;
        long totalSize = download.getTotalSize();
        long percentage = 0;

        int length = -1;
        byte[] buffer = new byte[BUFFER];

        while ((length = in.read(buffer)) > -1) {
            fos.write(buffer, 0, length);
            bytesDownloaded += length;
            percentage = (bytesDownloaded / totalSize) * 100L;
            System.out.println(percentage + "% completed.");
        }

        fos.close();
        in.close();

        long downloadTime = (System.currentTimeMillis() - downloadStart) / 1000L;
        System.out.println("\n" + mp3File.getName() + " download successful (" + downloadTime + "s).\n");
    }

    //exit program
    public static void exit(int status) {
        System.out.println("Exiting program...");
        System.exit(status);
    }
}