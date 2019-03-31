package com.example.musicplayer.musiclist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.database.Music;
import com.example.musicplayer.visualizer.BarVisualizer;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class MusicListAdapter extends RealmRecyclerViewAdapter<Music, MusicListAdapter.MusicListViewHolder> {


    String TAG = "MusicAdapter";

    Context mContext;
    MusicListFragment mMusicListFragment;
    OrderedRealmCollection<Music> musicList;

    public MusicListAdapter(@NonNull Context context, MusicListFragment musicListFragment,
                            @Nullable OrderedRealmCollection<Music> musicList) {
        super(context, musicList, true);
        setHasStableIds(true);

        this.mContext = context;
        this.mMusicListFragment = musicListFragment;
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_grid_layout,
                viewGroup, false);

        return new MusicListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListViewHolder holder, int i) {

        Music music = musicList.get(i);

        Log.d(TAG, i + " : " + music);
        Log.d(TAG, i + " : " + music.getArtistName());
        Log.d(TAG, i + " : " + music.getAlbumPicture());

        holder.mFolderItemTxt.setText(music.getArtistName());

        Glide.with(mContext).asBitmap().load(music.getAlbumPicture()).into(holder.mFolderItemThumbnail);

        setAnimation(holder.itemView, i);
    }

    private int mLastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > mLastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            mLastPosition = position;
        }
    }



    class MusicListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.folder_view_root)
        LinearLayout mFolderViewRoot;
        @BindView(R.id.folder_item_thumbnail)
        ImageView mFolderItemThumbnail;
        @BindView(R.id.folder_icon)
        ImageView mFolderIcon;
        @BindView(R.id.folder_item_label)
        TextView mFolderItemTxt;
        BarVisualizer mMusicVisualizer;
        AVLoadingIndicatorView mMusicLoading;

        int pos;

        public MusicListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mFolderItemTxt = (TextView) itemView.findViewById(R.id.folder_item_label);
            mFolderIcon = (ImageView) itemView.findViewById(R.id.folder_icon);
            mFolderItemThumbnail = (ImageView) itemView.findViewById(R.id.folder_item_thumbnail);
            mMusicVisualizer = (BarVisualizer) itemView.findViewById(R.id.music_visualizer);
            mMusicLoading = (AVLoadingIndicatorView) itemView.findViewById(R.id.music_loading);

            mFolderIcon.setOnClickListener(this);
            mFolderItemThumbnail.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            pos = getPosition();
            Music music = musicList.get(pos);


            switch (v.getId()) {
                case R.id.folder_icon:

                    mMusicListFragment.setMusicList(music.getArtistName());
                    mMusicListFragment.setArtistView(pos);
                    mMusicListFragment.showLoading(pos);

                    break;
                case R.id.folder_item_thumbnail:

                    mMusicListFragment.openArtistMusics(music);
                    break;
            }

        }

    }
}
