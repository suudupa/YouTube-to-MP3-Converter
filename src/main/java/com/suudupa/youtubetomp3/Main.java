package com.suudupa.youtubetomp3;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("Welcome to the YouTube to MP3 Java converter!\n");


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


        //get download URL
        String downloader = null;
        try {
            downloader = Utils.getDownloadUrl(converter);
            System.out.println(url + " successfully parsed and converted.\n");
        } catch (IOException e) {
            System.out.println("Error fetching the download link!\n");
            Utils.exit(2);
        }


        //get download path
        System.out.println("Where would you like to download the MP3 file?");
        System.out.print("Enter the folder path: ");
        String path = reader.readLine().trim();
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) System.out.println("Found " + path + "\n");
        else {
            System.out.print("Could not locate " + path + "\n");
            Utils.exit(3);
        }


        //get current project directory
        String currDir = System.getProperty("user.dir");
        System.setProperty("webdriver.chrome.driver", currDir + "\\libs\\chromedriver.exe");


        //start chromedriver.exe
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless --disable-gpu");
        HashMap<String, Object> chromePreferences = new HashMap<>();
        chromePreferences.put("safebrowsing.enabled", false);
        chromePreferences.put("profile.default_content_settings.popups", 0);
        chromePreferences.put("download.default_directory", path);
        chromeOptions.setExperimentalOption("prefs", chromePreferences);
        WebDriver chromeDriver = new ChromeDriver(chromeOptions);
        chromeDriver.get(downloader);
        Utils.getDownloadProgress(chromeDriver);
        chromeDriver.close();
        Utils.exit(0);
    }
}