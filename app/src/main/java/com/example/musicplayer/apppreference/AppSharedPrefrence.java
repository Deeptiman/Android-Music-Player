package com.example.musicplayer.apppreference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Arrays;

/**
 * @author Akash Jain
 * <p>
 * This class is shared prefrence which maintain data even application
 * is close.
 */
public class AppSharedPrefrence {

    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;
    private static AppSharedPrefrence AppSharedPrefrence;
    private static String MEDIA_PLAYER_CURRENT_POSITION = "mediaPlayerCurrentPosition";
    private static String MEDIA_PLAYER_TOTAL_DURATION = "mediaPlayersTotalDuration";
    private static String BUFFERING_PERCENTAGE = "buffering_percent";
    private static String MEDIA_PLAYER_STATE = "mediaPlayerState";
    private static String PLAYING_POSITION = "playing_position";
    private static String AUDIO_SESSION_ID = "audioSessionId";

    private static String PREV_ARTIST = "PREV_ARTIST";
    private static String CUR_ARTIST = "CUR_ARTIST";
    private static String PREV_SONG = "PREV_SONG";
    private static String CUR_SONG = "CUR_SONG";
    private static String VISUALIZER_BYTE_ARRAY = "VISUALIZER_BYTE_ARRAY";
    private static String IS_MUSIC_PLAYING = "IS_MUSIC_PLAYING";

    @SuppressLint("CommitPrefEdits")
    private AppSharedPrefrence(Context context) {
        this.appSharedPrefs = context.getSharedPreferences("sharedpref",
                Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public static AppSharedPrefrence getsharedprefInstance(Context con) {
        if (AppSharedPrefrence == null)
            AppSharedPrefrence = new AppSharedPrefrence(con);
        return AppSharedPrefrence;
    }

    public void clearPref(){
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public void setCurrentArtist(int currentPos) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt(CUR_ARTIST, currentPos);
        prefsEditor.commit();
    }

    public int getCurrentArtist() {
        return appSharedPrefs.getInt(CUR_ARTIST, 0);
    }

    public void setPrevArtist(int prevPos) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt(PREV_ARTIST, prevPos);
        prefsEditor.commit();
    }

    public int getPrevArtist() {
        return appSharedPrefs.getInt(PREV_ARTIST, 0);
    }

    public void setAudioSessionId(int audioSessionId) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt(AUDIO_SESSION_ID, audioSessionId);
        prefsEditor.commit();
    }

    public int getAudioSessionId() {
        return appSharedPrefs.getInt(AUDIO_SESSION_ID, 0);
    }

    public void setVisualizerByteArray(byte[] byteArray) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putString(VISUALIZER_BYTE_ARRAY, Arrays.toString(byteArray));
        prefsEditor.commit();
    }

    public byte[] getVisualizerByteArray() {
        String byteStringRes = appSharedPrefs.getString(VISUALIZER_BYTE_ARRAY, null);

        if(byteStringRes!=null) {
            String[] splitStr = byteStringRes.substring(1, byteStringRes.length()-1).split(", ");

            byte[] visualizeBytesArray = new byte[splitStr.length];

            for (int i = 0; i < splitStr.length; i++) {
                visualizeBytesArray[i] = Byte.parseByte(splitStr[i]);
            }
            return visualizeBytesArray;
        } else {
            return null;
        }
    }

    public void setIsMusicPlaying(boolean state) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putBoolean(IS_MUSIC_PLAYING, state);
        prefsEditor.commit();
    }
    
    public void setPlaying_position(int playing_position) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt(PLAYING_POSITION, playing_position);
        prefsEditor.commit();
    }

    public int getPlaying_position() {
        return appSharedPrefs.getInt(PLAYING_POSITION, 0);
    }

    public String getMediaplayerstate() {
        return appSharedPrefs.getString(MEDIA_PLAYER_STATE, "");
    }

    public void setMediaplayerstate(String mediaPlayerState) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putString(MEDIA_PLAYER_STATE, mediaPlayerState);
        prefsEditor.commit();
    }

    public int getBufferingpercentage() {
        return appSharedPrefs.getInt(BUFFERING_PERCENTAGE, 0);
    }

    public void setBufferingpercentage(int buffering_percent) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt(BUFFERING_PERCENTAGE, buffering_percent);
        prefsEditor.commit();
    }

    public void setMediaPlayerTotalDuration(int totalDurationMediaPlayer) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt(MEDIA_PLAYER_TOTAL_DURATION,
                totalDurationMediaPlayer);
        prefsEditor.commit();
    }

    public int getMediaPlayerTotalDuration() {
        return appSharedPrefs.getInt(MEDIA_PLAYER_TOTAL_DURATION, 0);
    }

    public void setMediaPlayerCurrentPosition(int mediaPlayerCurrentPosition) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt(MEDIA_PLAYER_CURRENT_POSITION,
                mediaPlayerCurrentPosition);
        prefsEditor.commit();
    }

    public int getMediaPlayerCurrentPosition() {
        return appSharedPrefs.getInt(MEDIA_PLAYER_CURRENT_POSITION, 0);
    }

    public void setPrevSong(String manualUrl) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putString(PREV_SONG, manualUrl);
        prefsEditor.commit();
    }

    public String getPrevSong() {
        return appSharedPrefs.getString(PREV_SONG, null);
    }

    public void setCurrentSong(String manualUrl) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putString(CUR_SONG, manualUrl);
        prefsEditor.commit();
    }

    public String getCurrentSong() {
        return appSharedPrefs.getString(CUR_SONG, null);
    }
}