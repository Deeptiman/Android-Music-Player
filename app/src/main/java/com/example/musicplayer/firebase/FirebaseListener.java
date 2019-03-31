package com.example.musicplayer.firebase;

import android.content.Context;
import android.util.Log;

import com.example.musicplayer.database.Music;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseListener {

    FirebaseDatabase mDatabase = null;

    Context mContext;

    String musicPlayer = "Music";

    String TAG = "DBMusicManager";

    public FirebaseListener(Context context) {
        this.mContext = context;
        this.mDatabase = FirebaseDatabase.getInstance();
    }

    public void addMusicPlayerListener() {

        Log.d(TAG,"addMusicPlayerListener");

        MusicPlayerListener musicPlayerListener = new MusicPlayerListener();
        mDatabase.getReference(musicPlayer).addChildEventListener(musicPlayerListener);
    }


    public void insertMusic(Music music){
        mDatabase.getReference(musicPlayer).child(music.getMusicKey()).setValue(music);
    }

}
