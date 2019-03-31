package com.example.musicplayer.artist;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.apppreference.AppSharedPrefrence;
import com.example.musicplayer.database.Music;
import java.util.ArrayList;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ArtistListAdapter extends RealmRecyclerViewAdapter<Music, ArtistListAdapter.ArtistListViewHolder> {


    String TAG = "ArtistMusicAdapter";

    AppSharedPrefrence mAppSharedPrefrence;

    Context mContext;
    OrderedRealmCollection<Music> musicList;

    public ArtistListAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Music> musicList, boolean autoUpdate) {
        super(context, musicList, autoUpdate);

        this.mContext = context;
        this.musicList = musicList;
        this.mAppSharedPrefrence = AppSharedPrefrence.getsharedprefInstance(mContext);

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ArtistListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.artist_list_adapter,
                viewGroup, false);

        return new ArtistListAdapter.ArtistListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistListViewHolder artistListViewHolder, int i) {

        Music music = musicList.get(i);

        artistListViewHolder.mMusicNameTxt.setText(music.getMusicName());
        Glide.with(mContext).asBitmap().load(music.getAlbumPicture()).into(artistListViewHolder.mMusicImg);

        Log.d(TAG,""+music.getArtistName());

    }

    class ArtistListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mMusicImg;
        TextView mMusicNameTxt;

        public ArtistListViewHolder(@NonNull View itemView) {
            super(itemView);

            mMusicImg = (ImageView) itemView.findViewById(R.id.artist_adapter_music_img_view);
            mMusicNameTxt = (TextView) itemView.findViewById(R.id.artist_adapter_music_name_txt);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(context instanceof MainActivity){

                mAppSharedPrefrence.setPrevSong(mAppSharedPrefrence.getCurrentSong());
                mAppSharedPrefrence.setCurrentSong(musicList.get(getPosition()).getMusicUrl());

                ((MainActivity) context).setPlayingPos(getPosition());
                ((MainActivity) context).setMusicList(musicList.get(getPosition()).getArtistName());
            }

        }
    }

}
