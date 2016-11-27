package akbaranjas.movieapp.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
        bottomLayout = (RelativeLayout) findViewById(R.id.loadingLayout);

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
                    recyclerView = (RecyclerView)findViewById(R.id.recycler_view_movie);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new GridLayoutManager(getApplicationContext(),2);
                    recyclerView.setLayoutManager(layoutManager);

                    if(movies != null){
                        movies.clear();
                    }

                    page = response.body().getPage() + 1;
                    movies = response.body().getResults();
                    movieGridAdapter = new MovieGridAdapter(movies, R.layout.grid_item_movie, MainActivity.this, MainActivity.this);

                    recyclerView.setAdapter(movieGridAdapter);

                    movieGridAdapter.notifyItemRangeInserted(0, movies.size() - 1);

                    if(scrollListener != null)
                        scrollListener.resetState();

                    scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                            addMovieList();
                        }

                    };
                    recyclerView.addOnScrollListener(scrollListener);
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
        String movieOrder = SP.getString("movieOrder","top_rated");
        if(movieOrder.equalsIgnoreCase("popular")){
            getSupportActionBar().setTitle(R.string.txt_popular);
            order = "popular";
        }else {
            getSupportActionBar().setTitle(R.string.txt_top_rated);
            order = "top_rated";
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
                    page = response.body().getPage() + 1;
                    List<Result> moreMovies = response.body().getResults();
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

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onMovieClick(int movieId) {
        Intent i = new Intent(MainActivity.this, DetailMovieActivity.class);
        i.putExtra(DetailMovieActivity.EXTRA_MOVIE_ID, movieId);
        this.startActivity(i);
    }
}
