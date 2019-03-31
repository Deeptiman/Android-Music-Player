package com.example.musicplayer.database;


import io.realm.RealmObject;

public class Music extends RealmObject {
    /**
     * musicUrl : s.mp3
     * albumPicture : s.jpg
     * artistName : Shakira
     * musicName : Hips Don 't Lie
     */

    private String musicKey;
    private String musicUrl;
    private String albumPicture;
    private String artistName;
    private String musicName;

    public String getMusicKey() {
        return musicKey;
    }

    public void setMusicKey(String musicKey) {
        this.musicKey = musicKey;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getAlbumPicture() {
        return albumPicture;
    }

    public void setAlbumPicture(String albumPicture) {
        this.albumPicture = albumPicture;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    //{musicUrl=s.mp3, albumPicture=s.jpg, artistName=Shakira, musicName=Hips Don't Lie}


}
