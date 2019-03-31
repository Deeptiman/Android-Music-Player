package com.example.musicplayer.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.example.musicplayer.appmanager.MusicAppManager;
import com.example.musicplayer.database.RealmManager;
import com.example.musicplayer.firebase.FirebaseListener;
import com.firebase.client.Firebase;

public class MusicPlayerApplication extends MultiDexApplication {


    String TAG = "MusicPlayerListener";

    @Override
    public void onCreate() {
        super.onCreate();

        MusicAppManager.getInstance(this);
        new RealmManager().initialization();

        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        Log.d(TAG,"MusicPlayerApplication : onCreate");


        new FirebaseListener(this).addMusicPlayerListener();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
