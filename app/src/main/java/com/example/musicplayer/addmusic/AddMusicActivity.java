package com.example.musicplayer.addmusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.customview.CustomButton;
import com.example.musicplayer.customview.CustomEditText;
import com.example.musicplayer.database.Music;
import com.example.musicplayer.database.RealmManager;
import com.example.musicplayer.firebase.FirebaseListener;
import com.example.musicplayer.helper.Utils;
import com.example.musicplayer.upload.UploadManager;

public class AddMusicActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatAutoCompleteTextView mArtistNameTxt;
    private CustomEditText mAlbumPicture, mMusicName, mMusicUrl;
    private CustomButton mAddMusicBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music);

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.action_bar_titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText("Add Music");

        this.getSupportActionBar().setCustomView(v);

        mArtistNameTxt = (AppCompatAutoCompleteTextView) findViewById(R.id.artist_name_txt_view);
        mAlbumPicture = (CustomEditText) findViewById(R.id.album_picture_url);
        mMusicName = (CustomEditText) findViewById(R.id.music_name_txt);
        mMusicUrl = (CustomEditText) findViewById(R.id.music_url_txt);
        mAddMusicBtn = (CustomButton) findViewById(R.id.add_music_btn);

        mAddMusicBtn.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.adapter_artist_name_view,
                RealmManager.getArtistName());
        mArtistNameTxt.setThreshold(1);
        mArtistNameTxt.setAdapter(adapter);

        //new UploadManager(this).upload();

    }


    private void insertData(String artistName,
                            String albumPicture,
                            String musicName,
                            String musicUrl) {

        Music music = new Music();
        music.setArtistName(artistName);
        music.setAlbumPicture(albumPicture);
        music.setMusicName(musicName);
        music.setMusicUrl(musicUrl);
        music.setMusicKey(RealmManager.getMusicKey());

        new FirebaseListener(this).insertMusic(music);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.add_music_btn:

                String artistName = mArtistNameTxt.getText().toString();
                String albumPicture = mAlbumPicture.getText().toString();
                String musicName = mMusicName.getText().toString();
                String musicUrl = mMusicUrl.getText().toString();

                if (artistName.length() > 0
                        && albumPicture.length() > 0
                        && musicName.length() > 0
                        && musicUrl.length() > 0) {

                    if (Utils.isValidUrl(albumPicture) &&
                            Utils.isValidUrl(musicUrl)) {
                        insertData(artistName, albumPicture, musicName, musicUrl);

                        resetView();

                        Toast.makeText(getApplicationContext(), "Music Added Successfully", Toast.LENGTH_SHORT).show();

                    } else if (!Utils.isValidUrl(albumPicture)) {
                        Toast.makeText(getApplicationContext(), "Add Valid Album Url", Toast.LENGTH_SHORT).show();
                    } else if (!Utils.isValidUrl(musicUrl)) {
                        Toast.makeText(getApplicationContext(), "Add Valid Music Url", Toast.LENGTH_SHORT).show();
                    }
                } else if (artistName.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Artist Name is missing", Toast.LENGTH_SHORT).show();
                } else if (albumPicture.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Album Picture Url is missing", Toast.LENGTH_SHORT).show();
                } else if (musicName.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Music Name is missing", Toast.LENGTH_SHORT).show();
                } else if (musicUrl.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Music Url is missing", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    private void resetView() {
        mArtistNameTxt.setText("");
        mAlbumPicture.setText("");
        mMusicName.setText("");
        mMusicUrl.setText("");
    }
}
