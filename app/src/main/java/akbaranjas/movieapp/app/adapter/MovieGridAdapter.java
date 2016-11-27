package akbaranjas.movieapp.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import akbaranjas.movieapp.R;
import akbaranjas.movieapp.app.listener.OnMovieClickListener;
import akbaranjas.movieapp.app.pojo.MovieList;
import akbaranjas.movieapp.app.pojo.Result;
import akbaranjas.movieapp.app.url.MovieURL;

/**
 * Created by akbaranjas on 22/11/16.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieViewHolder>  {

    public static final int TYPE_LOADING = 1;
    public static final int TYPE_ITEM = 0;
    private List<Result> movies;
    private int layout;
    private Context context;
    OnMovieClickListener onMovieClickListener;

    public MovieGridAdapter(List<Result> movies, int layout, Context context, OnMovieClickListener listener) {
        this.movies = movies;
        this.layout = layout;
        this.context = context;
        this.onMovieClickListener = listener;
    }


    @Override
    public MovieGridAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        view = layoutInflater.inflate(R.layout.grid_item_movie, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieGridAdapter.MovieViewHolder holder, int position) {
        //set poster
        Picasso.with(context).load(MovieURL.BASE_URL_IMG + MovieURL.SIZE_IMAGE[1] + "/"
                 + movies.get(position).getPosterPath()).into(holder.img_item_grid_movie);
        holder.tv_movie_title_grid.setText((movies.get(position).getTitle().length() > 20 ?
                movies.get(position).getTitle().substring(0,15) + "..." : movies.get(position).getTitle()));
        holder.tv_rating_movie_grid.setText(movies.get(position).getVoteAverage().toString());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        ImageView img_item_grid_movie;
        TextView tv_movie_title_grid;
        TextView tv_rating_movie_grid;

        public MovieViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            img_item_grid_movie = (ImageView) itemView.findViewById(R.id.img_item_grid_movie);
            tv_movie_title_grid = (TextView) itemView.findViewById(R.id.tv_movie_title_grid);
            tv_rating_movie_grid = (TextView) itemView.findViewById(R.id.tv_rating_movie_grid);
        }


        @Override
        public void onClick(View view) {
            int positition = getAdapterPosition();
            onMovieClickListener.onMovieClick(movies.get(positition).getId());
        }
    }
}
