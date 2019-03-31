package com.example.musicplayer.visualizer;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.util.Log;

public class VisualizeManager {

    String TAG = "VISUALIZE_TAG";

    private Context mContext;
    private int mAudioSessionId;

    private Visualizer mVisualizer;

    public VisualizeManager(Context context, int audioSessionId) {
        this.mContext = context;
        this.mAudioSessionId = audioSessionId;
        init();
    }

    private void init() {
        Log.d(TAG,"mAudioSessionId = "+mAudioSessionId);
        mVisualizer = new Visualizer(mAudioSessionId);
        mVisualizer.setEnabled(false);
    }

    public void execute(final VisualizeCallback visualizeCallback) {

        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                visualizeCallback.onReceiveByteData(waveform);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);

        mVisualizer.setEnabled(true);
    }


    public interface VisualizeCallback {
        public void onReceiveByteData(byte[] bytes);
    }


}
