package akbaranjas.movieapp.app.rest;

import akbaranjas.movieapp.app.pojo.MovieList;
import akbaranjas.movieapp.app.pojo.detail.DetailMovie;
import akbaranjas.movieapp.app.pojo.video.Videos;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by akbaranjas on 22/11/16.
 */

public interface APIInterface {
    @GET("{param}")
    Call<MovieList> getMovieLists(@Path("param") String param,@Query("page") int page,@Query("api_key") String apiKey);

    @GET("{id}")
    Call<DetailMovie> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("{id}/videos")
    Call<Videos> getVideoList(@Path("id") int id, @Query("api_key") String apiKey);
}
