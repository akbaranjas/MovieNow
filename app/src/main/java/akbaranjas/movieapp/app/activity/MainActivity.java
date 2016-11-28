package akbaranjas.movieapp.app.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import akbaranjas.movieapp.R;
import akbaranjas.movieapp.app.adapter.EndlessRecyclerViewScrollListener;
import akbaranjas.movieapp.app.adapter.MovieGridAdapter;
import akbaranjas.movieapp.app.data.MovieDBHelper;
import akbaranjas.movieapp.app.listener.OnMovieClickListener;
import akbaranjas.movieapp.app.pojo.MovieList;
import akbaranjas.movieapp.app.pojo.Result;
import akbaranjas.movieapp.app.rest.APIClient;
import akbaranjas.movieapp.app.rest.APIInterface;
import akbaranjas.movieapp.app.url.MovieURL;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMovieClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static RecyclerView recyclerView;
    List<Result> movies;
    int page = 1;
    String order = "";
    private static MovieGridAdapter movieGridAdapter;
    private static EndlessRecyclerViewScrollListener scrollListener;
    private static LinearLayoutManager layoutManager;
    private static RelativeLayout bottomLayout;
    private String tempPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_movie);
        bottomLayout = (RelativeLayout) findViewById(R.id.loadingLayout);

        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        movieGridAdapter = new MovieGridAdapter(null, MainActivity.this, MainActivity.this);
        recyclerView.setAdapter(movieGridAdapter);

        updateGrid();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String movieOrder = SP.getString("movieOrder","popular");
        if(!movieOrder.equalsIgnoreCase(order)){updateGrid();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, PrefActivity.class);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }


    private void init(String order){
        bottomLayout.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<MovieList> call = apiService.getMovieLists(order, 1 , MovieURL.APP_KEY_ID);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                int statusCode = response.code();
                if(statusCode==200) {

                    if(movies != null){
                        movies.clear();
                    }

                    movies = response.body().getResults();

                    movieGridAdapter.updateList(movies);
                    //movieGridAdapter.notifyItemRangeInserted(0, movies.size() - 1);
                    movieGridAdapter.notifyDataSetChanged();

                    insertData(movies, page);
                    page = response.body().getPage() + 1;

                    bottomLayout.setVisibility(View.GONE);
                }else{
                    Toast.makeText(MainActivity.this,"Can't establish the connection, Response Code [" + statusCode + "]",Toast.LENGTH_SHORT).show();
                    bottomLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(MainActivity.this,"Can't establish the connection. .",Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.toString());
            }
        });
    }

    private void updateGrid(){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String movieOrder = SP.getString("movieOrder","popular");
        if(movieOrder.equalsIgnoreCase("popular")){
            getSupportActionBar().setTitle(R.string.txt_popular);
            order = "popular";
        }else {
            getSupportActionBar().setTitle(R.string.txt_top_rated);
            order = "top_rated";
        }

        if(scrollListener != null)
            scrollListener.resetState();

        if(scrollListener == null) {
            scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                    addMovieList();
                }

            };

            recyclerView.addOnScrollListener(scrollListener);
        }
        init(order);

    }

    private void addMovieList(){
        bottomLayout.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);

        Call<MovieList> call = apiService.getMovieLists(order, page, MovieURL.APP_KEY_ID);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    List<Result> moreMovies = response.body().getResults();
                    insertData(moreMovies, page);
                    page = response.body().getPage() + 1;
                    int curSize = movieGridAdapter.getItemCount();
                    movies.addAll(moreMovies);

                    movieGridAdapter.notifyItemRangeInserted(curSize, movies.size() - 1);
                    //movieGridAdapter.notifyDataSetChanged();
                    bottomLayout.setVisibility(View.GONE);

                }else{
                    Toast.makeText(MainActivity.this,"Can't establish the connection, Response Code [" + statusCode + "]",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(MainActivity.this,"Can't establish the connection. .",Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onMovieClick(int movieId) {
        Intent i = new Intent(MainActivity.this, DetailMovieActivity.class);
        i.putExtra(DetailMovieActivity.EXTRA_MOVIE_ID, movieId);
        this.startActivity(i);
    }

    private void insertData(List<Result> movies, int page){
        ContentValues[] contentValues = new ContentValues[movies.size()];
        for(int i = 0;i < movies.size(); i++){
            ContentValues cv = new ContentValues();
            cv.put(MovieDBHelper.COLUMN_MOVIE_ID, movies.get(i).getId());
            cv.put(MovieDBHelper.COLUMN_OVERVIEW, movies.get(i).getOverview());
            cv.put(MovieDBHelper.COLUMN_TITLE, movies.get(i).getTitle());
            cv.put(MovieDBHelper.COLUMN_POSTER_PATH, movies.get(i).getPosterPath());
            cv.put(MovieDBHelper.COLUMN_VOTE_AVERAGE, movies.get(i).getVoteAverage());
            cv.put(MovieDBHelper.COLUMN_PAGE, page);
            contentValues[i] = cv;
        }

        Uri uri = Uri.parse("content://"+ getResources().getString(R.string.content_authority) + "/" + MovieDBHelper.TBL_MOVIE
                + "/" + page);
        getContentResolver().delete(
                uri,
                MovieDBHelper.TBL_MOVIE + "." + MovieDBHelper.COLUMN_PAGE
                        + " = ?"
                ,
                new String[] {String.valueOf(page)}
        );
        getContentResolver().bulkInsert(
                uri,
                contentValues);
        getContentResolver().notifyChange(uri, null);

    }
}
