package com.example.musicplayer.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.musicplayer.database.DBMusicManager;
import com.example.musicplayer.database.Music;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Map;

public class MusicPlayerListener implements ChildEventListener {


    String TAG = "DBMusicManager";


    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        Log.d(TAG, "onChildAdded : " + dataSnapshot.getValue());

        if (dataSnapshot.getValue() != null) {

            Map<String, Object> message = (Map<String, Object>) dataSnapshot.getValue();

            String artistName = (String) message.get("artistName");
            String albumPicture = (String) message.get("albumPicture");
            String musicName = (String) message.get("musicName");
            String musicUrl = (String) message.get("musicUrl");

            Log.d(TAG, " onChildAdded : getArtistName = " + artistName);
            Log.d(TAG, " onChildAdded : getAlbumPicture = " + albumPicture);
            Log.d(TAG, " onChildAdded : getMusicName = " + musicName);
            Log.d(TAG, " onChildAdded : getMusicUrl = " + musicUrl);

            Music music = new Music();
            music.setAlbumPicture(albumPicture);
            music.setArtistName(artistName);
            music.setMusicUrl(musicUrl);
            music.setMusicName(musicName);
            music.setMusicKey(dataSnapshot.getKey());

            DBMusicManager dbMusicManager = new DBMusicManager(DBMusicManager.ADD_MUSIC, music);
            dbMusicManager.execute();

        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        Log.d(TAG, "onChildChanged : " + dataSnapshot.getValue());

        if (dataSnapshot.getValue() != null) {

            Map<String, Object> message = (Map<String, Object>) dataSnapshot.getValue();

            String artistName = (String) message.get("artistName");
            String albumPicture = (String) message.get("albumPicture");
            String musicName = (String) message.get("musicName");
            String musicUrl = (String) message.get("musicUrl");

            Log.d(TAG, " onChildChanged : getArtistName = " + artistName);
            Log.d(TAG, " onChildChanged : getAlbumPicture = " + albumPicture);
            Log.d(TAG, " onChildChanged : getMusicName = " + musicName);
            Log.d(TAG, " onChildChanged : getMusicUrl = " + musicUrl);

            Music music = new Music();
            music.setAlbumPicture(albumPicture);
            music.setArtistName(artistName);
            music.setMusicUrl(musicUrl);
            music.setMusicName(musicName);
            music.setMusicKey(dataSnapshot.getKey());

            DBMusicManager dbMusicManager = new DBMusicManager(DBMusicManager.UPDATE_MUSIC, music);
            dbMusicManager.execute();
        }
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
