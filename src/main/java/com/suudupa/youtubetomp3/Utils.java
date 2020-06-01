package com.suudupa.youtubetomp3;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.IOException;

public class Utils {

    private static final String DELIMITER = "youtube";
    private static final String CONVERTER = "320";
    private static final String DOWNLOAD_ELEMENT = "a.btn.btn-success.btn-lg";
    private static final String DOWNLOAD_LINK = "abs:href";
    public static final String CHROMEDRIVER = "\\libs\\chromedriver.exe";
    private static final String NEW_TAB_SCRIPT = "window.open()";
    private static final String DOWNLOADS_TAB = "chrome://downloads";

    private static final int SLEEP = 100;

    //converter URL
    public static String convertUrl(String url) throws NullPointerException {
        int index = url.indexOf(DELIMITER);
        StringBuilder convertedUrl = new StringBuilder(url);
        return convertedUrl.insert(index, CONVERTER).toString();
    }

    //download URL
    public static String getDownloadUrl(String url) throws IOException {
        Document pageSource = Jsoup.connect(url).get();
        return pageSource.select(DOWNLOAD_ELEMENT).first().attr(DOWNLOAD_LINK);
    }

    //get download progress of MP3 file
    public static void getDownloadProgress(WebDriver driver) throws InterruptedException {

        //open chrome downloads tab
        String mainWindow = driver.getWindowHandle();
        JavascriptExecutor newTab = (JavascriptExecutor) driver;
        newTab.executeScript(NEW_TAB_SCRIPT);
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
        driver.get(DOWNLOADS_TAB);

        //parse downloads tab to get download progress
        JavascriptExecutor downloads = (JavascriptExecutor) driver;
        Long percentage = (long) 0;
        long downloadStart = System.currentTimeMillis();
        while (percentage != 100) {
            try {
                percentage = (Long) downloads.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('#progress').value");
                System.out.println(percentage + "% completed");
            } catch (Exception e) {
                System.out.println("\nWaiting for server to begin download...");
            }
            //Thread.sleep(SLEEP);
        }

        //download completed
        long downloadEnd = System.currentTimeMillis();
        String filename = (String) downloads.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('div#content #file-link').text");
        long downloadTime = (downloadEnd - downloadStart) / 1000L;
        System.out.println("\n" + filename + " successfully downloaded in " + downloadTime + "s." + "\n");
        driver.close();
        driver.switchTo().window(mainWindow);
    }

    //exit program
    public static void exit(int status) {
        System.out.println("Exiting program...\n");
        System.exit(status);
    }
}