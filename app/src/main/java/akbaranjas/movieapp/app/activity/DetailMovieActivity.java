package akbaranjas.movieapp.app.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import akbaranjas.movieapp.R;

import akbaranjas.movieapp.app.pojo.detail.DetailMovie;
import akbaranjas.movieapp.app.rest.APIClient;
import akbaranjas.movieapp.app.rest.APIInterface;
import akbaranjas.movieapp.app.url.MovieURL;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailMovieActivity extends AppCompatActivity {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_ID = "movie_id";
    private int movieID;
    private ImageView imgCoverHeader;
    private ImageView imgPosterHeader;
    private TextView tv_yearMin;
    private TextView tvTitle;
    private TextView tvRating;
    private TextView tvDesc;
    private String title = "";
    private RelativeLayout bottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieID = getIntent().getIntExtra(EXTRA_MOVIE_ID, 0 );

        setContentView(R.layout.activity_detail_movie);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        imgCoverHeader = (ImageView) findViewById(R.id.img_header_cover);
        imgPosterHeader = (ImageView) findViewById(R.id.img_poster_header);
        tv_yearMin = (TextView) findViewById(R.id.tv_year_and_min);
        tvTitle = (TextView) findViewById(R.id.tv_title_detail);
        tvRating = (TextView) findViewById(R.id.tv_rating_movie_detail);
        tvDesc = (TextView) findViewById(R.id.tv_desc_detail);
        bottomLayout = (RelativeLayout) findViewById(R.id.loadingLayout);

        this.init();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void init(){
        bottomLayout.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<DetailMovie> call = apiService.getMovieDetails(movieID,MovieURL.APP_KEY_ID);
        call.enqueue(new Callback<DetailMovie>() {
            @Override
            public void onResponse(Call<DetailMovie> call, Response<DetailMovie> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    title = response.body().getTitle();

                    Picasso.with(DetailMovieActivity.this).load(MovieURL.BASE_URL_IMG + MovieURL.SIZE_IMAGE[3] + "/"
                            +  response.body().getBackdropPath()).into(imgCoverHeader);
                    Picasso.with(DetailMovieActivity.this).load(MovieURL.BASE_URL_IMG + MovieURL.SIZE_IMAGE[2] + "/"
                            +  response.body().getPosterPath()).into(imgPosterHeader);
                    tv_yearMin.setText("("+
                            response.body().getReleaseDate().substring(0,4) + ")" + " "
                            + String.valueOf(response.body().getRuntime()) + " min");
                    tvTitle.setText(title);
                    tvRating.setText(String.valueOf(response.body().getVoteAverage()) + "/10");
                    tvDesc.setText(response.body().getOverview());

                    getSupportActionBar().setTitle(title);

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
    }
}
