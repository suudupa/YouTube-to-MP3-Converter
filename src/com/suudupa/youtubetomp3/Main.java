package com.suudupa.youtubetomp3;

import java.net.MalformedURLException;
import java.net.URL;

public class Main {

    private static final String CONVERTER = "https://mp3fy.com/yfy/";

    public static void main(String[] args) {

        System.out.println("Enter the URL of the Youtube video: ");

        String url = System.console().readLine().trim();
        String key = getKey(url);

        URL converter = null;
        try {
            converter = new URL(CONVERTER + key);
        } catch (MalformedURLException e) {
            System.out.println("Sorry, there was an error converting the given URL!");
            exit(-1);
        }
    }

    private static String getKey(String url) {
        if (url.contains("=")) {
            String key = url.split("=")[1];
            if (key.contains("&")) {
                return key.split("&")[0];
            } else {
                return key;
            }
        } else {
            return url.split("/")[3];
        }
    }

    private static void exit(int status) {
        System.out.println("Exiting program...");
        System.exit(status);
    }
}