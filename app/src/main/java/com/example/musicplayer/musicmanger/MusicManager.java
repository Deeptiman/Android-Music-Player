package com.example.musicplayer.musicmanger;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.example.musicplayer.apppreference.AppSharedPrefrence;
import com.example.musicplayer.database.Music;
import com.example.musicplayer.musicplayer.PlayerFragment;
import com.example.musicplayer.musicmanger.utils.AppConstants;
import com.example.musicplayer.musicmanger.utils.AudioFocusHelper;
import com.example.musicplayer.visualizer.BarVisualizer;

import java.util.ArrayList;
import java.util.List;

public class MusicManager extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener {

    String TAG = PlayerFragment.DEBUG_TAG;

    private static boolean isRunning = false;
    private AppSharedPrefrence mAppSharedPrefrence;
    private int mTotalPlayerDuration;
    private int mPlayingPosition;
    private Intent musicIntent;

    private Runnable runnable;
    private Handler mHandler = new Handler();

    public static final float DUCK_VOLUME = 0.1f;

    public MediaPlayer mPlayer = null;
    public MusicHelper musicHelper;
    private List<Music> feeds = new ArrayList<Music>();

    private RequestDataBroadCast requestBroadCast;

    public enum State {
        Stopped, // media player is stopped and not prepared to play
        Preparing, // media player is preparing...
        Playing, // playback active (media player ready!). (but the media player
        Paused // playback paused (media player ready!)
    }

    public State mState = State.Stopped;

    // do we have audio focus?
    public enum AudioFocus {
        NoFocusNoDuck, // we don't have audio focus, and can't duck
        NoFocusCanDuck, // we don't have focus, but can play at a low volume
        // ("ducking")
        Focused // we have full audio focus
    }

    AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;
    AudioFocusHelper mAudioFocusHelper = null;

    /**
     * Makes sure the media player exists and has been reset. This will create
     * the media player if needed, or reset the existing media player if one
     * already exists.
     */
    public void createMediaPlayerIfNeeded() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnBufferingUpdateListener(this);

