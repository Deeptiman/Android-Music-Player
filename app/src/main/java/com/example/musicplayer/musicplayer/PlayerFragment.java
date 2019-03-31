package com.example.musicplayer.musicplayer;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.apppreference.AppSharedPrefrence;
import com.example.musicplayer.database.Music;
import com.example.musicplayer.helper.CheckConnection;
import com.example.musicplayer.helper.Utils;
import com.example.musicplayer.musicmanger.MusicHelper;
import com.example.musicplayer.musicmanger.MusicManager;
import com.example.musicplayer.musicmanger.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.example.musicplayer.musicmanger.utils.AppConstants.MUSICACTIVITY;
import static com.example.musicplayer.musicmanger.utils.AppConstants.MUSICSERVICE;
import static com.example.musicplayer.musicmanger.utils.AppConstants.MUSICSERVICEDATA;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {


    public static String DEBUG_TAG = "PLAYER_TAG";

    private ImageView mPoster;
    private Button mPlayPauseBtn, mPreviousBtn, mNextBtn;
    private TextView mRunTimeTxt, mTotalTimeTxt;


    public Handler mHandler = new Handler();
    public Runnable runnable;
    private SeekBar mTrackProgressBar;

    private MusicHelper musicHelper;

    private List<Music> feeds = new ArrayList<Music>();

    private int mediaFileLengthInMilliseconds;
    private int mediaPlayerCurrentPosition = 0;


    private Utils util;
    private int selectedPositionList;

    private String statesMediaPlayer = "";
    private AppSharedPrefrence appSharedPrefrence;
    private RefreshDataBroadCast refreshBroadCast;

    private Intent intentForService;
    private int position;

    MainActivity mainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_player_layout, container, false);

        initView(view);

        intentForService = new Intent(MUSICSERVICEDATA);

        appSharedPrefrence = AppSharedPrefrence.getsharedprefInstance(getActivity());
        util = new Utils();
        refreshBroadCast = new RefreshDataBroadCast();
        musicHelper = MusicHelper.getInstance(getActivity());


        getActivity().registerReceiver(refreshBroadCast, new IntentFilter(AppConstants.MUSICBROADCAST));

        if (getActivity() instanceof MainActivity)
            mainActivity = (MainActivity) getActivity();


        if (!isMyServiceRunning()) {
            getActivity().startService(new Intent(getActivity(), MusicManager.class));
        } else {
            getActivity().stopService(new Intent(getActivity(), MusicManager.class));
        }

        mTrackProgressBar.setOnSeekBarChangeListener(this);

        mPlayPauseBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mPreviousBtn.setOnClickListener(this);
        feeds = musicHelper.getTrackListMediaPlayer();
        statesMediaPlayer = appSharedPrefrence.getMediaplayerstate();

        Log.d(DEBUG_TAG, "statesMediaPlayer : " + statesMediaPlayer);

        checkTrackProgressState();

        pausePlayState();

        return view;
    }

    private void pausePlayState() {
        statesMediaPlayer = appSharedPrefrence.getMediaplayerstate();
        if (statesMediaPlayer.equalsIgnoreCase("play")) {
            mPlayPauseBtn.setBackgroundResource(R.drawable.pause_music);
            mPlayPauseBtn.setTag(R.drawable.pause_music);
        } else {
            mPlayPauseBtn.setBackgroundResource(R.drawable.play_music);
            mPlayPauseBtn.setTag(R.drawable.play_music);
        }
    }

    private void checkTrackProgressState() {

        if (feeds != null && feeds.size() > 0) {
            if (statesMediaPlayer.equalsIgnoreCase("play")
                    || statesMediaPlayer.equalsIgnoreCase("pause")) {
                mTrackProgressBar.setEnabled(true);
            } else {
                mTrackProgressBar.setEnabled(false);
            }
        } else {
            mTrackProgressBar.setEnabled(false);
        }
    }


    private void initView(View view) {
        mPoster = (ImageView) view.findViewById(R.id.music_img_poster);
        mPlayPauseBtn = (Button) view.findViewById(R.id.btnPlayPause);
        mNextBtn = (Button) view.findViewById(R.id.btnNext);
        mPreviousBtn = (Button) view.findViewById(R.id.btnPrevious);
        mTrackProgressBar = (SeekBar) view.findViewById(R.id.SeekBarTimer);
        mRunTimeTxt = (TextView) view.findViewById(R.id.start_time);
        mTotalTimeTxt = (TextView) view.findViewById(R.id.end_time);
    }


    /**
     * Method which updates the SeekBar primary progress by current song playing
     * position
     */
    private void primarySeekBarProgressUpdater() {

        //Log.d(DEBUG_TAG, "primarySeekBarProgressUpdater");

        runnable = new Thread() {
            public void run() {
                updateSeekBar();
            }
        };
        mHandler.postDelayed(runnable, 0);
    }


    private void updateSeekBar() {
        statesMediaPlayer = appSharedPrefrence.getMediaplayerstate();

        //Log.d(DEBUG_TAG, "updateSeekBar : " + statesMediaPlayer);

        if (statesMediaPlayer.equalsIgnoreCase("play")
                || statesMediaPlayer.equalsIgnoreCase("pause"))
            if (!mTrackProgressBar.isEnabled())
                mTrackProgressBar.setEnabled(true);
        mediaPlayerCurrentPosition = appSharedPrefrence
                .getMediaPlayerCurrentPosition();
        mTrackProgressBar
                .setProgress((int) (((float) (mediaPlayerCurrentPosition) / mediaFileLengthInMilliseconds) * 100));

        String runTime = "" + util.milliSecondsToTimer(mediaPlayerCurrentPosition);
        String totalTime = "-" + util.milliSecondsToTimer(mediaFileLengthInMilliseconds - mediaPlayerCurrentPosition);

        mRunTimeTxt.setText(runTime);
        mTotalTimeTxt.setText(totalTime);

        //Log.d(DEBUG_TAG, runTime + " : " + totalTime);

        if (("-" + util.milliSecondsToTimer(mediaFileLengthInMilliseconds
                - mediaPlayerCurrentPosition)).equalsIgnoreCase("-0:00")) {
            mTrackProgressBar.setProgress(100);
        }

        mHandler.postDelayed(runnable, 1000);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnPrevious:

                selectedPositionList = appSharedPrefrence.getPlaying_position();

                if (feeds != null && feeds.size() > 0) {
                    if (CheckConnection.isConnection(getActivity())) {
                        mRunTimeTxt.setText("0:00");
                        mediaPlayerCurrentPosition = 0;

                        if (selectedPositionList == 0) {
                            selectedPositionList = feeds.size() - 1;
                        } else {
                            selectedPositionList--;
                        }

                        mainActivity.updateView(appSharedPrefrence.getPlaying_position(), false);
                        appSharedPrefrence.setPlaying_position(selectedPositionList);
                        mainActivity.updateView(appSharedPrefrence.getPlaying_position(), true);

                        mainActivity.setUpSlideView(feeds.get(appSharedPrefrence.getPlaying_position()));
                        showPoster();
                        preparePlaySong();

                    } else {
                        musicHelper.showToast(getResources().getString(R.string.text_nointernetconnction));
                    }
                } else {
                    musicHelper.showToast(getString(R.string.no_trcks_available));
                }

                break;
            case R.id.btnNext:

                selectedPositionList = appSharedPrefrence.getPlaying_position();

                if (feeds != null && feeds.size() > 0) {
                    if (CheckConnection.isConnection(getActivity())) {
                        mRunTimeTxt.setText("0:00");
                        mediaPlayerCurrentPosition = 0;

                        if (selectedPositionList == feeds.size() - 1) {
                            selectedPositionList = 0;
                        } else {
                            selectedPositionList++;
                        }

                        mainActivity.updateView(appSharedPrefrence.getPlaying_position(), false);
                        appSharedPrefrence.setPlaying_position(selectedPositionList);
                        mainActivity.updateView(appSharedPrefrence.getPlaying_position(), true);

                        mainActivity.setUpSlideView(feeds.get(appSharedPrefrence.getPlaying_position()));
                        showPoster();
                        preparePlaySong();

                    } else {
                        musicHelper.showToast(getResources().getString(R.string.text_nointernetconnction));
                    }
                } else {
                    musicHelper.showToast("no track available");
                }
                break;
            case R.id.btnPlayPause:
                pausePlay();
                break;
        }
    }

    private void pausePlay() {

        Log.d(DEBUG_TAG, "btnPlayPause");

        /**
         * ImageButton onClick event handler. Method which start/pause
         * mediaplayer playing
         */
        if (mPlayPauseBtn.getTag() == null) {
            statesMediaPlayer = appSharedPrefrence.getMediaplayerstate();
            if (statesMediaPlayer.equalsIgnoreCase("play")) {
                mPlayPauseBtn.setTag(R.drawable.pause_music);
            } else {
                mPlayPauseBtn.setTag(R.drawable.play_music);
            }
        }
        if (mPlayPauseBtn.getTag().equals(R.drawable.play_music)) {
            Log.d(DEBUG_TAG, "playing_music");
            mPlayPauseBtn.setBackgroundResource(R.drawable.pause_music);
            mPlayPauseBtn.setTag(R.drawable.pause_music);
            mainActivity.pausePlayState(true);
        } else {
            Log.d(DEBUG_TAG, "pause_music");
            mPlayPauseBtn.setBackgroundResource(R.drawable.play_music);
            mPlayPauseBtn.setTag(R.drawable.play_music);
            mainActivity.pausePlayState(false);
        }

        Log.d(DEBUG_TAG, "feeds = " + feeds);

        if (feeds != null && feeds.size() > 0) {
            statesMediaPlayer = appSharedPrefrence.getMediaplayerstate();

            if (CheckConnection.isConnection(getActivity())) {

                Log.d(DEBUG_TAG, "pausePlay == statesMediaPlayer : " + statesMediaPlayer);

                if (statesMediaPlayer.equalsIgnoreCase("")) {

                    Log.d(DEBUG_TAG, "INITIAL PLAY MUSIC");
                    intentForService.putExtra(MUSICACTIVITY, AppConstants.PLAY_MUSIC);
                    getActivity().sendBroadcast(intentForService);
                    statesMediaPlayer = "play";

                } else if (statesMediaPlayer.equalsIgnoreCase("play")) {

                    Log.d(DEBUG_TAG, "PAUSE MUSIC");
                    intentForService.putExtra(MUSICACTIVITY, AppConstants.PAUSE_MUSIC);
                    getActivity().sendBroadcast(intentForService);
                    statesMediaPlayer = "pause";

                } else {

                    Log.d(DEBUG_TAG, "PLAY MUSIC");
                    intentForService.putExtra(MUSICACTIVITY, AppConstants.PLAY_MUSIC);
                    getActivity().sendBroadcast(intentForService);
                    statesMediaPlayer = "play";
                }

            } else {
                musicHelper.showToast(getResources().getString(
                        R.string.text_nointernetconnction));
            }
        } else {
            musicHelper.showToast("no track available");
            mPlayPauseBtn.setBackgroundResource(R.drawable.play_music);
            mPlayPauseBtn.setTag(R.drawable.play_music);
        }
    }

    public void pausePlayMusic() {

        Log.d(DEBUG_TAG, "pausePlayMusic");

        pausePlay();
    }

    String TRACK_TAG = "TRACK_TAG";

    public void setTrackListMediaPlayer(ArrayList<Music> musicArrayList) {

        musicHelper.setTrackListMediaPlayer(musicArrayList);
        feeds = musicHelper.getTrackListMediaPlayer();

        Log.d(MainActivity.POSTER_TAG, "" + feeds.get(appSharedPrefrence.getPlaying_position()).getAlbumPicture());

        showPoster();

        playSong();
    }

    private void showPoster() {

        Glide.with(this).asBitmap()
                .load(feeds.get(appSharedPrefrence.getPlaying_position()).getAlbumPicture())
                .into(mPoster);
    }

    public void preparePlaySong() {

        /**
         * ImageButton onClick event handler. Method which start/pause
         * mediaplayer playing
         */
        if (feeds != null && feeds.size() > 0) {

            stopMusic();

            runnable = new Thread() {
                public void run() {

                    playSong();
                }
            };
            mHandler.postDelayed(runnable, 1000);

        } else {
            musicHelper.showToast(getString(R.string.no_trcks_available));
        }
    }

    private void stopMusic() {

        mRunTimeTxt.setText("0:00");
        mTotalTimeTxt.setText("0:00");
        mTrackProgressBar.setProgress(0);
        mediaPlayerCurrentPosition = 0;
        mPlayPauseBtn.setBackgroundResource(R.drawable.play_music);
        mPlayPauseBtn.setTag(R.drawable.play_music);

    }

    private void playSong() {

        if (MusicManager.isRunning()) {

            Log.d(TRACK_TAG, "isRunning : Play");

            intentForService.putExtra(MUSICACTIVITY, AppConstants.PLAY_MUSIC);
            getActivity().sendBroadcast(intentForService);
            statesMediaPlayer = "play";
            appSharedPrefrence.setMediaplayerstate(statesMediaPlayer);
            mPlayPauseBtn.setBackgroundResource(R.drawable.pause_music);
            mPlayPauseBtn.setTag(R.drawable.pause_music);
        } else {

            Log.d(TRACK_TAG, "Pause");

            intentForService.putExtra(MUSICACTIVITY, AppConstants.PAUSE_MUSIC);
            getActivity().sendBroadcast(intentForService);
            statesMediaPlayer = "pause";
            appSharedPrefrence.setMediaplayerstate(statesMediaPlayer);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        statesMediaPlayer = appSharedPrefrence.getMediaplayerstate();
        if (feeds != null && feeds.size() > 0) {
            if (fromUser) {
                if (statesMediaPlayer.equalsIgnoreCase("play")) {
                    Log.e(DEBUG_TAG, "runTime "
                            + util.milliSecondsToTimer((mediaFileLengthInMilliseconds / 100)
                            * progress));
                    Log.e(DEBUG_TAG, "totalTime "
                            + util.milliSecondsToTimer(mediaFileLengthInMilliseconds
                            - (mediaFileLengthInMilliseconds / 100)
                            * progress));

                    mRunTimeTxt.setText(""
                            + util.milliSecondsToTimer((mediaFileLengthInMilliseconds / 100)
                            * progress));
                    mTotalTimeTxt
                            .setText("-"
                                    + util.milliSecondsToTimer(mediaFileLengthInMilliseconds
                                    - (mediaFileLengthInMilliseconds / 100)
                                    * progress));
                }
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        statesMediaPlayer = appSharedPrefrence.getMediaplayerstate();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (feeds != null && feeds.size() > 0) {
            statesMediaPlayer = appSharedPrefrence.getMediaplayerstate();
            if (statesMediaPlayer.equalsIgnoreCase("play")) {
                mHandler.removeCallbacks(runnable);
                mediaPlayerCurrentPosition = (mediaFileLengthInMilliseconds / 100)
                        * seekBar.getProgress();
                appSharedPrefrence.setMediaPlayerCurrentPosition(mediaPlayerCurrentPosition);
                try {
                    intentForService.putExtra(MUSICACTIVITY, AppConstants.PLAYER_SEEK_PROGRESS);
                    intentForService.putExtra("mediaPlayersCurrentPosition", mediaPlayerCurrentPosition);
                    getActivity().sendBroadcast(intentForService);
                    primarySeekBarProgressUpdater();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {

        try {

            getActivity().unregisterReceiver(refreshBroadCast);
        } catch (IllegalArgumentException e) {
        }
        super.onDestroy();
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.musicplayer.musicmanger.MusicManager".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private class RefreshDataBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            position = intent.getIntExtra(MUSICSERVICE, 0);

            Log.d(DEBUG_TAG, " RefreshDataBroadCast =  " + position);

            if (position == AppConstants.MEDIA_PLAYER_RUNNING) {
                mainActivity.playArtistPlayer();
            }

            switch (position) {

                case AppConstants.CONFIGURE_START_PLAYER:

                    Log.d(DEBUG_TAG, "CONFIGURE_START_PLAYER");

                    primarySeekBarProgressUpdater();
                    mPlayPauseBtn.setBackgroundResource(R.drawable.pause_music);
                    mPlayPauseBtn.setTag(R.drawable.pause_music);
                    statesMediaPlayer = appSharedPrefrence.getMediaplayerstate();
                    if (statesMediaPlayer.equalsIgnoreCase("play")
                            || statesMediaPlayer.equalsIgnoreCase("pause"))
                        if (!mTrackProgressBar.isEnabled())
                            mTrackProgressBar.setEnabled(true);
                    break;
                case AppConstants.BUFFERING_PLAYER:

                    Log.d(DEBUG_TAG, "SECONDARY_BUFFERING_PROGRESS");

                    mTrackProgressBar.setSecondaryProgress(appSharedPrefrence.getBufferingpercentage());
                    break;

                case AppConstants.MEDIA_PLAYER_RUNNING:

                    Log.d(DEBUG_TAG, "MEDIA_PLAYER_RUNNING");

                    mediaFileLengthInMilliseconds = appSharedPrefrence.getMediaPlayerTotalDuration();
                    mTrackProgressBar.setSecondaryProgress(appSharedPrefrence.getBufferingpercentage());
                    mHandler.removeCallbacks(runnable);
                    primarySeekBarProgressUpdater();
                    break;

                case AppConstants.SECONDARY_BUFFERING_PROGRESS:

                    Log.d(DEBUG_TAG, "SECONDARY_BUFFERING_PROGRESS");

                    mTrackProgressBar.setSecondaryProgress(appSharedPrefrence.getBufferingpercentage());
                    statesMediaPlayer = "stop";
                    appSharedPrefrence.setMediaplayerstate(statesMediaPlayer);
                    break;
            }

        }
    }

}
