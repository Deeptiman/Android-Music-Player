package com.example.musicplayer.appmanager;

import android.content.Context;

public class MusicAppManager {


    public static MusicAppManager appManager;
    public static Context appContext;

    public static MusicAppManager getInstance(Context context) {

        if (appManager == null) {

            appManager = new MusicAppManager();
            appContext = context;

        }
        return appManager;
    }

    public static Context getContext() {
        return appContext;
    }
}
