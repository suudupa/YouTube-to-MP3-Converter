package com.suudupa.youtubetomp3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.suudupa.youtubetomp3.Utils.FILE_FORMAT;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("\nWelcome to the YouTube to MP3 Java converter!\n");


        //get converter URL
        System.out.print("Enter the URL of the Youtube video: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String url = reader.readLine().trim();
        String converter = null;
        try {
            converter = Utils.convertUrl(url);
        } catch (NullPointerException e) {
            System.out.println("Error parsing the given URL (invalid format)!\n");
            Utils.exit(1);
        }


        //get download data
        Download download = null;
        try {
            download = Utils.getDownload(converter);
            System.out.println(url + " successfully parsed and converted.\n");
        } catch (IOException e) {
            System.out.println("Error fetching the download link!\n");
            Utils.exit(2);
        }


        //get download path
        System.out.println("Where would you like to download the MP3 file?");
        System.out.print("Enter the folder path: ");
        String path = Utils.formatPath(reader.readLine().trim());
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) System.out.println("Found " + path + ".\n");
        else {
            System.out.print("Could not locate " + path + "\n");
            Utils.exit(3);
        }


        //create new file in download path
        System.out.print("Enter the title of the song: ");
        String filename = reader.readLine().trim() + FILE_FORMAT;
        String mp3File = path + "\\" + filename;


        //download MP3 file from download URL
        Utils.download(download, mp3File);
        Utils.exit(0);
    }
}