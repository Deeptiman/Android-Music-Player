package com.example.musicplayer.upload;

import android.content.Context;
import android.util.Log;
import com.example.musicplayer.database.Music;
import com.example.musicplayer.firebase.FirebaseListener;

public class UploadManager {

    String[] imageUrl = {"YOUR_ALBUM_IMAGE_URL"};

    String[] musicUrl = {"YOUR_MUSIC_MP3_URL"};

    String[] artistName = {"YOUR_ARTIST_NAME"};

    String[] musicName = {"YOUR_MUSIC_NAME"};

    Context mContext;

    String TAG = "UPLOAD_MNG";

    public UploadManager(Context context) {
        this.mContext = context;
    }


    public void upload() {

        for (int i = 0; i < imageUrl.length; i++) {

            String aName = artistName[i];
            String aPicture = imageUrl[i];
            String mName = musicName[i];
            String mUrl = musicUrl[i];

            Log.d(TAG, i + " = " + aName + " : " + aPicture + " : " + mName + " : " + mUrl);

            insertData(i, aName, aPicture, mName, mUrl);

        }
    }

    private void insertData(int id,
                            String artistName,
                            String albumPicture,
                            String musicName,
                            String musicUrl) {

        Music music = new Music();
        music.setArtistName(artistName);
        music.setAlbumPicture(albumPicture);
        music.setMusicName(musicName);
        music.setMusicUrl(musicUrl);
        music.setMusicKey("M" + id);

        new FirebaseListener(mContext).insertMusic(music);
    }

}
