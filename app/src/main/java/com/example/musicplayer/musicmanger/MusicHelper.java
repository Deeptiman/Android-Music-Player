package com.example.musicplayer.musicmanger;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.example.musicplayer.database.Music;
import java.util.ArrayList;
import java.util.List;

public class MusicHelper {

    private static MusicHelper helper = null;
    private Context context = null;
    private List<Music> trackListMediaPlayer = new ArrayList<Music>();

    private MusicHelper(Context context) {
        this.context = context;
    }

    public static synchronized MusicHelper getInstance(Context context) {
        if (helper == null) {
            helper = new MusicHelper(context);
        }
        return helper;
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public void showToast(String message) {
        Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();

    }

    public List<Music> getTrackListMediaPlayer() {
        return trackListMediaPlayer;
    }

    public void setTrackListMediaPlayer(List<Music> trackListMediaPlayer) {
        this.trackListMediaPlayer = trackListMediaPlayer;
    }

}
