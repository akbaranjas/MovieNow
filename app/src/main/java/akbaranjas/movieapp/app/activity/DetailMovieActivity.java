package akbaranjas.movieapp.app.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;
import java.util.StringTokenizer;

import akbaranjas.movieapp.R;

import akbaranjas.movieapp.app.adapter.VideosAdapter;
import akbaranjas.movieapp.app.data.MovieDBHelper;
import akbaranjas.movieapp.app.listener.OnPlayClickListener;
import akbaranjas.movieapp.app.pojo.MovieList;
import akbaranjas.movieapp.app.pojo.Result;
import akbaranjas.movieapp.app.pojo.detail.DetailMovie;
import akbaranjas.movieapp.app.pojo.video.ResultVideo;
import akbaranjas.movieapp.app.pojo.video.Videos;
import akbaranjas.movieapp.app.rest.APIClient;
import akbaranjas.movieapp.app.rest.APIInterface;
import akbaranjas.movieapp.app.url.MovieURL;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailMovieActivity extends AppCompatActivity implements
        OnPlayClickListener , LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = DetailMovieActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_ID = "movie_id";
    public static final int LOADER_DETAIL_MOVIE = 100;
    public static final int LOADER_VIDEOS = 200;
    private int movieID;
    private ImageView imgCoverHeader;
    private ImageView imgPosterHeader;
    private TextView tv_yearMin;
    private TextView tvTitle;
    private TextView tvRating;
    private TextView tvDesc;
    private String title = "";
    private RelativeLayout bottomLayout;
    private static RecyclerView rv_videos;
    private static VideosAdapter videosAdapter;
    private MaterialFavoriteButton btnfav;
    List<ResultVideo> videosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieID = getIntent().getIntExtra(EXTRA_MOVIE_ID, 0 );

        setContentView(R.layout.activity_detail_movie);

        imgCoverHeader = (ImageView) findViewById(R.id.img_header_cover);
        imgPosterHeader = (ImageView) findViewById(R.id.img_poster_header);
        tv_yearMin = (TextView) findViewById(R.id.tv_year_and_min);
        tvTitle = (TextView) findViewById(R.id.tv_title_detail);
        tvRating = (TextView) findViewById(R.id.tv_rating_movie_detail);
        tvDesc = (TextView) findViewById(R.id.tv_desc_detail);
        rv_videos = (RecyclerView) findViewById(R.id.recycler_view_videos);

        btnfav = (MaterialFavoriteButton) findViewById(R.id.btn_add_fav);

        btnfav.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        addFav(favorite);
                    }
                }
        );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_videos.setLayoutManager(linearLayoutManager);
        videosAdapter = new VideosAdapter(this,null,true,this);
        rv_videos.setAdapter(videosAdapter);
        rv_videos.setNestedScrollingEnabled(false);


        bottomLayout = (RelativeLayout) findViewById(R.id.loadingLayout);

        getLoaderManager().initLoader(LOADER_DETAIL_MOVIE, null, this);
        getLoaderManager().initLoader(LOADER_VIDEOS, null, this);


    }

    private void init(){
        bottomLayout.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<DetailMovie> call = apiService.getMovieDetails(movieID,MovieURL.APP_KEY_ID);
        Call<Videos> callVid = apiService.getVideoList(movieID,MovieURL.APP_KEY_ID);
        call.enqueue(new Callback<DetailMovie>() {
            @Override
            public void onResponse(Call<DetailMovie> call, Response<DetailMovie> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    DetailMovie detailMovie = response.body();
                    insertDataDetail(detailMovie);

                    bottomLayout.setVisibility(View.GONE);
                }else{
                    Toast.makeText(DetailMovieActivity.this,"Can't establish the connection, Response Code [" + statusCode + "]",Toast.LENGTH_SHORT).show();
                    bottomLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<DetailMovie> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                Toast.makeText(DetailMovieActivity.this,"Can't establish the connection. .",Toast.LENGTH_SHORT).show();
            }
        });

        //the video time
        callVid.enqueue(new Callback<Videos>() {
            @Override
            public void onResponse(Call<Videos> call, Response<Videos> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    if(videosList != null){
                        videosList.clear();
                    }
                    videosList = response.body().getResults();
                    insertVideos(videosList);

                }else{
                    Toast.makeText(DetailMovieActivity.this,"Can't establish the connection, Response Code [" + statusCode + "]",Toast.LENGTH_SHORT).show();
                    bottomLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<Videos> call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(DetailMovieActivity.this,"Can't establish the connection. .",Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.toString());
            }
        });
    }

    private void insertDataDetail(DetailMovie detailMovie){
        ContentValues cv = new ContentValues();
        cv.put(MovieDBHelper.COLUMN_MOVIE_ID, detailMovie.getId());
        cv.put(MovieDBHelper.COLUMN_OVERVIEW, detailMovie.getOverview());
        cv.put(MovieDBHelper.COLUMN_TITLE, detailMovie.getTitle());
        cv.put(MovieDBHelper.COLUMN_RUNTIME, detailMovie.getRuntime());
        cv.put(MovieDBHelper.COLUMN_RELEASE_DATE, detailMovie.getReleaseDate());
        cv.put(MovieDBHelper.COLUMN_VOTE_AVERAGE, detailMovie.getVoteAverage());
        cv.put(MovieDBHelper.COLUMN_BACKDROP_PATH, detailMovie.getBackdropPath());
        cv.put(MovieDBHelper.COLUMN_POSTER_PATH, detailMovie.getPosterPath());
        cv.put(MovieDBHelper.COLUMN_AS_FAVOURITE, 0);

        Uri uri = Uri.parse("content://"+ getResources().getString(R.string.content_authority) + "/" + MovieDBHelper.TBL_MOVIE_DETAIL
                + "/" + detailMovie.getId());
        Cursor cursor = getContentResolver().query(uri ,
                new String[]{
                        MovieDBHelper.COLUMN_MOVIE_ID,
                        MovieDBHelper.COLUMN_OVERVIEW,
                        MovieDBHelper.COLUMN_TITLE,
                        MovieDBHelper.COLUMN_RUNTIME
                },
                MovieDBHelper.TBL_MOVIE_DETAIL + "." + MovieDBHelper.COLUMN_MOVIE_ID + " = ? ",
                new String[]{String.valueOf(detailMovie.getId())},
                null
                );
        if(cursor.getCount() == 0) {
            getContentResolver().insert(
                    uri,
                    cv);
            getContentResolver().notifyChange(uri, null);
        }
    }

    private void insertVideos(List<ResultVideo> videos){
        ContentValues[] contentValues = new ContentValues[videos.size()];
        for(int i = 0;i < videos.size(); i++){
            ContentValues cv = new ContentValues();
            cv.put(MovieDBHelper.COLUMN_MOVIE_ID, movieID);
            cv.put(MovieDBHelper.COLUMN_KEY_VIDEOS, videos.get(i).getKey());
            cv.put(MovieDBHelper.COLUMN_NAME_VIDEOS, videos.get(i).getName());
            contentValues[i] = cv;
        }

        Uri uri = Uri.parse("content://"+ getResources().getString(R.string.content_authority) + "/" + MovieDBHelper.TBL_VIDEOS
                + "/" + movieID);
        getContentResolver().delete(
                uri,
                MovieDBHelper.TBL_VIDEOS + "." + MovieDBHelper.COLUMN_MOVIE_ID
                        + " = ?"
                ,
                new String[] {String.valueOf(movieID)}
        );
        getContentResolver().bulkInsert(
                uri,
                contentValues);
        getContentResolver().notifyChange(uri, null);

    }

    @Override
    public void onYoutubeClick(String videosID) {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, MovieURL.API_KEY_GOOGLE, videosID);
        DetailMovieActivity.this
                .startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i == LOADER_DETAIL_MOVIE) {
            return new CursorLoader(
                    DetailMovieActivity.this,
                    Uri.parse("content://"+ getResources().getString(R.string.content_authority) + "/" + MovieDBHelper.TBL_MOVIE_DETAIL
                            + "/" + movieID ),
                    new String[]{
                            MovieDBHelper.COLUMN_MOVIE_ID,
                            MovieDBHelper.COLUMN_OVERVIEW,
                            MovieDBHelper.COLUMN_TITLE,
                            MovieDBHelper.COLUMN_RUNTIME,
                            MovieDBHelper.COLUMN_RELEASE_DATE,
                            MovieDBHelper.COLUMN_VOTE_AVERAGE,
                            MovieDBHelper.COLUMN_OVERVIEW,
                            MovieDBHelper.COLUMN_BACKDROP_PATH,
                            MovieDBHelper.COLUMN_POSTER_PATH,
                            MovieDBHelper.COLUMN_AS_FAVOURITE
                    },
                    MovieDBHelper.TBL_MOVIE_DETAIL +  "." + MovieDBHelper.COLUMN_MOVIE_ID + " = ?",
                    new String[] {String.valueOf(movieID)},
                    null
            );
        }else if (i == LOADER_VIDEOS){
            return new CursorLoader(
                    DetailMovieActivity.this,
                    Uri.parse("content://"+ getResources().getString(R.string.content_authority) + "/" + MovieDBHelper.TBL_VIDEOS
                            + "/" + movieID ),
                    new String[]{
                            MovieDBHelper.COLUMN_MOVIE_ID,
                            MovieDBHelper.COLUMN_KEY_VIDEOS,
                            MovieDBHelper.COLUMN_NAME_VIDEOS,

                    },
                    MovieDBHelper.TBL_VIDEOS + "." +  MovieDBHelper.COLUMN_MOVIE_ID + " = ?",
                    new String[] {String.valueOf(movieID)},
                    null
            );

           }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(loader.getId() == LOADER_DETAIL_MOVIE) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                title = cursor.getString(2);
                Picasso.with(DetailMovieActivity.this).load(MovieURL.BASE_URL_IMG + MovieURL.SIZE_IMAGE[3] + "/"
                        + cursor.getString(7)).into(imgCoverHeader);
                Picasso.with(DetailMovieActivity.this).load(MovieURL.BASE_URL_IMG + MovieURL.SIZE_IMAGE[2] + "/"
                        + cursor.getString(8)).into(imgPosterHeader);
                tv_yearMin.setText("(" +
                        cursor.getString(4).substring(0, 4) + ")" + " "
                        + String.valueOf(3) + " min");
                tvTitle.setText(title);
                tvRating.setText(String.valueOf(cursor.getDouble(5)) + "/10");
                tvDesc.setText(cursor.getString(1));
                if(cursor.getInt(9) == 1){
                    btnfav.setFavorite(true);
                }else{
                    btnfav.setFavorite(false);
                }

                getSupportActionBar().setTitle(title);
            }else{
                init();
            }
        }else if(loader.getId() == LOADER_VIDEOS){
            videosAdapter.updateList(cursor);
            videosAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_VIDEOS){
            videosAdapter.updateList(null);
        }
    }

    private void addFav(boolean state){
        Uri uri = Uri.parse("content://"+ getResources().getString(R.string.content_authority) + "/" + MovieDBHelper.TBL_MOVIE_DETAIL
                + "/" + movieID);
        Cursor cursor = getContentResolver().query(uri ,
                new String[]{
                        MovieDBHelper.COLUMN_AS_FAVOURITE
                },
                MovieDBHelper.TBL_MOVIE_DETAIL + "." + MovieDBHelper.COLUMN_MOVIE_ID + " = ? ",
                new String[]{String.valueOf(movieID)},
                null
        );
        ContentValues cv = new ContentValues();

        if(cursor.getCount() > 0) {
            if(state){
                cv.put(MovieDBHelper.COLUMN_AS_FAVOURITE , 1);
            }else{
                cv.put(MovieDBHelper.COLUMN_AS_FAVOURITE , 0);
            }
            getContentResolver().update(uri ,
                    cv,
                    MovieDBHelper.TBL_MOVIE_DETAIL + "." + MovieDBHelper.COLUMN_MOVIE_ID + " = ? ",
                    new String[]{String.valueOf(movieID)}
            );
            getContentResolver().notifyChange(uri, null);
        }

    }
}
