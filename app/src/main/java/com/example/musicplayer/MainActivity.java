package com.example.musicplayer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.addmusic.AddMusicActivity;
import com.example.musicplayer.apppreference.AppSharedPrefrence;
import com.example.musicplayer.artist.ArtistListAdapter;
import com.example.musicplayer.database.Music;
import com.example.musicplayer.database.RealmManager;
import com.example.musicplayer.visualizer.VisualizeManager;
import com.example.musicplayer.musicplayer.PlayerFragment;
import com.example.musicplayer.musiclist.MusicListFragment;
import com.example.musicplayer.permission.PermissionModel;
import com.example.musicplayer.visualizer.BarVisualizer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import io.realm.OrderedRealmCollection;

public class MainActivity extends AppCompatActivity {


    private SlidingUpPanelLayout mLayout, mSubLayout;
    private RecyclerView mArtistMusicList;
    private LinearLayout mDragView, mSubDragView;
    private ImageView musicImg;
    private TextView musicNameTxt;
    private Button musicPausePlay;
    private ImageView mArtistHeaderBackView;
    private TextView mArtistHeaderTxt;

    private MusicListFragment mMusicListFragment;
    private PlayerFragment mPlayerFragment;
    private ArtistListAdapter mArtistListAdapter;

    private PermissionModel permissionModel;

    private int mPlayingPos = 0;

    public boolean PAUSE_PLAY_STATE = true;

    AppSharedPrefrence appSharedPrefrence;

    String TAG = "MusicSliderPanel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.action_bar_titleview, null);

        ((TextView) v.findViewById(R.id.title)).setText("Music Player");

        this.getSupportActionBar().setCustomView(v);

        getSupportActionBar().setIcon(R.drawable.add_song);

        initView();

        clickListeners();


        permissionModel = new PermissionModel();