            mAppSharedPrefrence.setAudioSessionId(mPlayer.getAudioSessionId());

        } else {
            mPlayer.reset();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        musicHelper = MusicHelper.getInstance(this);
        mAppSharedPrefrence = AppSharedPrefrence.getsharedprefInstance(getApplicationContext());

        requestBroadCast = new RequestDataBroadCast();
        registerReceiver(requestBroadCast, new IntentFilter(AppConstants.MUSICSERVICEDATA));

        mAudioFocus = AudioFocus.Focused; // no focus feature, so we always

        isRunning = true;
    }

    /**
     * Called when we receive an Intent. When we receive an intent sent to us
     * via startService(), this is the method that gets called. So here we react
     * appropriately depending on the Intent's action, which specifies what is
     * being requested of us.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "test " + "onStartCommand");
        musicIntent = new Intent(AppConstants.MUSICBROADCAST);
        feeds = musicHelper.getTrackListMediaPlayer();
        mPlayingPosition = mAppSharedPrefrence.getPlaying_position();

        if (feeds != null && feeds.size() > 0) {
            playSong(feeds.get(mPlayingPosition).getMusicUrl());
        }

        return START_NOT_STICKY; // Means we started the service, but don't want
    }


    String TRACK_TAG = "TRACK_TAG";

    public void processPlayRequest() {

        Log.d(TRACK_TAG, "Service : processPlayRequest : " + mState);

        tryToGetAudioFocus();

        if (mState == State.Paused) {

            mState = State.Playing;
            mAppSharedPrefrence.setMediaplayerstate("play");
            configAndStartMediaPlayer();
        } else {
            // to change the song when already song is running
            mPlayingPosition = mAppSharedPrefrence.getPlaying_position();
            feeds = musicHelper.getTrackListMediaPlayer();

            Log.d(TRACK_TAG, "Service setTrackListMediaPlayer : " + feeds.size());
            for (int i = 0; i < feeds.size(); i++) {
                Log.d(TRACK_TAG, i + " : " + feeds.get(i).getMusicName());
            }

            playSong(feeds.get(mPlayingPosition).getMusicUrl());
        }
    }

    public void processPauseRequest() {

        Log.d(TAG, "processPauseRequest : ");
        mAppSharedPrefrence.setIsMusicPlaying(false);
        // Pause media player and cancel the 'foreground service' state.
        mState = State.Paused;
        if (mPlayer != null)
            mPlayer.pause();
        mAppSharedPrefrence.setMediaplayerstate("pause");
    }


    public static boolean isRunning() {
        return isRunning;
    }

    /**
     * Releases resources used by the service for playback. This includes the
     * "foreground service" status and notification, the wake locks and possibly
     * the MediaPlayer.
     *
     * @param releaseMediaPlayer Indicates whether the Media Player should also be released or
     *                           not
     */
    void relaxResources(boolean releaseMediaPlayer) {
        // stop being a foreground service
        stopForeground(true);
        if (mPlayer != null && mPlayer.isPlaying())
            mPlayer.stop();
        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * Reconfigures MediaPlayer according to audio focus settings and
     * starts/restarts it. This method starts/restarts the MediaPlayer
     * respecting the current audio focus state. So if we have focus, it will
     * play normally; if we don't have focus, it will either leave the
     * MediaPlayer paused or set it to a low volume, depending on what is
     * allowed by the current focus settings. This method assumes mPlayer !=
     * null, so if you are calling it, you have to do so from a context where
     * you are sure this is the case.
     */
    void configAndStartMediaPlayer() {

        Log.d(TAG, "configAndStartMediaPlayer");

        if (mPlayer == null)
            return;

        mAppSharedPrefrence.setIsMusicPlaying(true);
        if (mAudioFocus == AudioFocus.NoFocusNoDuck) {
            // If we don't have audio focus and can't duck, we have to pause,
            // even if mState
            // is State.Playing. But we stay in the Playing state so that we
            // know we have to resume
            // playback once we get the focus back.
            if (mPlayer.isPlaying()) {

                Log.d(TAG, "configAndStartMediaPlayer: mPlayer.pause()");
                mPlayer.pause();
                musicIntent.putExtra(AppConstants.MUSICSERVICE, AppConstants.SECONDARY_BUFFERING_PROGRESS);
                musicIntent.putExtra(AppConstants.MUSICSESSIONID, mPlayer.getAudioSessionId());
                sendBroadcast(musicIntent);
                mState = State.Playing;
            }
            return;
        } else if (mAudioFocus == AudioFocus.NoFocusCanDuck)
            mPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME); // we'll be relatively
            // quiet
        else
            mPlayer.setVolume(1.0f, 1.0f); // we can be loud


        if (!mPlayer.isPlaying()) {
            Log.d(TAG, "configAndStartMediaPlayer: mPlayer.start() : ");
            mPlayer.start();
            mState = State.Playing;
            musicIntent.putExtra(AppConstants.MUSICSERVICE, AppConstants.CONFIGURE_START_PLAYER);
            musicIntent.putExtra(AppConstants.MUSICSESSIONID, mPlayer.getAudioSessionId());
            sendBroadcast(musicIntent);
        }
    }


    private void stopMusic() {

        mState = State.Paused;
        //mAppSharedPrefrence.setMediaplayerstate("stop");
        relaxResources(false); // release everything except MediaPlayer
    }

    /**
     * Starts playing the next song. If manualUrl is null, the next song will be
     * randomly selected from our Media Retriever (that is, it will be a random
     * song in the user's device). If manualUrl is non-null, then it specifies
     * the URL or path to the song that will be played next.
     */
    public void playSong(String manualUrl) {

        Log.d(TRACK_TAG, "Service : getPlaying_position() = " + mAppSharedPrefrence.getPlaying_position());
        Log.d(TRACK_TAG, "Service : mPlayer : " + mPlayer);

        mState = State.Paused;

        mAppSharedPrefrence.setMediaplayerstate("play");
        relaxResources(false); // release everything except MediaPlayer

        try {
            if (manualUrl != null) {
                // set the source of the media player to a manual URL or path
                createMediaPlayerIfNeeded();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(manualUrl);

                mAppSharedPrefrence.setPrevSong(mAppSharedPrefrence.getCurrentSong());
                mAppSharedPrefrence.setCurrentSong(manualUrl);
            }

            mState = State.Playing;

            try {
                mPlayer.prepareAsync();
            } catch (NullPointerException e) {

                Log.d(TAG, "NullPointerException : " + e.toString());

                e.printStackTrace();
                mPlayer.reset();
                mPlayer.release();
                mPlayer = null;
                mPlayer.setDataSource(manualUrl);
            }

            tryToGetAudioFocus();

        } catch (Exception ex) {
            Log.e(TAG, "Service " + "IOException playing next song: " + ex.getMessage() + " :: " + mPlayer);
            ex.printStackTrace();
        }
    }


    void tryToGetAudioFocus() {
        if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.requestFocus())
            mAudioFocus = AudioFocus.Focused;
    }


    private void primarySeekBarProgressUpdater() {
        // SharedPreferences.Editor editor = preferences.edit();
        if (mPlayer != null)
            mAppSharedPrefrence.setMediaPlayerCurrentPosition(mPlayer.getCurrentPosition());
        mAppSharedPrefrence.setMediaPlayerTotalDuration(mTotalPlayerDuration);

        runnable = new Thread() {
            public void run() {
                primarySeekBarProgressUpdater();
            }
        };
        mHandler.postDelayed(runnable, 1000);
    }

    private void setMediaPlayerRunningPosition() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            Log.d(BarVisualizer.TAG, "setMediaPlayerRunningPosition");

            primarySeekBarProgressUpdater();
            musicIntent.putExtra(AppConstants.MUSICSERVICE, AppConstants.MEDIA_PLAYER_RUNNING);
            musicIntent.putExtra(AppConstants.MUSICSESSIONID, mPlayer.getAudioSessionId());
            sendBroadcast(musicIntent);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

        if (!mAppSharedPrefrence.getMediaplayerstate().equalsIgnoreCase("stop")) {
            musicIntent.putExtra(AppConstants.MUSICSERVICE, AppConstants.BUFFERING_PLAYER);
            sendBroadcast(musicIntent);
            mAppSharedPrefrence.setBufferingpercentage(percent);

        }
    }

    /**
     * Called when media player is done playing current song.
     */

    @Override
    public void onCompletion(MediaPlayer mp) {

        Log.d(TAG, "onCompletion ");

        try {
            processPauseRequest();
        } catch (NullPointerException e) {
        } catch (IllegalStateException e) {
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        Log.d(TAG, "onPrepared : ");

        // The media player is done preparing. That means we can start playing!
        mState = State.Playing;
        configAndStartMediaPlayer();
        mTotalPlayerDuration = mp.getDuration();

        setMediaPlayerRunningPosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Service is being killed, so make sure we release our resources
        mState = State.Stopped;
        relaxResources(true);
        Log.e(TAG, "onDestory " + "onDestory of step service");
        mHandler.removeCallbacks(runnable);

        isRunning = false;
    }

    private class RequestDataBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int position = intent.getIntExtra(AppConstants.MUSICACTIVITY, 0);

            Log.d(TAG, "RequestDataBroadCast : position = " + position);

            switch (position) {
                case AppConstants.PLAY_MUSIC:

                    Log.d(TAG, "RequestDataBroadCast : position = 12  = processPlayRequest");

                    processPlayRequest();
                    break;
                case AppConstants.PAUSE_MUSIC:

                    Log.d(TAG, "RequestDataBroadCast : position = 13  = processPauseRequest");

                    processPauseRequest();
                    break;

                case AppConstants.PLAYER_SEEK_PROGRESS:

                    Log.d(TAG, "RequestDataBroadCast : position = 15");

                    mPlayer.seekTo(intent.getIntExtra("mediaPlayersCurrentPosition", 0));
                    break;
                case AppConstants.STOP_MUSIC:
                    stopMusic();
                    break;
                default:
                    break;
            }
        }
    }
}
