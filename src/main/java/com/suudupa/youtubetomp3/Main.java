package com.suudupa.youtubetomp3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("\nWelcome to the YouTube to MP3 Java converter!\n");


        //get list of urls
        System.out.print("Enter a comma-separated list of the YouTube video URLs you wish to download: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String[] urls = reader.readLine().trim().split(",");
        System.out.println();

        //get list of songs to download
        ArrayList<Song> songs = new ArrayList<>();
        for (String url : urls) {
            try {
                songs.add(new Song(url.trim()));
                System.out.println(url.trim() + " successfully parsed and converted.");
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println(String.format("Error parsing %s (invalid format) -> skipped!", url));
            } catch (IOException e) {
                System.out.println(String.format("Error fetching the download link for %s -> skipped!", url));
            }
        }
        System.out.println();

        if (songs.size() < 1) Utils.exit(1);


        //get download path
        System.out.println("Where would you like to download the MP3 files?");
        System.out.print("Enter the folder path: ");
        String path = Utils.formatDownloadPath(reader.readLine().trim());
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) System.out.println("Found " + path + ".\n");
        else {
            System.out.print("Could not locate " + path + "\n");
            Utils.exit(2);
        }


        //download each song in the list
        for (Song song : songs) {
            String songTitle = song.getDownload().getSongTitle();
            String mp3File = Utils.getFilePath(path, songTitle);
            Utils.download(song.getDownload(), mp3File);
        }

        Utils.exit(0);
    }
}