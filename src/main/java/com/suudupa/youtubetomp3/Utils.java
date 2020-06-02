package com.suudupa.youtubetomp3;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;

public class Utils {

    private static final String DELIMITER = "youtube";
    private static final String CONVERTER = "320";
    private static final String DOWNLOAD_BTN = "a.btn.btn-success.btn-lg";
    private static final String DOWNLOAD_LINK = "abs:href";
    private static final String PARAGRAPH = "p";
    public static final String FILE_FORMAT = ".mp3";

    private static final int DOWNLOAD_SIZE = 3;
    private static final int BUFFER = 1024;
    private static final int PROGRESS_BAR = 140;

    //remove "" or '\' from path
    public static String formatPath(String path) {
        path = path.replaceAll("\"", "");
        if (path.charAt(path.length()-1) == '\\') path = path.substring(0, path.length()-1);
        return path;
    }

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
    private static double parseDownloadSize(String downloadSize) {
        downloadSize = downloadSize.replaceAll("[^\\d.]", "");
        double totalBytes;
        try {
            totalBytes = Double.parseDouble(downloadSize) * 1000000D;
        } catch (NumberFormatException e) {
            totalBytes = 0;
        }
        return totalBytes;
    }

    //download file from download URL
    public static void download(Download download, String file) throws IOException {
        System.out.println("\nStarting download...");
        double downloadStart = System.currentTimeMillis();

        InputStream in = download.getUrl().openStream();
        File mp3File = new File(file);
        FileOutputStream fos = new FileOutputStream(mp3File);
        double bytesDownloaded = 0;
        double totalSize = download.getTotalSize();
        int prevPercentage = -1;
        int percentage = 0;

        int length = -1;
        byte[] buffer = new byte[BUFFER];

        while ((length = in.read(buffer)) > -1) {
            fos.write(buffer, 0, length);
            bytesDownloaded += length;
            percentage = (int) ((bytesDownloaded / totalSize) * 100D);
            //if (percentage != prevPercentage && percentage <= 100) System.out.print("\r" + percentage + "% completed.");
            if (percentage != prevPercentage && percentage <= 100) printDownloadProgress(percentage);
            prevPercentage = percentage;
        }

        fos.close();
        in.close();

        double downloadTime = (System.currentTimeMillis() - downloadStart) / 1000D;
        System.out.println("\n\n" + mp3File.getName() + " download successful (" + downloadTime + "s).\n");
    }

    private static void printDownloadProgress(int percentage) {
        StringBuilder progress = new StringBuilder(PROGRESS_BAR);
        progress
                .append('\r')
                .append(String.join("", Collections.nCopies(percentage == 0 ? 2 : 2 - (int) (Math.log10(percentage)), " ")))
                .append(String.format("%d%% completed [", percentage))
                .append(String.join("", Collections.nCopies(percentage, "=")))
                .append('>')
                .append(String.join("", Collections.nCopies(100 - percentage, " ")))
                .append(']');
        System.out.print(progress);
    }

    //exit program
    public static void exit(int status) {
        System.out.println("Exiting program...");
        System.exit(status);
    }
}