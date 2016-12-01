package akbaranjas.movieapp.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.StringDef;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;

import akbaranjas.movieapp.R;
import akbaranjas.movieapp.app.listener.OnMovieClickListener;
import akbaranjas.movieapp.app.listener.OnPlayClickListener;
import akbaranjas.movieapp.app.url.MovieURL;

/**
 * Created by akbaranjas on 29/11/16.
 */

public class FavAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Cursor cursor;
    Context context;
    RecyclerView.ViewHolder mholder;
    OnMovieClickListener onMovieClickListener;

    public FavAdapter(Context context ,Cursor cursor, OnMovieClickListener onMovieClickListener) {
        this.cursor = cursor;
        this.context = context;
        this.onMovieClickListener = onMovieClickListener;
    }

    public void updateList(Cursor c){
        this.cursor = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item_fav, parent, false);
            mholder = new ItemViewHolder(v);
            return mholder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            final ItemViewHolder mholder = (ItemViewHolder) holder;
            cursor.moveToPosition(position);
            Picasso.with(context).load(MovieURL.BASE_URL_IMG + MovieURL.SIZE_IMAGE[3] + "/"
                + cursor.getString(2)).into(mholder.imgview);
            mholder.tv_title.setText(cursor.getString(1));
            mholder.rating.setText(String.valueOf(cursor.getDouble(3)) + "/10");

    }

    @Override
    public int getItemCount() {
        if(cursor != null) {
            return cursor.getCount();
        }else {
            return 0;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_title;
        ImageView imgview;
        TextView rating;

        ItemViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title_item_fav);
            imgview=(ImageView)itemView.findViewById(R.id.image_item_fav);
            rating = (TextView) itemView.findViewById(R.id.tv_rating_fav);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int positition = getAdapterPosition();
            cursor.moveToPosition(positition);
            onMovieClickListener.onMovieClick(cursor.getInt(0));
        }
    }
}
