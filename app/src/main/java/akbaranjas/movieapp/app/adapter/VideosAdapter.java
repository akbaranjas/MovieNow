package akbaranjas.movieapp.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;

import akbaranjas.movieapp.R;
import akbaranjas.movieapp.app.listener.OnPlayClickListener;
import akbaranjas.movieapp.app.pojo.Result;
import akbaranjas.movieapp.app.pojo.video.ResultVideo;
import akbaranjas.movieapp.app.url.MovieURL;

/**
 * Created by akbaranjas on 29/11/16.
 */

public class VideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int CURSOR_TITLE_VIDEO = 2;
    public static final int CURSOR_KEY_VIDEOS = 1;
    Cursor cursor;
    Context context;
    private boolean mWithHeader;
    RecyclerView.ViewHolder mholder;
    OnPlayClickListener onPlayClickListener;


    public VideosAdapter(Context context ,Cursor cursor, boolean withHeader, OnPlayClickListener onPlayClick) {
        this.cursor = cursor;
        this.context = context;
        this.mWithHeader = withHeader;
        this.onPlayClickListener = onPlayClick;
    }

    public void updateList(Cursor c){
        this.cursor = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_item_videos, parent, false);
            mholder = new HeaderViewHolder(v);
            return mholder;
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videos, parent, false);
            mholder = new ItemViewHolder(v);
            return mholder;
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return this.mWithHeader && position == 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == TYPE_ITEM) {
            final ItemViewHolder mholder = (ItemViewHolder) holder;
            if(mWithHeader){
                cursor.moveToPosition(position - 1);
                mholder.tv_title_videos.setText(cursor.getString(CURSOR_TITLE_VIDEO));

//                final YouTubeThumbnailLoader.OnThumbnailLoadedListener  onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
//                    @Override
//                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
//
//                    }
//
//                    @Override
//                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
//                        youTubeThumbnailView.setVisibility(View.VISIBLE);
//                        mholder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
//                    }
//                };

//                    mholder.youTubeThumbnailView.initialize(MovieURL.API_KEY_GOOGLE, new YouTubeThumbnailView.OnInitializedListener() {
//                    @Override
//                    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
//
//                        youTubeThumbnailLoader.setVideo(videos.get(position - 1).getKey());
//                        youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
//                        youTubeThumbnailLoader.release();
//                    }
//
//                    @Override
//                    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
//                        //write something for failure
//                    }
//                });
                final YouTubeThumbnailView youTubeThumbnailView = mholder.youTubeThumbnailView;
                youTubeThumbnailView.initialize(MovieURL.API_KEY_GOOGLE, new YouTubeThumbnailView.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                        youTubeThumbnailLoader.setVideo(cursor.getString(CURSOR_KEY_VIDEOS));
                        youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                            @Override
                            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                                youTubeThumbnailView.setVisibility(View.VISIBLE);
                                mholder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
                                youTubeThumbnailLoader.release();
                            }

                            @Override
                            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                            }
                        });
                    }

                    @Override
                    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if(cursor != null) {
            if(mWithHeader){
                return cursor.getCount() + 1;
            }else{
                return cursor.getCount();
            }
        }else {
            return 0;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview_header_videos);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_title_videos;
        protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        YouTubeThumbnailView youTubeThumbnailView;
        protected ImageView playButton;

        ItemViewHolder(View itemView) {
            super(itemView);
            tv_title_videos = (TextView) itemView.findViewById(R.id.tv_title_item_video);
            playButton=(ImageView)itemView.findViewById(R.id.btnYoutube_player);
            playButton.setOnClickListener(this);
            relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
        }

        @Override
        public void onClick(View view) {
            int positition = getAdapterPosition();
            cursor.moveToPosition(positition - 1);
            onPlayClickListener.onYoutubeClick(cursor.getString(CURSOR_KEY_VIDEOS));
        }
    }
}
