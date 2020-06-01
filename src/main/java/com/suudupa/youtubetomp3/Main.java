package com.suudupa.youtubetomp3;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        //get video URL
        System.out.println("Welcome to the YouTube to MP3 Java converter!\n");
        System.out.print("Enter the URL of the Youtube video: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String url = reader.readLine().trim();
        String key = null;
        try {
            key = getKey(url);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Sorry, there was an error converting the given URL (invalid format)!\n");
            exit(1);
        }


        //convert to URL object
        URL converter = null;
        try {
            converter = new URL("https://mp3fy.com/yfy/" + key);
        } catch (MalformedURLException e) {
            System.out.println("Sorry, there was an error converting the given URL (URL doesn't exist)!\n");
            exit(1);
        }

        System.out.println(url + " successfully parsed.\n");


        //get download path
        System.out.println("Where would you like to download the MP3 file?");
        System.out.print("Enter the folder path: ");
        String path = reader.readLine().trim();
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) System.out.println("Found " + path + "\n");
        else exit(2);


        //get current project directory
        String currDir = System.getProperty("user.dir");
        System.setProperty("webdriver.chrome.driver", currDir + "\\libs\\chromedriver.exe");


        //start chromedriver.exe
        ChromeOptions chromeOptions = new ChromeOptions();
        //chromeOptions.addArguments("--headless --disable-gpu");
        chromeOptions.addArguments("--headless");
        HashMap<String, Object> chromePreferences = new HashMap<>();
        chromePreferences.put("safebrowsing.enabled", false);
        chromePreferences.put("profile.default_content_settings.popups", 0);
        chromePreferences.put("download.default_directory", path);
        chromeOptions.setExperimentalOption("prefs", chromePreferences);
        WebDriver chromeDriver = new ChromeDriver(chromeOptions);
        chromeDriver.get(String.valueOf(converter));
        getDownloadProgress(chromeDriver);
        chromeDriver.close();
    }


    //get key associated to the YouTube video
    private static String getKey(String url) throws ArrayIndexOutOfBoundsException {
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


    //get download progress of MP3 file
    private static void getDownloadProgress(WebDriver driver) throws InterruptedException {

        //open chrome downloads tab
        String mainWindow = driver.getWindowHandle();
        JavascriptExecutor newTab = (JavascriptExecutor) driver;
        newTab.executeScript("window.open()");
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
        driver.get("chrome://downloads");

        //parse downloads tab to get download progress
        JavascriptExecutor downloads = (JavascriptExecutor) driver;
        boolean isFirstIteration = true;
        boolean isDownloadStarting = false;
        Long prevPercentage = (long) -1;
        Long percentage = (long) 0;
        long downloadStart = System.currentTimeMillis();
        while (percentage != 100) {
            try {
                percentage = (Long) downloads.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('#progress').value");
                if (isDownloadStarting) System.out.println("Starting download...");
                if (!prevPercentage.equals(percentage)) System.out.println(percentage + "% completed");
                prevPercentage = percentage;
                isDownloadStarting = false;
            } catch (Exception e) {
                if (isFirstIteration) System.out.println("\nWaiting for server to begin download...");
            }
            Thread.sleep(100);
            isFirstIteration = false;
        }

        //download completed
        long downloadEnd = System.currentTimeMillis();
        String filename = (String) downloads.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('div#content #file-link').text");
        String downloadPath = (String) downloads.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('div.is-active.focus-row-active #file-icon-wrapper img').src");
        File file = new File(downloadPath);
        long downloadTime = downloadEnd - downloadStart;
        System.out.println("\n" + filename + " successfully downloaded in " + downloadPath + " (" + file.length() + " bytes" + " in " + downloadTime/1000L + "s.");
        driver.close();
        driver.switchTo().window(mainWindow);
    }

    private static void exit(int status) {
        System.out.println("Exiting program...\n");
        System.exit(status);
    }
}