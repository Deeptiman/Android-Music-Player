package com.example.musicplayer.helper;

import android.content.res.Resources;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public int stringTimeToMilliSeconds(String time) {
        int totalTime = 0;
        int numberOfColons = 0;
        List<Integer> myList = new ArrayList<Integer>();

        int i = 0;

        while (i < time.length()) {
            if (time.charAt(i) == ':') {
                numberOfColons++;
                myList.add(i);
            }
            i++;
        }

        if (numberOfColons == 2) {
            String hh = time.substring(0, myList.get(0));
            int hour = Integer.parseInt(hh);
            String min = time.substring(myList.get(0) + 1, myList.get(1));
            int minutes = Integer.parseInt(min);
            String ss = time.substring(myList.get(1) + 1, time.length());
            int seconds = Integer.parseInt(ss);
            totalTime = hour * 1000 * 60 * 60 + minutes * 1000 * 60 + seconds
                    * 1000;
        } else {
            String min = time.substring(0, myList.get(0));
            int minutes = Integer.parseInt(min);
            String ss = time.substring(myList.get(0) + 1, time.length());
            int seconds = Integer.parseInt(ss);
            totalTime = minutes * 1000 * 60 + seconds * 1000;
        }

        // return timer milliseconds
        return totalTime;
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress
     *            -
     * @param totalDuration
     *            returns current duration in milliseconds
     * */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public static boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