        permissionModel.checkRecordAudioPermission(this, new PermissionModel.PermissionContract() {
            @Override
            public void onReceivePermission() {

                appSharedPrefrence = AppSharedPrefrence.getsharedprefInstance(MainActivity.this);
                mPlayerFragment = new PlayerFragment();
                mMusicListFragment = new MusicListFragment();

                musicListFragment(mMusicListFragment);
                replaceFragment(mPlayerFragment);
            }
        });

    }

    private void initView() {
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSubLayout = (SlidingUpPanelLayout) findViewById(R.id.sub_sliding_layout);
        mArtistMusicList = (RecyclerView) findViewById(R.id.artist_music_list_view);
        mDragView = (LinearLayout) findViewById(R.id.dragView);
        mSubDragView = (LinearLayout) findViewById(R.id.sub_dragView);
        mArtistHeaderTxt = (TextView) findViewById(R.id.header_artist_name_txt);
        mArtistHeaderBackView = (ImageView) findViewById(R.id.artist_header_back_view);
        musicImg = (ImageView) findViewById(R.id.music_img_view);
        musicNameTxt = (TextView) findViewById(R.id.music_name_txt);
        musicPausePlay = (Button) findViewById(R.id.music_pause_play);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_music_btn:
                startActivity(new Intent(MainActivity.this, AddMusicActivity.class));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        permissionModel.hasRecordAudioPermission();
    }

    public void musicListFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.music_list_container, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.music_player_container, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public void setPlayingPos(int pos) {
        mPlayingPos = pos;
        appSharedPrefrence.setPlaying_position(pos);
    }

    public void setMusicList(String artistName) {

        OrderedRealmCollection<Music> musicList =
                RealmManager.getAllModelList(Music.class, "artistName", artistName);

        ArrayList<Music> musicArrayList = new ArrayList<>();
        musicArrayList.addAll(musicList);
        onReceiveMusicList(musicArrayList);
    }

    public void onReceiveMusicList(ArrayList<Music> musicArrayList) {

        Music music = musicArrayList.get(mPlayingPos);
        setUpSlideView(music);

        mPlayerFragment.setTrackListMediaPlayer(musicArrayList);

        PAUSE_PLAY_STATE = false;
        musicPausePlay.setBackgroundResource(R.drawable.pause_sub_music);

        updatePlayState();
        musicPausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(PlayerFragment.DEBUG_TAG, "onReceiveMusicList");

                pausePlayState(PAUSE_PLAY_STATE);
                mPlayerFragment.pausePlayMusic();
            }
        });
    }

    public void pausePlayState(boolean pause_play_state) {

        if (pause_play_state) {
            PAUSE_PLAY_STATE = false;
            musicPausePlay.setBackgroundResource(R.drawable.pause_sub_music);
        } else {
            PAUSE_PLAY_STATE = true;
            musicPausePlay.setBackgroundResource(R.drawable.play_sub_music);
        }
    }

    public void playArtistPlayer() {
        mMusicListFragment.playArtistPlayer();

        setVisualizer();
    }

    public static String VISUALIZE_TAG = "VISUALIZE_TAG";

    private void setVisualizer() {

        Log.d(VISUALIZE_TAG, "setVisualizer : " + appSharedPrefrence.getAudioSessionId());
        VisualizeManager visualizeManager = new VisualizeManager(getApplicationContext(),
                appSharedPrefrence.getAudioSessionId());
        visualizeManager.execute(new VisualizeManager.VisualizeCallback() {
            @Override
            public void onReceiveByteData(byte[] bytes) {
                appSharedPrefrence.setVisualizerByteArray(bytes);
                showVisualizer();
            }
        });
    }

    public void openArtistMusics(Music music) {

        mSubLayout.setVisibility(View.VISIBLE);
        mSubLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

        mArtistHeaderTxt.setText(music.getArtistName());

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mArtistMusicList.setLayoutManager(manager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);

        mArtistMusicList.addItemDecoration(dividerItemDecoration);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.recycle_view_divider));

        OrderedRealmCollection<Music> musicList = RealmManager.getAllModelList(Music.class,
                "artistName",
                music.getArtistName());

        mArtistListAdapter = new ArtistListAdapter(this, musicList, true);
        mArtistMusicList.setAdapter(mArtistListAdapter);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePlayState();
            }
        }, 300);
    }

    public static String ARTIST_TAG = "ARTIST_TAG";

    BarVisualizer mBarVisualizer;

    private void updatePlayState() {

        if (mArtistListAdapter == null)
            return;

        int prevPlayingArtist = getItemId(appSharedPrefrence.getPrevSong());
        updateView(prevPlayingArtist, false);

        int curPlayingArtist = getItemId(appSharedPrefrence.getCurrentSong());
        updateView(curPlayingArtist, true);

        Log.d(ARTIST_TAG, "getPrevSong = " + appSharedPrefrence.getPrevSong());
        Log.d(ARTIST_TAG, "getCurrentSong = " + appSharedPrefrence.getCurrentSong());

    }

    public void updateView(int pos, boolean playing) {

        Log.d(ARTIST_TAG, "updateView = " + pos);

        View view = mArtistMusicList.getChildAt(pos);
        if (view == null)
            return;

        RelativeLayout artistLayout = (RelativeLayout) view.findViewById(R.id.artist_layout);
        mBarVisualizer = (BarVisualizer) view.findViewById(R.id.artist_music_visualizer);
        TextView musicNameTxt = (TextView) view.findViewById(R.id.artist_adapter_music_name_txt);

        mBarVisualizer.setVisibility(playing ? View.VISIBLE : View.INVISIBLE);
        mBarVisualizer.setColor(Color.parseColor("#FFFFFF"));
        mBarVisualizer.setDensity(BarVisualizer.density);

        musicNameTxt.setTextColor(Color.parseColor("#FFFFFF"));
        artistLayout.setBackgroundColor(playing ? Color.parseColor("#e50914") :
                Color.parseColor("#221F1F"));
    }

    public void showVisualizer() {
        if (mBarVisualizer != null)
            mBarVisualizer.invalidate();

        mMusicListFragment.showVisualizer();
    }

    public int getItemId(String musicUrl) {

        for (int i = 0; i < mArtistListAdapter.getItemCount(); i++) {
            Music music = mArtistListAdapter.getItem(i);
            if (music == null || music.getMusicUrl() == null)
                return -1;
            if (music.getMusicUrl().equals(musicUrl)) {
                return i;
            }
        }
        return -1;
    }

    public static String POSTER_TAG = "POSTER_TAG";

    public void setUpSlideView(Music music) {
        musicNameTxt.setText(music.getMusicName());

        Log.d(POSTER_TAG, "Pic : " + music.getMusicName() + " :: " +
                music.getAlbumPicture());

        Glide.with(this).asBitmap()
                .load(music.getAlbumPicture())
                .into(musicImg);
    }

    private void clickListeners() {

        final float[] offset = {0};

        mDragView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        mSubDragView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d(TAG, "slideOffset = " + slideOffset);
                offset[0] = slideOffset;
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                if (offset[0] == 1.0) {
                    musicPausePlay.setVisibility(View.INVISIBLE);
                } else {
                    musicPausePlay.setVisibility(View.VISIBLE);
                }
            }
        });


        final String SUB_SLIDE_PANEL = "SUB_SLIDE_PANEL";

        mSubLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                Log.d(SUB_SLIDE_PANEL, "" + slideOffset);

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {

                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                    mSubLayout.setVisibility(View.GONE);
            }
        });

        mArtistHeaderBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSubLayout != null &&
                        (mSubLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                                mSubLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
                    mSubLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    mSubLayout.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appSharedPrefrence.clearPref();
    }
}
