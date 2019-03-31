package com.example.musicplayer.musiclist;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.apppreference.AppSharedPrefrence;
import com.example.musicplayer.database.Music;
import com.example.musicplayer.database.RealmManager;
import com.example.musicplayer.helper.Utils;
import com.example.musicplayer.visualizer.BarVisualizer;
import com.wang.avi.AVLoadingIndicatorView;

public class MusicListFragment extends Fragment {

    private int mArtistPos;
    private MusicListAdapter musicListAdapter;
    private AppSharedPrefrence mAppSharedPrefrence;

    private BarVisualizer mBarVisualizer;
    private RecyclerView mMusicGridView;
    private MainActivity mainActivity;

    String TAG = "ARTIST_STATE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_music_list, container, false);

        mMusicGridView = (RecyclerView) view.findViewById(R.id.music_grid_view);

        mainActivity = (MainActivity) getActivity();
        mAppSharedPrefrence = AppSharedPrefrence.getsharedprefInstance(getActivity());

        setArtistAdapter();

        return view;
    }

    private void setArtistAdapter() {

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        mMusicGridView.setLayoutManager(manager);
        mMusicGridView.addItemDecoration(new GridSpacingItemDecoration(2, Utils.dpToPx(10), true));
        mMusicGridView.setItemAnimator(new DefaultItemAnimator());
        mMusicGridView.getItemAnimator().setChangeDuration(0);

        musicListAdapter = new MusicListAdapter(getActivity(), this,
                RealmManager.getDistinctModelList(Music.class, "artistName"));
        mMusicGridView.setAdapter(musicListAdapter);

    }

    public void setMusicList(String artistName) {
        mainActivity.setMusicList(artistName);
    }

    public void openArtistMusics(Music music) {
        mainActivity.openArtistMusics(music);
    }

    public void playArtistPlayer() {


        updateView(getItemId(mAppSharedPrefrence.getCurrentSong()), false, true, true);
    }

    public void showVisualizer() {
        if (mBarVisualizer != null)
            mBarVisualizer.invalidate();
    }

    public void setArtistView(int artistPos) {

        Log.d(TAG, "ARTIST_POS = " + mArtistPos + " :: " + artistPos);

        if (mArtistPos != artistPos) {
            updateView(mArtistPos, false, false, false);
        }
        mArtistPos = artistPos;
    }

    public void showLoading(int pos) {
        updateView(pos, true, true, false);
    }

    private void updateView(int pos, boolean loading, boolean playing, boolean visualizer) {

        View view = mMusicGridView.getChildAt(pos);

        if (view == null)
            return;

        ImageView playIcon = (ImageView) view.findViewById(R.id.folder_icon);
        mBarVisualizer = (BarVisualizer) view.findViewById(R.id.music_visualizer);
        AVLoadingIndicatorView musicLoading = (AVLoadingIndicatorView) view.findViewById(R.id.music_loading);

        musicLoading.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);

        playIcon.setVisibility(playing ? View.INVISIBLE : View.VISIBLE);
        playIcon.setEnabled(!playing);
        mBarVisualizer.setVisibility(visualizer ? View.VISIBLE : View.INVISIBLE);
        mBarVisualizer.setColor(Color.parseColor("#FFFFFF"));
        mBarVisualizer.setDensity(BarVisualizer.density);
    }


    public int getItemId(String musicUrl) {

        for (int i = 0; i < musicListAdapter.getItemCount(); i++) {
            Music music = musicListAdapter.getItem(i);
            if (music == null || music.getMusicUrl() == null)
                return -1;
            if (music.getMusicUrl().equals(musicUrl)) {
                return i;
            }
        }
        return 0;
    }
}
