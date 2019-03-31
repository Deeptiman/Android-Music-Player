package com.example.musicplayer.database;

import android.util.Log;

import io.realm.Realm;

public class DBMusicManager implements Realm.Transaction, Realm.Transaction.OnSuccess,
        Realm.Transaction.OnError {

    String TAG = "DBMusicManager";

    public static final int ADD_MUSIC = 0;
    public static final int UPDATE_MUSIC = 1;

    int mTransactionType;
    Music mMusic;
    Realm mRealm;

    public DBMusicManager(int transactionType, Music music) {

        this.mTransactionType = transactionType;
        mMusic = music;
    }

    public void execute() {
        try {
            RealmManager.getRealm().executeTransactionAsync(this, this, this);
        } catch (Exception e) {
            Log.d(TAG, "execute : Exception = " + e.toString());
        }
    }


    @Override
    public void execute(Realm realm) {

        this.mRealm = realm;

        Log.d(TAG, "transactionType = " + mTransactionType);

        switch (mTransactionType) {

            case ADD_MUSIC:
                addMusic();
                break;
            case UPDATE_MUSIC:
                updateMusic();
                break;

        }
    }

    private void addMusic() {

        if (getMusicByKey(mMusic.getMusicKey()) == null) {

            Log.d(TAG, "Music Key Add : " + mMusic.getMusicKey());
            Log.d(TAG, "Music getAlbumPicture Add : " + mMusic.getAlbumPicture());
            Log.d(TAG, "Music getArtistName Add : " + mMusic.getArtistName());
            Log.d(TAG, "Music getMusicName Add : " + mMusic.getMusicName());
            Log.d(TAG, "Music getMusicUrl Add : " + mMusic.getMusicUrl());

            Music music = mRealm.createObject(Music.class);
            music.setAlbumPicture(mMusic.getAlbumPicture());
            music.setArtistName(mMusic.getArtistName());
            music.setMusicKey(mMusic.getMusicKey());
            music.setMusicName(mMusic.getMusicName());
            music.setMusicUrl(mMusic.getMusicUrl());
        } else {
            Log.d(TAG, "Music Key Exists : " + mMusic.getMusicKey());
        }
    }

    private void updateMusic() {

        Music music = getMusicByKey(mMusic.getMusicKey());
        music.setAlbumPicture(mMusic.getAlbumPicture());
        music.setArtistName(mMusic.getArtistName());
        music.setMusicKey(mMusic.getMusicKey());
        music.setMusicName(mMusic.getMusicName());
        music.setMusicUrl(mMusic.getMusicUrl());

    }

    private Music getMusicByKey(String key) {
        return mRealm.where(Music.class).equalTo("musicKey", key).findFirst();
    }


    @Override
    public void onError(Throwable error) {

    }

    @Override
    public void onSuccess() {

    }
}
