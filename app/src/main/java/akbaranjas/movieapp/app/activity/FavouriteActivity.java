package akbaranjas.movieapp.app.activity;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import akbaranjas.movieapp.R;
import akbaranjas.movieapp.app.adapter.FavAdapter;
import akbaranjas.movieapp.app.adapter.VideosAdapter;
import akbaranjas.movieapp.app.data.MovieDBHelper;
import akbaranjas.movieapp.app.listener.OnMovieClickListener;
import akbaranjas.movieapp.app.url.MovieURL;

/**
 * Created by Administrator on 12/1/2016.
 */
public class FavouriteActivity extends AppCompatActivity
        implements OnMovieClickListener , LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = FavouriteActivity.class.getSimpleName();
    private static RecyclerView rv_fav;
    private FavAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        getSupportActionBar().setTitle("My Favourite");

        rv_fav = (RecyclerView) findViewById(R.id.recycler_view_fav);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_fav.setLayoutManager(linearLayoutManager);
        mAdapter = new FavAdapter(FavouriteActivity.this,null,this);
        rv_fav.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onMovieClick(int movieId) {
        Intent i = new Intent(FavouriteActivity.this, DetailMovieActivity.class);
        i.putExtra(DetailMovieActivity.EXTRA_MOVIE_ID, movieId);
        this.startActivity(i);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i == 0) {
            return new CursorLoader(
                    FavouriteActivity.this,
                    Uri.parse("content://"+ getResources().getString(R.string.content_authority) + "/" + MovieDBHelper.TBL_MOVIE_DETAIL
                            + "/FAV/" + 1 ),
                    new String[]{
                            MovieDBHelper.COLUMN_MOVIE_ID,
                            MovieDBHelper.COLUMN_TITLE,
                            MovieDBHelper.COLUMN_BACKDROP_PATH,
                            MovieDBHelper.COLUMN_VOTE_AVERAGE
                    },
                    MovieDBHelper.TBL_MOVIE_DETAIL +  "." + MovieDBHelper.COLUMN_AS_FAVOURITE + " = ?",
                    new String[] {String.valueOf(1)},
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {

        mAdapter.updateList(cursor);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mAdapter.updateList(null);
    }

}
